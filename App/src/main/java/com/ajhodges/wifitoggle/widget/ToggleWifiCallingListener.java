package com.ajhodges.wifitoggle.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.ajhodges.wifitoggle.Constants;
import com.ajhodges.wifitoggle.R;
import com.ajhodges.wifitoggle.ipphone.WifiCallingListener;
import com.ajhodges.wifitoggle.ipphone.WifiCallingManager;


/**
 * Created by Adam on 3/15/14.
 */
public class ToggleWifiCallingListener implements WifiCallingListener {
    private static WifiCallingListener wifiCallingListener = null;
    public static WifiCallingListener getWifiCallingListenerInstance(){
        //only create one listener for all widgets
        if(wifiCallingListener == null){
            wifiCallingListener = new ToggleWifiCallingListener();

        }
        return wifiCallingListener;
    }

    @Override
    public void onWifiCallingStateChanged(Context context) {
        Log.v(Constants.LOG_TAG, "onWifiCallingStateChanged: updating widgets...");
        //get widget views
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        //set current widget icon to reflect the state Wifi Calling
        boolean ipphoneEnabled = WifiCallingManager.getInstance(context).getIPPhoneEnabled(context);
        views.setImageViewResource(R.id.toggle_button, (ipphoneEnabled ? R.drawable.toggle_on : R.drawable.toggle_off));
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(appWidgetManager.getAppWidgetIds(new ComponentName(context, ToggleWidgetProvider.class)), views);
    }
}