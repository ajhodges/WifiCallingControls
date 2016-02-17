package com.ajhodges.wificallingcontrols.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ajhodges.wificallingcontrols.Constants;
import com.ajhodges.wificallingcontrols.service.ToggleWidgetService;

/**
 * Created by Adam on 3/13/14.
 */
public class ToggleWidgetProvider extends AppWidgetProvider {
    public final static String EXTRA_WIDGET_IDS = "ToggleWidgetProviderID";
    public final static String EXTRA_WIDGET_TOGGLE = "ToggleWidgetProviderToggle";

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.hasExtra(EXTRA_WIDGET_IDS)){
            Log.d(Constants.LOG_TAG, "ToggleWidgetProvider: Received widget update");
            intent.setAction(ToggleWidgetService.WIDGET_UPDATE_ACTION);
            intent.setClass(context, ToggleWidgetService.class);
            context.startService(intent);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        Log.d(Constants.LOG_TAG, "ToggleWidgetProvider: Received widget update");
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent intent = new Intent();
        intent.setAction(ToggleWidgetService.WIDGET_UPDATE_ACTION);
        intent.setClass(context, ToggleWidgetService.class);
        intent.putExtra(ToggleWidgetProvider.EXTRA_WIDGET_IDS, appWidgetIds);
        context.startService(intent);
    }
}
