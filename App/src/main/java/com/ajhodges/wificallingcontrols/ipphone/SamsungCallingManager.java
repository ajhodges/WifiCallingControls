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
    private static Method setRegister = null;

    private SamsungCallingManager(Context context){
        File dexOutputDir = context.getDir("dex", Context.MODE_PRIVATE);
        DexClassLoader loader = new DexClassLoader(apkFile, dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
        try {
            WfcDbHelper = loader.loadClass("com.samsung.tmowfc.wfcutils.WfcDbHelper");
            WfcDbHelperRegistration = (Class<? extends Enum>)loader.loadClass("com.samsung.tmowfc.wfcutils.WfcDbHelper$RegisterContract$State");
            setRegister = WfcDbHelper.getMethod("setRegister", new Class[]{ContentResolver.class, WfcDbHelperRegistration});
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
            Method getRegister = WfcDbHelper.getMethod("getRegister", new Class[]{ContentResolver.class});
            Enum registerContractState = (Enum)getRegister.invoke(null, context.getContentResolver());
            ipphoneEnabled = (registerContractState.name().equals("REGISTER"));

        } catch (NoSuchMethodException e) {
            Log.e(Constants.LOG_TAG, "ERROR: This app is not compatible with your phone");
        } catch (InvocationTargetException e) {
            Toast moveToSystem = Toast.makeText(context.getApplicationContext(), R.string.samsung_toast, Toast.LENGTH_SHORT);
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
            Toast moveToSystem = Toast.makeText(context.getApplicationContext(), R.string.samsung_toast, Toast.LENGTH_SHORT);
            moveToSystem.show();
            e.printStackTrace();
        }
    }
}
