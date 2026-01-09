package com.pathway.android;

import android.Manifest;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import java.io.IOException;

@CapacitorPlugin(
    name = "Gateway",
    permissions = {
        @Permission(alias = "sms", strings = { Manifest.permission.SEND_SMS }),
        @Permission(alias = "phone", strings = { Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE }),
        @Permission(alias = "internet", strings = { Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE })
    }
)
public class GatewayPlugin extends Plugin {
    private ApiServer server;

    private void notifyLog(String message) {
        JSObject ret = new JSObject();
        ret.put("message", message);
        notifyListeners("log", ret);
    }

    @PluginMethod
    public void startServer(PluginCall call) {
        int port = call.getInt("port", 8080);
        String apiKey = call.getString("apiKey");

        if (apiKey == null) {
            call.reject("API Key required");
            return;
        }

        if (server != null && server.isAlive()) {
             server.stop();
        }

        try {
            server = new ApiServer(port, apiKey, getContext(), this::notifyLog);
            server.start();
            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("message", "Server started on port " + port);
            call.resolve(ret);
        } catch (IOException e) {
            call.reject("Failed to start server: " + e.getMessage());
        }
    }

    @PluginMethod
    public void stopServer(PluginCall call) {
        if (server != null) {
            server.stop();
            server = null;
        }
        JSObject ret = new JSObject();
        ret.put("success", true);
        ret.put("message", "Server stopped");
        call.resolve(ret);
    }
    
    @PluginMethod
    public void getLocalIpAddress(PluginCall call) {
        // Need ACCESS_WIFI_STATE
        Context context = getContext();
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        
        JSObject ret = new JSObject();
        ret.put("ip", ip);
        call.resolve(ret);
    }
    
    @PluginMethod
    public void getSims(PluginCall call) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            android.telephony.SubscriptionManager sm = (android.telephony.SubscriptionManager) getContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (sm != null) {
                if (getContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    java.util.List<android.telephony.SubscriptionInfo> subs = sm.getActiveSubscriptionInfoList();
                    com.getcapacitor.JSArray ret = new com.getcapacitor.JSArray();
                    if (subs != null) {
                        for (android.telephony.SubscriptionInfo info : subs) {
                            JSObject sim = new JSObject();
                            sim.put("slot", info.getSimSlotIndex());
                            sim.put("subscriptionId", info.getSubscriptionId());
                            sim.put("carrierName", info.getCarrierName());
                            sim.put("number", info.getNumber()); // May be null/empty
                            sim.put("displayName", info.getDisplayName());
                            ret.put(sim);
                        }
                    }
                    JSObject response = new JSObject();
                    response.put("sims", ret);
                    call.resolve(response);
                    return;
                }
            }
        }
        call.reject("Unable to get SIMs");
    }

    @PluginMethod
    public void setPreferredSim(PluginCall call) {
        int slot = call.getInt("slot", -1);
        android.content.SharedPreferences prefs = getContext().getSharedPreferences("GatewayConfig", Context.MODE_PRIVATE);
        prefs.edit().putInt("preferred_sim", slot).apply();
        call.resolve();
    }

    @PluginMethod
    public void getPreferredSim(PluginCall call) {
        android.content.SharedPreferences prefs = getContext().getSharedPreferences("GatewayConfig", Context.MODE_PRIVATE);
        int slot = prefs.getInt("preferred_sim", -1); // -1 = Auto
        JSObject ret = new JSObject();
        ret.put("slot", slot);
        call.resolve(ret);
    }

    @PluginMethod
    public void setKeepScreenOn(PluginCall call) {
        boolean on = call.getBoolean("on", false);
        getBridge().executeOnMainThread(() -> {
            if (on) {
                getActivity().getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                getActivity().getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            call.resolve();
        });
    }

    @PluginMethod
    public void exitApp(PluginCall call) {
        getBridge().executeOnMainThread(() -> {
            getActivity().finishAffinity();
            System.exit(0);
        });
        call.resolve();
    }

    @PluginMethod
    public void requestBatteryOpt(PluginCall call) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            String packageName = getContext().getPackageName();
            android.os.PowerManager pm = (android.os.PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                android.content.Intent intent = new android.content.Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(android.net.Uri.parse("package:" + packageName));
                getContext().startActivity(intent);
                call.resolve();
            } else {
                call.resolve(new JSObject().put("status", "already_ignoring"));
            }
        } else {
            call.resolve();
        }
    }
}
