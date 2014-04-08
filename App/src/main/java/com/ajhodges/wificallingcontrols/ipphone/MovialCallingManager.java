package com.ajhodges.wificallingcontrols.ipphone;

import android.content.Context;
import android.util.Log;

import com.ajhodges.wificallingcontrols.Constants;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by Adam on 3/9/14.
 */
public class MovialCallingManager extends WifiCallingManager {
    private static Class<?> ipPhoneSettings = null;

    private MovialCallingManager(Context context){
        //Use dexclassloader to get the IPPhoneSettings class from the WifiCall app
        File dexOutputDir = context.getDir("dex", Context.MODE_PRIVATE);
        DexClassLoader loader = new DexClassLoader(apkFile, dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
        try {
            ipPhoneSettings = loader.loadClass("com.movial.ipphone.IPPhoneSettings");
        } catch (ClassNotFoundException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
        }
    }
    private static MovialCallingManager instance = null;
    public static MovialCallingManager getInstance(Context context){
        if(instance == null){
            instance = new MovialCallingManager(context);
        }
        return instance;
    }

    //Test if Wifi calling is enabled
    public boolean getIPPhoneEnabled(Context context) {
        //Use reflection to get the current state of Wifi Calling
        Method[] mthds = ipPhoneSettings.getMethods();
        Boolean ipphoneEnabled = false;
        for(Method m : mthds){
            if(m.getName().equalsIgnoreCase("getBoolean")) {
                try {
                    ipphoneEnabled = !(Boolean) m.invoke(null, context.getContentResolver(), "CELL_ONLY", false);
                } catch (IllegalAccessException e) {
                    Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
                } catch (InvocationTargetException e) {
                    Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
                }
                Log.d(Constants.LOG_TAG, "Current wifi calling state: " + (ipphoneEnabled ? "on" : "off"));
            }
        }
        return ipphoneEnabled;
    }

    //Toggle Wifi Calling mode(-1=toggle, 0=off, 1=on)
    public void toggleWifi(Context context, int mode) {
        boolean ipphoneEnabled = getIPPhoneEnabled(context);

        Method[] mthds = ipPhoneSettings.getMethods();
        if (mode == MODE_TOGGLE){
            //toggle mode
            for(Method m : mthds){
                if(m.getName().equalsIgnoreCase("putBoolean")) {
                    Log.d(Constants.LOG_TAG, "Turning wifi calling " + (!ipphoneEnabled ? "on" : "off") + "...");
                    try {
                        m.invoke(null, context.getContentResolver(), "CELL_ONLY", ipphoneEnabled);
                    } catch (IllegalAccessException e) {
                        Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
                    } catch (InvocationTargetException e) {
                        Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
                    }
                }
            }
        } else {
            //Wifi Calling is OFF when cellOnly == true
            boolean cellOnly = (mode == MODE_OFF);

            if(!cellOnly == ipphoneEnabled) {
                //nothing to be done
                Log.d(Constants.LOG_TAG, "Wifi calling is already " + (ipphoneEnabled ? "on" : "off") + "...");
                return;
            }

            //set mode enabled/disabled
            for(Method m : mthds){
                if(m.getName().equalsIgnoreCase("putBoolean")) {
                    Log.d(Constants.LOG_TAG, "Turning wifi calling " + (cellOnly ? "off" : "on") + "...");
                    try {
                        m.invoke(null, context.getContentResolver(), "CELL_ONLY", cellOnly);
                    } catch (IllegalAccessException e) {
                        Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
                    } catch (InvocationTargetException e) {
                        Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
                    }
                }
            }
        }
    }
}
