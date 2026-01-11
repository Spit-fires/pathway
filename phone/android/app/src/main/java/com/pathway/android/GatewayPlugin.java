package com.pathway.android;

import android.Manifest;
import android.content.Context;
import android.content.ComponentName;
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
    private android.net.wifi.WifiManager.WifiLock wifiLock;
    private android.os.PowerManager.WakeLock wakeLock;

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
            
            // Acquire WiFi lock to prevent WiFi from going to sleep
            acquireWifiLock();
            
            // Acquire partial wake lock to keep CPU running
            acquireWakeLock();
            
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
        
        // Release locks
        releaseWifiLock();
        releaseWakeLock();
        
        JSObject ret = new JSObject();
        ret.put("success", true);
        ret.put("message", "Server stopped");
        call.resolve(ret);
    }
    
    private void acquireWifiLock() {
        if (wifiLock == null) {
            WifiManager wm = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm == null) {
                notifyLog("WiFi manager not available");
                return;
            }
            wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "Pathway:WifiLock");
            wifiLock.setReferenceCounted(false);
        }
        if (!wifiLock.isHeld()) {
            wifiLock.acquire();
            notifyLog("WiFi lock acquired");
        }
    }
    
    private void releaseWifiLock() {
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
            notifyLog("WiFi lock released");
        }
    }
    
    private void acquireWakeLock() {
        if (wakeLock == null) {
            android.os.PowerManager pm = (android.os.PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(android.os.PowerManager.PARTIAL_WAKE_LOCK, "Pathway:WakeLock");
            wakeLock.setReferenceCounted(false);
        }
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
            notifyLog("Wake lock acquired");
        }
    }
    
    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            notifyLog("Wake lock released");
        }
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
        // Release locks before exit
        releaseWifiLock();
        releaseWakeLock();
        
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
    
    @PluginMethod
    public void openOemBatterySettings(PluginCall call) {
        // Try to open OEM-specific battery optimization settings
        // This handles Realme, Xiaomi (MIUI), OPPO (ColorOS), OnePlus (OxygenOS), Samsung, Huawei (EMUI), Vivo
        String manufacturer = android.os.Build.MANUFACTURER.toLowerCase();
        
        // Define potential intents for OPPO/Realme (they have multiple possible package names)
        String[][] oppoRealmeIntents = {
            {"com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"},
            {"com.oplus.safecenter", "com.oplus.safecenter.permission.startup.StartupAppListActivity"}
        };
        
        try {
            android.content.Intent intent = new android.content.Intent();
            boolean useOppoFallback = false;
            
            switch (manufacturer) {
                case "xiaomi":
                case "redmi":
                    // MIUI Battery Saver / Autostart
                    intent.setComponent(new ComponentName("com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    break;
                    
                case "oppo":
                case "realme":
                    // ColorOS / RealmeUI Battery Optimization - try multiple package names
                    useOppoFallback = true;
                    break;
                    
                case "vivo":
                    // Funtouch OS
                    intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                            "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                    break;
                    
                case "huawei":
                case "honor":
                    // EMUI
                    intent.setComponent(new ComponentName("com.huawei.systemmanager",
                            "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"));
                    break;
                    
                case "samsung":
                    // OneUI / TouchWiz
                    intent.setComponent(new ComponentName("com.samsung.android.lool",
                            "com.samsung.android.sm.ui.battery.BatteryActivity"));
                    break;
                    
                case "oneplus":
                    // OxygenOS
                    intent.setComponent(new ComponentName("com.oneplus.security",
                            "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"));
                    break;
                    
                case "asus":
                    // ZenUI
                    intent.setComponent(new ComponentName("com.asus.mobilemanager",
                            "com.asus.mobilemanager.autostart.AutoStartActivity"));
                    break;
                    
                default:
                    // Fallback to standard Android battery settings
                    intent.setAction(android.provider.Settings.ACTION_BATTERY_SAVER_SETTINGS);
                    break;
            }
            
            // Special handling for OPPO/Realme with multiple possible intents
            if (useOppoFallback) {
                for (String[] intentData : oppoRealmeIntents) {
                    try {
                        android.content.Intent oppoIntent = new android.content.Intent();
                        oppoIntent.setComponent(new ComponentName(intentData[0], intentData[1]));
                        oppoIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(oppoIntent);
                        
                        JSObject ret = new JSObject();
                        ret.put("manufacturer", manufacturer);
                        ret.put("opened", true);
                        call.resolve(ret);
                        return;
                    } catch (android.content.ActivityNotFoundException ignored) {
                        // Try next intent
                    }
                }
                // If none worked, throw to trigger fallback
                throw new android.content.ActivityNotFoundException("No OPPO/Realme battery settings found");
            }
            
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            
            JSObject ret = new JSObject();
            ret.put("manufacturer", manufacturer);
            ret.put("opened", true);
            call.resolve(ret);
            
        } catch (android.content.ActivityNotFoundException e) {
            // Fallback to standard battery settings if OEM-specific fails
            try {
                android.content.Intent fallbackIntent = new android.content.Intent(android.provider.Settings.ACTION_BATTERY_SAVER_SETTINGS);
                fallbackIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(fallbackIntent);
                
                JSObject ret = new JSObject();
                ret.put("manufacturer", manufacturer);
                ret.put("opened", true);
                ret.put("fallback", true);
                call.resolve(ret);
            } catch (Exception e2) {
                call.reject("Unable to open battery settings: " + e2.getMessage());
            }
        } catch (Exception e) {
            call.reject("Unable to open battery settings: " + e.getMessage());
        }
    }
    
    @PluginMethod
    public void getDeviceInfo(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("manufacturer", android.os.Build.MANUFACTURER);
        ret.put("model", android.os.Build.MODEL);
        ret.put("brand", android.os.Build.BRAND);
        ret.put("device", android.os.Build.DEVICE);
        ret.put("sdkVersion", android.os.Build.VERSION.SDK_INT);
        call.resolve(ret);
    }
}
