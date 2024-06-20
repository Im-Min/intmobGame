package com.example.intmob.fpga;

import com.example.intmob.MainActivity;

public class Keypad {

    static {
        MainActivity.loadLibrary();}

    public static native String read(String event);
    private static native String idev();
    public static String getEventName(){
        return idev();
    }
    private static String eventName;
    public static String read(){
        if(eventName == null){
            eventName = getEventName();
            if(eventName == null){
                eventName = "null";
            }
        }

        if(!eventName.equals("null")){
            return read(eventName);
        }
        return null;
    }

}
