package com.ajhodges.wifitoggle.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ajhodges.wifitoggle.Constants;
import com.ajhodges.wifitoggle.bundle.BundleScrubber;
import com.ajhodges.wifitoggle.bundle.PluginBundleManager;
import com.ajhodges.wifitoggle.ipphone.WifiCallingManager;

/**
 * Created by Adam on 3/25/2014.
 */
public class QueryReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if (com.twofortyfouram.locale.Intent.ACTION_QUERY_CONDITION.equals(intent.getAction())){
            Log.v(Constants.LOG_TAG, "Query from Locale!");
            BundleScrubber.scrub(intent);
            final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
            BundleScrubber.scrub(bundle);

            if(PluginBundleManager.isBundleValid(bundle)){
                final Boolean ipphoneEnabled = WifiCallingManager.getInstance(context).getIPPhoneEnabled(context);
                final boolean conditionState = bundle.getInt(PluginBundleManager.BUNDLE_EXTRA_INT_MODE) == 1;
                if(ipphoneEnabled){
                    if(conditionState){
                        setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED);
                    }else{
                        setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_UNSATISFIED);
                    }
                } else{
                    if(conditionState){
                        setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_UNSATISFIED);
                    }else{
                        setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED);
                    }
                }
            }
        }
    }
}
