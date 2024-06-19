package com.example.intmob;

import java.util.Random;

public class LED {
    static native int on();
    static native int off();
    static native int set(int value);

    static Random dice = new Random();

    public static int rand(){

        int ret =  set(dice.nextInt(256));
        if(ret != 0){
            System.err.println("LED.rand()="+ret);
        }
        return ret;

    }

}
