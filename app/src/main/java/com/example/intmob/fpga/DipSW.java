package com.example.intmob.fpga;

import com.example.intmob.MainActivity;

public class DipSW {
    static{
        MainActivity.loadLibrary();
    }

    public static native int GetValue();
}
