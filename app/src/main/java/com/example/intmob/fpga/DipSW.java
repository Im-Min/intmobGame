package com.example.intmob.fpga;

public class DipSW {
    static{
        System.loadLibrary("intmob");
    }

    public static native int GetValue();
}
