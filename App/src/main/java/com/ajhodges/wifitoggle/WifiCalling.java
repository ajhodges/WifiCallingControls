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
            String jarFile = "/system/priv-app/WifiCall.apk";
            File dexOutputDir = context.getDir("dex", 0);
            DexClassLoader loader = new DexClassLoader(jarFile, dexOutputDir.getAbsolutePath(), null, context.getClassLoader());

            Class<?> ipManager = loader.loadClass("com.movial.ipphone.IPPhoneSettings");
            Method[] mthds =ipManager.getMethods();

            Integer ipphoneEnabled = 0;
            for(Method m : mthds){
                if(m.getName().equalsIgnoreCase("getBoolean"))
                    ipphoneEnabled = (Boolean)m.invoke(null, context.getContentResolver(), "CELL_ONLY", false) ? 1 : 0;
            }

            if(mode == ipphoneEnabled)
                //nothing to be done
                return;
            else if (mode < 0){
                //toggle mode
                for(Method m : mthds){
                    if(m.getName().equalsIgnoreCase("putBoolean"))
                        m.invoke(null, context.getContentResolver(), "CELL_ONLY", (ipphoneEnabled == 1) ? false : true);
                }
            } else {
                //toggle mode
                boolean cellOnly = (mode == 0) ? true : false;
                for(Method m : mthds){
                    if(m.getName().equalsIgnoreCase("putBoolean"))
                        m.invoke(null, context.getContentResolver(), "CELL_ONLY", cellOnly);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
