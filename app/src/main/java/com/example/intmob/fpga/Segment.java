package com.example.intmob.fpga;

import android.util.Log;

import com.example.intmob.MainActivity;
import com.example.intmob.lang.DaemonThread;

public class Segment extends DaemonThread {

    static {
        MainActivity.loadLibrary();
    }

    public static native int writeInt(int value);

    public int value = -1;

    @Override
    public void run(){
            while(true) {
                if(0 <= value && value <= 999999) {

                    if (Segment.writeInt(value) != 0) {

                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            return;
                        }

                    }

                }

                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    return;
                }

            }
    }

    public void random(){
        value = MainActivity.dice.nextInt(1000000);
    }

}
