package com.example.intmob.fpga;

import com.example.intmob.MainActivity;

import java.util.Random;

public class LED {

    static {
        MainActivity.loadLibrary();}

    public static native int on();
    public static native int off();
    public static native int set(int value);

    public static int rand(){
        int ret = set(MainActivity.dice.nextInt(256));

        if(ret != 0){
            System.err.println("LED.rand()="+ret);
        }

        return ret;
    }

}
