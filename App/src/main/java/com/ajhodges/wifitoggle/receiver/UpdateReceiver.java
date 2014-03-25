package com.ajhodges.wifitoggle.receiver;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.ajhodges.wifitoggle.widget.ToggleWidgetProvider;

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
        Boolean reg_status = intent.getBooleanExtra("IMS_REG_STATUS", false);
        if(!reg_status){
            //Wifi calling disabled or being enabled (best reflects the state of CELL_ONLY in db)
            //Update widgets
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            ComponentName widgetComponent = new ComponentName(context, ToggleWidgetProvider.class);
            int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
            Intent updateWidgets = new Intent();
            updateWidgets.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateWidgets.putExtra(ToggleWidgetProvider.EXTRA_WIDGET_IDS, widgetIds);
            context.sendBroadcast(updateWidgets);

            //Locale broadcast

        } else{
            //Wifi calling fully enabled, ready for calls
        }
    }
}
