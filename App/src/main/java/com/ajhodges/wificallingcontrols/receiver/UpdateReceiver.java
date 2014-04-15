package com.ajhodges.wificallingcontrols.receiver;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.ajhodges.wificallingcontrols.ui.EditConditionActivity;
import com.ajhodges.wificallingcontrols.widget.ToggleWidgetProvider;

/**
 * Created by Adam on 3/25/14.
 */
public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /* Intent com.movial.IMS_REGISTRATION is received when:
        * Wifi calling is disabled (REG_STATUS == false)
        * Wifi calling is being enabled (REG_STATUS == false)
        * Wifi calling is enabled (REG_STATUS == true)
         */
        //Don't need to update in the third case
        if(intent.getAction().equals("com.movial.IMS_REGISTRATION") && intent.getBooleanExtra("IMS_REG_STATUS", false))
            return;

        //Update widgets
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, ToggleWidgetProvider.class);
        int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
        Intent updateWidgets = new Intent();
        updateWidgets.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateWidgets.putExtra(ToggleWidgetProvider.EXTRA_WIDGET_IDS, widgetIds);
        context.sendBroadcast(updateWidgets);

        //Notify Locale that state has changed
        Intent notifyLocale = new Intent();
        notifyLocale.setAction(com.twofortyfouram.locale.Intent.ACTION_REQUEST_QUERY);
        notifyLocale.putExtra(com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY, EditConditionActivity.class.getName());
        context.sendBroadcast(notifyLocale);
    }
}
