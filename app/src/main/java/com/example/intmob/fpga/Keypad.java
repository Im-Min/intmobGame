package com.example.intmob.fpga;

public class Keypad {

    static {System.loadLibrary("intmob");}

    public static native String read(String event);
    private static native String idev();
    public static String getEventName(){
        return idev();
    }

}
