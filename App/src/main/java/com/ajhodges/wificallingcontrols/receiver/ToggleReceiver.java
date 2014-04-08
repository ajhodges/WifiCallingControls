package com.ajhodges.wificallingcontrols.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ajhodges.wificallingcontrols.ipphone.WifiCallingManager;
import com.ajhodges.wificallingcontrols.bundle.BundleScrubber;
import com.ajhodges.wificallingcontrols.bundle.PluginBundleManager;
import com.ajhodges.wificallingcontrols.widget.ToggleWidgetProvider;

/**
 * Created by Adam on 3/9/14.
 */
public class ToggleReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())){
            //unexpected intent
            return;
        }
        BundleScrubber.scrub(intent);

        final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(bundle);

        if (PluginBundleManager.isBundleValid(bundle))
        {
            final int mode = bundle.getInt(PluginBundleManager.BUNDLE_EXTRA_INT_MODE);

            WifiCallingManager wifiCallingManager = WifiCallingManager.getInstance(context);
            wifiCallingManager.toggleWifi(context, mode);

            ToggleWidgetProvider.widgetUpdating = false;
        }
    }
}
