package com.example.intmob.fpga;

public class Keypad {

    static {System.loadLibrary("intmob");}

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
