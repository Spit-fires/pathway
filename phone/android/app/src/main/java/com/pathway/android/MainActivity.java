package com.pathway.android;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(GatewayPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
