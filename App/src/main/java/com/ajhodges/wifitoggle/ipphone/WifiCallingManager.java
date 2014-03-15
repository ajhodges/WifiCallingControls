package com.ajhodges.wifitoggle.ipphone;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.ajhodges.wifitoggle.Constants;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * Created by Adam on 3/9/14.
 */
public class WifiCallingManager {
    final public static int MODE_TOGGLE = -1;
    final public static int MODE_OFF = 0;
    final public static int MODE_ON = 1;

    final private static String apkFile = "/system/priv-app/WifiCall.apk";
    private static Class<?> ipPhoneSettings = null;

    private List<WifiCallingListener> listeners;

    private static Context mContext;
    private static Handler handler = new Handler();

    private WifiCallingManager(Context context){
        mContext = context;

        //Use dexclassloader to get the IPPhoneSettings class from the WifiCall app
        File dexOutputDir = context.getDir("dex", 0);
        DexClassLoader loader = new DexClassLoader(apkFile, dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
        try {
            ipPhoneSettings = loader.loadClass("com.movial.ipphone.IPPhoneSettings");
        } catch (ClassNotFoundException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
        }

        listeners = new ArrayList<WifiCallingListener>();

        try {
            Field uriField = ipPhoneSettings.getField("CONTENT_URI");
            Uri contentURI = (Uri)uriField.get(null);

            Log.d(Constants.LOG_TAG, contentURI.toString());
            context.getApplicationContext().getContentResolver().registerContentObserver(
                    contentURI, true, new IPPhoneContentObserver(handler));

        } catch (NoSuchFieldException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
        } catch (IllegalAccessException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
        }


    }
    private static WifiCallingManager instance = null;
    public static WifiCallingManager getInstance(Context context){
        if(instance == null){
            instance = new WifiCallingManager(context);
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

    public void registerListener(WifiCallingListener listener){
        listeners.add(listener);
    }
    public void unregisterListener(WifiCallingListener listener) { listeners.remove(listener); }

    private void notifyListeners(Context context){
        for(WifiCallingListener listener : listeners){
            listener.onWifiCallingStateChanged(context);
        }
    }

    public class IPPhoneContentObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public IPPhoneContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications(){
            return true;
        }

        @Override
        public void onChange(boolean selfChange){
            Log.v(Constants.LOG_TAG, "IPPhoneContentObserver: CELL_ONLY CHANGED!");
            super.onChange(selfChange);
            notifyListeners(mContext);
        }
    }
}
