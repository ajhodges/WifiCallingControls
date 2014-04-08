package com.ajhodges.wificallingcontrols.ipphone;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by Adam on 4/7/2014.
 */
public abstract class WifiCallingManager {
    final public static int MODE_TOGGLE = -1;
    final public static int MODE_OFF = 0;
    final public static int MODE_ON = 1;

    public static final int TYPE_MOVIAL = 0;
    public static final int TYPE_SAMSUNG = 1;
    public static final int TYPE_KINETO = 2;
    public static int type = -1;

    public static String apkFile = "";
    //get singleton
    public static WifiCallingManager getInstance(Context context){
        //test to see what kind of IMS this phone uses
        if(type == -1){
            //Find location of the WifiCall app
            PackageManager pm = context.getPackageManager();
            for (ApplicationInfo app : pm.getInstalledApplications(0)){
                if(app.packageName.equals("com.movial.wificall")){
                    type = TYPE_MOVIAL;
                    apkFile = app.sourceDir;
                } else if (app.packageName.equals("com.samsung")) {
                    type = TYPE_SAMSUNG;
                } else if (app.packageName.equals("kineto")){
                    type = TYPE_KINETO;
                }
            }
        }
        switch(type){
            case TYPE_MOVIAL:
                return MovialCallingManager.getInstance(context);
            default:
                return null;
        }

    }

    //Test if Wifi calling is enabled
    public abstract boolean getIPPhoneEnabled(Context context);

    //Toggle Wifi Calling mode(-1=toggle, 0=off, 1=on)
    public abstract void toggleWifi(Context context, int mode);
}
