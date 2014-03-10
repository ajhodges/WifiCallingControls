package com.ajhodges.wifitoggle;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by Adam on 3/9/14.
 */
public class WifiCalling {
    public static final String IPPHONE_PREFS = "ipphonesettings";
    public static void toggleWifi(Context context, int mode){
        try{
            //Use dexclassloader to get the IPPhoneSettings class from the WifiCall app
            String jarFile = "/system/priv-app/WifiCall.apk";
            File dexOutputDir = context.getDir("dex", 0);
            DexClassLoader loader = new DexClassLoader(jarFile, dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
            Class<?> ipManager = loader.loadClass("com.movial.ipphone.IPPhoneSettings");

            //Use reflection to get the current state of Wifi Calling
            Method[] mthds =ipManager.getMethods();
            Integer ipphoneEnabled = 0;
            for(Method m : mthds){
                if(m.getName().equalsIgnoreCase("getBoolean")) {
                    ipphoneEnabled = (Boolean) m.invoke(null, context.getContentResolver(), "CELL_ONLY", false) ? 0 : 1;
                    Log.d(Constants.LOG_TAG, "Current wifi calling state: " + (ipphoneEnabled==1 ? "on" : "off"));
                }
            }

            //Toggle Wifi Calling
            if(mode == ipphoneEnabled) {
                //nothing to be done
                Log.d(Constants.LOG_TAG, "Wifi calling is already " + ((ipphoneEnabled == 1) ? "on" : "off") + "...");
                return;
            }
            else if (mode < 0){
                //toggle mode
                for(Method m : mthds){
                    if(m.getName().equalsIgnoreCase("putBoolean")) {
                        Log.d(Constants.LOG_TAG, "Turning wifi calling " + (!(ipphoneEnabled == 1) ? "on" : "off") + "...");
                        m.invoke(null, context.getContentResolver(), "CELL_ONLY", (!(ipphoneEnabled == 1) ? true : false));
                    }
                }
            } else {
                //set mode enabled/disabled
                boolean cellOnly = (mode == 0) ? true : false;
                for(Method m : mthds){
                    if(m.getName().equalsIgnoreCase("putBoolean")) {
                        Log.d(Constants.LOG_TAG, "Turning wifi calling " + ((cellOnly) ? "off" : "on") + "...");
                        m.invoke(null, context.getContentResolver(), "CELL_ONLY", cellOnly);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
