package com.ajhodges.wificallingcontrols.ipphone;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.ajhodges.wificallingcontrols.Constants;
import com.ajhodges.wificallingcontrols.NotCompatibleException;

/**
 * Created by Adam on 4/7/2014.
 */
public abstract class WifiCallingManager {
    final public static int MODE_TOGGLE = -1;
    final public static int MODE_OFF = 0;
    final public static int MODE_ON = 1;

    final public static int PREFER_WIFI = 10;
    final public static int PREFER_CELL = 11;
    final public static int PREFER_NEVER_CELL = 12;

    public static final int TYPE_MOVIAL = 0;
    public static final int TYPE_SAMSUNG = 1;
    public static final int TYPE_KINETO = 2;
    public static final int TYPE_LGE = 3;
    public static int type = -1;

    public static String apkFile = "";
    //get singleton
    public static WifiCallingManager getInstance(Context context) throws NotCompatibleException {
        //test to see what kind of IMS this phone uses
        if(type == -1){
            //Find location of the WifiCall app
            PackageManager pm = context.getPackageManager();
            for (ApplicationInfo app : pm.getInstalledApplications(0)){
                if(app.packageName.equals("com.movial.wificall")){
                    type = TYPE_MOVIAL;
                    apkFile = app.sourceDir;
                    break;
                } else if (app.packageName.equals("com.lge.wificall")){
                    type = TYPE_LGE;
                    apkFile = app.sourceDir;
                } else if (app.packageName.equals("com.samsung.tmowfc.wfccontroller")) {
                    type = TYPE_SAMSUNG;
                    //I don't like this (hardcoding)... but the class we want is in an external library.
                    apkFile = "/system/framework/TmoWfcUtils.jar";
                    break;
                } else if (app.packageName.equals("com.android.kineto")){
                    type = TYPE_KINETO;
                    //break;
                }
            }
        }

        switch(type){
            case TYPE_MOVIAL:
                return MovialCallingManager.getInstance(context);
            case TYPE_SAMSUNG:
                return SamsungCallingManager.getInstance(context);
            case TYPE_LGE:
                return LGECallingManager.getInstance(context);
            default:
                throw new NotCompatibleException();
        }

    }

    //Test if Wifi calling is enabled
    public abstract boolean getIPPhoneEnabled(Context context);

    //Toggle Wifi Calling mode(-1=toggle, 0=off, 1=on)
    public abstract void toggleWifi(Context context, int mode);

    public abstract int getPreferred(Context context);

    //Set "preferred" option
    public abstract void setPreferred(Context context, int preferred);
}
