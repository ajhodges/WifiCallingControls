package com.ajhodges.wificallingcontrols.ipphone;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ajhodges.wificallingcontrols.Constants;
import com.ajhodges.wificallingcontrols.R;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by Adam on 4/7/2014.
 */
public class SamsungCallingManager extends WifiCallingManager{
    private static Class<?> WfcDbHelper = null;
    private static Class<? extends Enum> WfcDbHelperRegistration = null;
    private static Class<? extends Enum> WfcDbHelperProfile = null;

    private static Method getRegister = null;
    private static Method setRegister = null;

    private static Method getProfile = null;
    private static Method setProfile = null;

    private SamsungCallingManager(Context context){
        File dexOutputDir = context.getDir("dex", Context.MODE_PRIVATE);
        DexClassLoader loader = new DexClassLoader(apkFile, dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
        try {
            WfcDbHelper = loader.loadClass("com.samsung.tmowfc.wfcutils.WfcDbHelper");
            WfcDbHelperRegistration = (Class<? extends Enum>)loader.loadClass("com.samsung.tmowfc.wfcutils.WfcDbHelper$RegisterContract$State");
            WfcDbHelperProfile = (Class<? extends Enum>)loader.loadClass("com.samsung.tmowfc.wfcutils.WfcDbHelper$ProfileContract$Profile");

            getRegister = WfcDbHelper.getMethod("getRegister", new Class[]{ContentResolver.class});
            setRegister = WfcDbHelper.getMethod("setRegister", new Class[]{ContentResolver.class, WfcDbHelperRegistration});

            getProfile = WfcDbHelper.getMethod("getProfile", new Class[]{ContentResolver.class});
            setProfile = WfcDbHelper.getMethod("setProfile", new Class[]{ContentResolver.class, WfcDbHelperProfile});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static SamsungCallingManager instance = null;
    public static SamsungCallingManager getInstance(Context context){
        if(instance == null){
            instance = new SamsungCallingManager(context);
        }
        return instance;
    }

    @Override
    public boolean getIPPhoneEnabled(Context context) {
        //Use reflection to get the current state of Wifi Calling
        Boolean ipphoneEnabled = false;
        try {
            Enum registerContractState = (Enum)getRegister.invoke(null, context.getContentResolver());
            ipphoneEnabled = (registerContractState.name().equals("REGISTER"));
        } catch (InvocationTargetException e) {
            Toast moveToSystem = Toast.makeText(context.getApplicationContext(), R.string.system_wfc_toast, Toast.LENGTH_SHORT);
            moveToSystem.show();
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
        }
        Log.d(Constants.LOG_TAG, "Current wifi calling state: " + (ipphoneEnabled ? "on" : "off"));
        return ipphoneEnabled;
    }

    @Override
    public void toggleWifi(Context context, int mode) {
        boolean ipphoneEnabled = getIPPhoneEnabled(context);

        try {
            if (mode == MODE_TOGGLE) {
                //toggle mode
                Log.d(Constants.LOG_TAG, "Turning wifi calling " + (!ipphoneEnabled ? "on" : "off") + "...");
                setRegister.invoke(null, context.getContentResolver(), Enum.valueOf(WfcDbHelperRegistration, (ipphoneEnabled) ? "DONT_REGISTER" : "REGISTER" ));
            } else {
                if ((mode == MODE_ON) == ipphoneEnabled) {
                    //nothing to be done
                    Log.d(Constants.LOG_TAG, "Wifi calling is already " + (ipphoneEnabled ? "on" : "off") + "...");
                    return;
                }
                //set mode enabled/disabled
                Log.d(Constants.LOG_TAG, "Turning wifi calling " + ((mode == MODE_ON) ? "on" : "off") + "...");
                setRegister.invoke(null, context.getContentResolver(), Enum.valueOf(WfcDbHelperRegistration, (mode == MODE_ON) ? "REGISTER" : "DONT_REGISTER"));
            }
        } catch(IllegalAccessException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Toast moveToSystem = Toast.makeText(context.getApplicationContext(), R.string.system_wfc_toast, Toast.LENGTH_SHORT);
            moveToSystem.show();
            e.printStackTrace();
        }
    }

    @Override
    public int getPreferred(Context context){
        int preferred = -1;

        try {
            Enum preferredState = (Enum)getProfile.invoke(null, context.getContentResolver());
            if(preferredState.name().equals("WIFI_PREFERRED")){
                preferred = PREFER_WIFI;
            } else if(preferredState.name().equals("CELLULAR_NETWORK_PREFERRED")){
                preferred = PREFER_CELL;
            } else if(preferredState.name().equals("NEVER_USE_CELLULAR_NETWORK")){
                preferred = PREFER_NEVER_CELL;
            }
        } catch (IllegalAccessException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Toast moveToSystem = Toast.makeText(context.getApplicationContext(), R.string.system_wfc_toast, Toast.LENGTH_SHORT);
            moveToSystem.show();
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
                    setProfile.invoke(null, context.getContentResolver(), Enum.valueOf(WfcDbHelperProfile, "WIFI_PREFERRED"));
                    break;
                case PREFER_CELL:
                    setProfile.invoke(null, context.getContentResolver(), Enum.valueOf(WfcDbHelperProfile, "CELLULAR_NETWORK_PREFERRED"));
                    break;
                case PREFER_NEVER_CELL:
                    setProfile.invoke(null, context.getContentResolver(), Enum.valueOf(WfcDbHelperProfile, "NEVER_USE_CELLULAR_NETWORK"));
                    break;
            }
        } catch (InvocationTargetException e) {
            Toast moveToSystem = Toast.makeText(context.getApplicationContext(), R.string.system_wfc_toast, Toast.LENGTH_SHORT);
            moveToSystem.show();
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
            e.printStackTrace();
        }
    }
}
