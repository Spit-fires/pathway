package com.pathway.android;

import fi.iki.elonen.NanoHTTPD;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.PendingIntent;
import android.os.Handler;
import android.os.Looper;
import android.os.Build;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicReference;
import org.json.JSONObject;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionInfo;
import java.util.List;

public class ApiServer extends NanoHTTPD {
    private String apiKey;
    private Context context;
    private LogCallback logCallback;
    private final ReentrantLock lock = new ReentrantLock();

    public interface LogCallback {
        void log(String message);
    }

    public ApiServer(int port, String apiKey, Context context, LogCallback logCallback) {
        super(port);
        this.apiKey = apiKey;
        this.context = context;
        this.logCallback = logCallback;
    }

    private void log(String msg) {
        if (logCallback != null) {
            logCallback.log(msg);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        // Handle CORS Preflight
        if (Method.OPTIONS.equals(session.getMethod())) {
            Response r = newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "OK");
            addCORSHeaders(r);
            return r;
        }

        Response response = handleRequest(session);
        addCORSHeaders(response);
        return response;
    }

    private void addCORSHeaders(Response r) {
        r.addHeader("Access-Control-Allow-Origin", "*");
        r.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        r.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
    }

    private Response handleRequest(IHTTPSession session) {
        // Health check / Ping (No auth required for basic connectivity check)
        if (Method.GET.equals(session.getMethod()) && "/".equals(session.getUri())) {
            return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\": \"online\"}");
        }

        Map<String, String> headers = session.getHeaders();
        String authHeader = headers.get("authorization");
        if (authHeader == null) authHeader = headers.get("Authorization");

        if (authHeader == null || !authHeader.equals("Bearer " + apiKey)) {
             return newFixedLengthResponse(Response.Status.UNAUTHORIZED, MIME_PLAINTEXT, "Unauthorized");
        }

        if (Method.POST.equals(session.getMethod())) {
            // Lock to ensure serial processing
            boolean acquired = false;
            try {
                acquired = lock.tryLock(40, TimeUnit.SECONDS); // Wait max 40s (longer than operation timeout)
                if (!acquired) {
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Server busy");
                }
                
                // Parse Body
                Map<String, String> files = new HashMap<>();
                session.parseBody(files);
                String postBody = files.get("postData");
                JSONObject json = new JSONObject(postBody);

                if ("/sms".equals(session.getUri())) {
                    return handleSms(json);
                } else if ("/ussd".equals(session.getUri())) {
                    return handleUssd(json);
                }

            } catch (Exception e) {
                log("Error: " + e.getMessage());
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error: " + e.getMessage());
            } finally {
                if (acquired) {
                    lock.unlock();
                }
            }
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
    }

    private Response handleSms(JSONObject json) throws InterruptedException {
        String number = json.optString("number");
        String message = json.optString("message");
        int simSlot = json.optInt("sim", -1);

        if (number.isEmpty() || message.isEmpty()) {
            return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\": \"failed\", \"reason\": \"Missing number or message\"}");
        }

        log("Sending SMS to " + number + (simSlot != -1 ? " on SIM " + simSlot : ""));
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> resultStatus = new AtomicReference<>("unknown");
        
        String SENT = "SMS_SENT_" + System.currentTimeMillis();
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), PendingIntent.FLAG_IMMUTABLE);

        BroadcastReceiver sentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case android.app.Activity.RESULT_OK:
                        resultStatus.set("sent");
                        log("SMS Sent Successfully");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        resultStatus.set("generic_failure");
                        log("SMS Generic Failure");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        resultStatus.set("no_service");
                        log("SMS No Service");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        resultStatus.set("null_pdu");
                        log("SMS Null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        resultStatus.set("radio_off");
                        log("SMS Radio Off");
                        break;
                }
                latch.countDown();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
             context.registerReceiver(sentReceiver, new IntentFilter(SENT), Context.RECEIVER_EXPORTED);
        } else {
             context.registerReceiver(sentReceiver, new IntentFilter(SENT));
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            
            // Handle Multi-SIM
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                 android.telephony.SubscriptionManager sm = (android.telephony.SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                 if (sm != null) {
                     if (context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                         java.util.List<android.telephony.SubscriptionInfo> subs = sm.getActiveSubscriptionInfoList();
                         if (subs != null && !subs.isEmpty()) {
                             int subId = -1;
                             if (simSlot != -1) {
                                 for (android.telephony.SubscriptionInfo info : subs) {
                                     if (info.getSimSlotIndex() == simSlot) {
                                         subId = info.getSubscriptionId();
                                         break;
                                     }
                                 }
                             } else {
                                 // If no Sim specified but we are in this block, we could default to first? 
                                 // But SmsManager.getDefault() handles default ID. 
                                 // So only override if simSlot is specific found OR we want to be explicit.
                                 // For now only override if subId found from slot.
                             }
                             
                             if (subId != -1) {
                                 smsManager = SmsManager.getSmsManagerForSubscriptionId(subId);
                                 log("Using SMS Subscription ID: " + subId);
                             }
                         }
                     }
                 }
            }

            smsManager.sendTextMessage(number, null, message, sentPI, null);
            
            boolean finished = latch.await(15, TimeUnit.SECONDS);
            if (!finished) {
                resultStatus.set("timeout");
                log("SMS Timed out waiting for carrier response");
            }
        } catch (Exception e) {
            resultStatus.set("error_" + e.getMessage());
            log("SMS Exception: " + e.getMessage());
        } finally {
            context.unregisterReceiver(sentReceiver);
        }
        
        String finalStatus = resultStatus.get();
        if ("sent".equals(finalStatus)) {
             return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\": \"sent\"}");
        } else {
             // Always return HTTP 200 - use JSON body for status indication
             return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\": \"failed\", \"reason\": \"" + finalStatus + "\"}");
        }
    }
    
    private Response handleUssd(JSONObject json) throws InterruptedException {
        String code = json.optString("code");
        
         if (code.isEmpty()) {
            return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\": \"failed\", \"reason\": \"Missing code\"}");
        }
        
        // Get Global Preference
        android.content.SharedPreferences prefs = context.getSharedPreferences("GatewayConfig", Context.MODE_PRIVATE);
        int simSlot = prefs.getInt("preferred_sim", -1);
        
        log("Running USSD: " + code + (simSlot != -1 ? " on SIM " + simSlot : ""));

        // USSD requires Main Thread to initiate, but we need to block this background thread until response
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> resultRef = new AtomicReference<>(null);
        AtomicReference<String> errorRef = new AtomicReference<>(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                 try {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    
                    // Handle Multi-SIM / Subscription selection
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                         android.telephony.SubscriptionManager sm = (android.telephony.SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                         if (sm != null) {
                             if (context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                 java.util.List<android.telephony.SubscriptionInfo> subs = sm.getActiveSubscriptionInfoList();
                                 if (subs != null && !subs.isEmpty()) {
                                     // Default to first SIM, or find requested slot
                                     int subId = subs.get(0).getSubscriptionId();
                                     if (simSlot != -1) {
                                         for (android.telephony.SubscriptionInfo info : subs) {
                                             if (info.getSimSlotIndex() == simSlot) {
                                                 subId = info.getSubscriptionId();
                                                 break;
                                             }
                                         }
                                     }
                                     tm = tm.createForSubscriptionId(subId);
                                     log("Using Subscription ID: " + subId);
                                 }
                             }
                         }
                    }

                    tm.sendUssdRequest(code, new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);
                            String res = response != null ? response.toString() : "Success";
                            resultRef.set(res);
                            log("USSD Response: " + res);
                            latch.countDown();
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                            super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                             String errorMsg;
                            if (failureCode == -1) { // TelephonyManager.USSD_RETURN_FAILURE
                                errorMsg = "USSD Failed: Network rejected request (-1)";
                            } else if (failureCode == -2) { // TelephonyManager.USSD_ERROR_SERVICE_UNAVAIL
                                errorMsg = "USSD Failed: Service unavailable (-2)";
                            } else {
                                errorMsg = "USSD Failed: code " + failureCode;
                            }
                            errorRef.set(errorMsg);
                            log(errorMsg);
                            latch.countDown();
                        }
                    }, handler);
                 } catch (SecurityException e) {
                     errorRef.set("Security Exception: " + e.getMessage());
                     log("USSD Security Exception");
                     latch.countDown();
                 } catch (Exception e) {
                     errorRef.set("Exception: " + e.getMessage());
                     log("USSD Exception: " + e.getMessage());
                     latch.countDown();
                 }
            });
            
            boolean finished = latch.await(45, TimeUnit.SECONDS); // Extended timeout for USSD
            if (!finished) {
                 return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\": \"failed\", \"reason\": \"timeout\"}");
            }
            
            if (errorRef.get() != null) {
                // Always return HTTP 200 - use JSON body for status indication
                String escapedError = errorRef.get().replace("\\", "\\\\").replace("\"", "\\\"");
                return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\": \"failed\", \"reason\": \"" + escapedError + "\"}");
            }
            
            // Success - JSONObject.quote() already wraps the result in quotes, so don't add extra quotes
            String result = resultRef.get();
            String escapedResult = result != null ? result.replace("\\", "\\\\").replace("\"", "\\\"") : "";
             return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\": \"success\", \"result\": \"" + escapedResult + "\"}");

        } else {
             return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\": \"failed\", \"reason\": \"Android 8.0+ required for USSD\"}");
        }
    }
}
