package com.ajhodges.wifitoggle.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.ajhodges.wifitoggle.Constants;
import com.ajhodges.wifitoggle.R;
import com.ajhodges.wifitoggle.ipphone.WifiCallingManager;
import com.ajhodges.wifitoggle.bundle.PluginBundleManager;

/**
 * Created by Adam on 3/13/14.
 */
public class ToggleWidgetProvider extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context){
        super.onEnabled(context);

        Log.v(Constants.LOG_TAG, "Widget added");

        //only create/register one listener for all widgets
        WifiCallingManager.getInstance(context).registerListener(ToggleWifiCallingListener.getWifiCallingListenerInstance());
    }

    @Override
    public void onDisabled(Context context){
        super.onDisabled(context);

        Log.v(Constants.LOG_TAG, "Last widget removed, unregistering listener");

        //unregister listener when last widget removed
        WifiCallingManager.getInstance(context).unregisterListener(ToggleWifiCallingListener.getWifiCallingListenerInstance());
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        super.onUpdate(context,appWidgetManager,appWidgetIds);

        //Called once per widget to initialize the state
        Log.v(Constants.LOG_TAG, "onUpdate: initializing widget state");

        //get the current Wifi Calling state
        boolean ipphoneEnabled = false;

        ipphoneEnabled = WifiCallingManager.getInstance(context).getIPPhoneEnabled(context);


        for(int i : appWidgetIds){
            //set onClick to toggle the Wifi Calling state
            Intent intent = new Intent();
            intent.setAction(com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING);

            final Bundle resultBundle = PluginBundleManager.generateBundle(context, WifiCallingManager.MODE_TOGGLE);
            intent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
            PendingIntent pendingIntent=PendingIntent.getBroadcast(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.toggle_button, pendingIntent);

            //set the widget background to reflect the current Wifi Calling State
            views.setImageViewResource(R.id.toggle_button, (ipphoneEnabled ? R.drawable.toggle_on : R.drawable.toggle_off));

            appWidgetManager.updateAppWidget(i, views);
        }
    }


}
