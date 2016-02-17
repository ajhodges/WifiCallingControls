package com.ajhodges.wificallingcontrols;

/**
 * Created by Adam on 2/16/2016.
 */
public class NotCompatibleException extends Exception {
    public NotCompatibleException(){ super("Your device is not currently compatible with Wifi Calling Controls."); }

    public NotCompatibleException(String message){
        super(message);
    }

    public NotCompatibleException(String message, Throwable throwable){
        super(message, throwable);
    }
}
