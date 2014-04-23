package com.ajhodges.wificallingcontrols.ipphone;

import android.content.ContentResolver;
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
    public static final int MOVIAL_PREFER_WIFI = 0;
    public static final int MOVIAL_PREFER_CELL = 2;
    public static final int MOVIAL_PREFER_NEVER_CELL = 1;

    private static Class<?> ipPhoneSettings = null;
    private static Method getBoolean = null;
    private static Method putBoolean = null;

    private static Method getInt = null;
    private static Method putInt = null;

    private MovialCallingManager(Context context){
        //Use dexclassloader to get the IPPhoneSettings class from the WifiCall app
        File dexOutputDir = context.getDir("dex", Context.MODE_PRIVATE);
        DexClassLoader loader = new DexClassLoader(apkFile, dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
        try {
            ipPhoneSettings = loader.loadClass("com.movial.ipphone.IPPhoneSettings");
            getBoolean = ipPhoneSettings.getMethod("getBoolean", new Class[]{ContentResolver.class, String.class, boolean.class});
            putBoolean = ipPhoneSettings.getMethod("putBoolean", new Class[]{ContentResolver.class, String.class, boolean.class});
            getInt = ipPhoneSettings.getMethod("getInt", new Class[]{ContentResolver.class, String.class, int.class});
            putInt = ipPhoneSettings.getMethod("putInt", new Class[]{ContentResolver.class, String.class, int.class});
        } catch (ClassNotFoundException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
            e.printStackTrace();
        }
    }
    private static MovialCallingManager instance = null;
    public static MovialCallingManager getInstance(Context context){
        if(instance == null){
            instance = new MovialCallingManager(context);
        }
        return instance;
    }

    @Override
    //Test if Wifi calling is enabled
    public boolean getIPPhoneEnabled(Context context) {
        //Use reflection to get the current state of Wifi Calling
        Boolean ipphoneEnabled = false;
        try {
            ipphoneEnabled = !(Boolean) getBoolean.invoke(null, context.getContentResolver(), "CELL_ONLY", false);
        } catch (IllegalAccessException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
            e.printStackTrace();
        }
        Log.d(Constants.LOG_TAG, "Current wifi calling state: " + (ipphoneEnabled ? "on" : "off"));
        return ipphoneEnabled;
    }

    @Override
    //Toggle Wifi Calling mode(-1=toggle, 0=off, 1=on)
    public void toggleWifi(Context context, int mode) {
        boolean ipphoneEnabled = getIPPhoneEnabled(context);

        if (mode == MODE_TOGGLE){
            //toggle mode
            Log.d(Constants.LOG_TAG, "Turning wifi calling " + (!ipphoneEnabled ? "on" : "off") + "...");
            try {
                putBoolean.invoke(null, context.getContentResolver(), "CELL_ONLY", ipphoneEnabled);
            } catch (IllegalAccessException e) {
                Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
                e.printStackTrace();
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
            Log.d(Constants.LOG_TAG, "Turning wifi calling " + (cellOnly ? "off" : "on") + "...");
            try {
                putBoolean.invoke(null, context.getContentResolver(), "CELL_ONLY", cellOnly);
            } catch (IllegalAccessException e) {
                Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
                e.printStackTrace();
            }

        }
    }

    @Override
    public int getPreferred(Context context) {
        int preferred = -1;

        try {
            Integer preferredState = (Integer)getInt.invoke(null, context.getContentResolver(), "PREFERRED_OPTION", 0);
            switch(preferredState){
                case MOVIAL_PREFER_WIFI:
                    preferred = PREFER_WIFI;
                    break;
                case MOVIAL_PREFER_CELL:
                    preferred = PREFER_CELL;
                    break;
                case MOVIAL_PREFER_NEVER_CELL:
                    preferred = PREFER_NEVER_CELL;
                    break;
            }
        } catch (IllegalAccessException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
            e.printStackTrace();
        }
        return preferred;
    }

    @Override
    public void setPreferred(Context context, int preferred) {
        if(getPreferred(context) == preferred){
            return;
        }
        try {
            switch (preferred) {
                case PREFER_WIFI:
                    putInt.invoke(null, context.getContentResolver(), "PREFERRED_OPTION", MOVIAL_PREFER_WIFI);
                    break;
                case PREFER_CELL:
                    putInt.invoke(null, context.getContentResolver(), "PREFERRED_OPTION", MOVIAL_PREFER_CELL);
                    break;
                case PREFER_NEVER_CELL:
                    putInt.invoke(null, context.getContentResolver(), "PREFERRED_OPTION", MOVIAL_PREFER_NEVER_CELL);
                    break;
            }
        } catch (InvocationTargetException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
            e.printStackTrace();
        }
    }
}
