package com.example.intmob.fpga;

import com.example.intmob.MainActivity;

public class Segment {

    static {
        MainActivity.loadLibrary();}

    public static native int writeInt(int value);

}
