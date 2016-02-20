package com.ajhodges.wificallingcontrols.ipphone;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ajhodges.wificallingcontrols.Constants;
import com.ajhodges.wificallingcontrols.R;
import com.samsung.tmowfc.wfcutils.WfcDbHelper;

/**
 * Created by Adam on 4/7/2014.
 */
public class SamsungCallingManager extends WifiCallingManager{
    private SamsungCallingManager(Context context){

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
        try{
            WfcDbHelper.RegisterContract.State registerContractState = WfcDbHelper.getRegister(context.getContentResolver());
            ipphoneEnabled = (registerContractState.equals(WfcDbHelper.RegisterContract.State.REGISTER));
            Log.d(Constants.LOG_TAG, "Current wifi calling state: " + (ipphoneEnabled ? "on" : "off"));
        } catch (SecurityException e){
            Toast.makeText(context.getApplicationContext(), R.string.system_wfc_toast, Toast.LENGTH_SHORT).show();
        }
        return ipphoneEnabled;
    }

    @Override
    public void toggleWifi(Context context, int mode) {
        boolean ipphoneEnabled = getIPPhoneEnabled(context);

        try{
            if (mode == MODE_TOGGLE) {
                //toggle mode
                Log.d(Constants.LOG_TAG, "Turning wifi calling " + (!ipphoneEnabled ? "on" : "off") + "...");
                WfcDbHelper.setRegister(context.getContentResolver(), (ipphoneEnabled) ? WfcDbHelper.RegisterContract.State.DONT_REGISTER : WfcDbHelper.RegisterContract.State.REGISTER);
            } else {
                if ((mode == MODE_ON) == ipphoneEnabled) {
                    //nothing to be done
                    Log.d(Constants.LOG_TAG, "Wifi calling is already " + (ipphoneEnabled ? "on" : "off") + "...");
                    return;
                }
                //set mode enabled/disabled
                Log.d(Constants.LOG_TAG, "Turning wifi calling " + ((mode == MODE_ON) ? "on" : "off") + "...");
                WfcDbHelper.setRegister(context.getContentResolver(), (mode == MODE_ON) ? WfcDbHelper.RegisterContract.State.REGISTER : WfcDbHelper.RegisterContract.State.DONT_REGISTER);
            }
        } catch(SecurityException e){
            Toast.makeText(context.getApplicationContext(), R.string.system_wfc_toast, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getPreferred(Context context){
        int preferred = -1;
        try{
            WfcDbHelper.ProfileContract.Profile preferredState = WfcDbHelper.getProfile(context.getContentResolver());
            if(preferredState.equals(WfcDbHelper.ProfileContract.Profile.WIFI_PREFERRED)){
                preferred = PREFER_WIFI;
            } else if(preferredState.equals(WfcDbHelper.ProfileContract.Profile.CELLULAR_NETWORK_PREFERRED)){
                preferred = PREFER_CELL;
            } else if(preferredState.equals(WfcDbHelper.ProfileContract.Profile.NEVER_USE_CELLULAR_NETWORK)){
                preferred = PREFER_NEVER_CELL;
            }
        } catch (SecurityException ex){
            Toast.makeText(context.getApplicationContext(), R.string.system_wfc_toast, Toast.LENGTH_SHORT).show();
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
                    WfcDbHelper.setProfile(context.getContentResolver(), WfcDbHelper.ProfileContract.Profile.WIFI_PREFERRED);
                    break;
                case PREFER_CELL:
                    WfcDbHelper.setProfile(context.getContentResolver(), WfcDbHelper.ProfileContract.Profile.CELLULAR_NETWORK_PREFERRED);
                    break;
                case PREFER_NEVER_CELL:
                    WfcDbHelper.setProfile(context.getContentResolver(), WfcDbHelper.ProfileContract.Profile.NEVER_USE_CELLULAR_NETWORK);
                    break;
            }
        } catch (SecurityException ex){
            Toast.makeText(context.getApplicationContext(), R.string.system_wfc_toast, Toast.LENGTH_SHORT).show();
        }
    }
}
