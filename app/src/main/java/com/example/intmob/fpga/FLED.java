package com.example.intmob.fpga;

import android.util.Log;

import com.example.intmob.MainActivity;

public class FLED {
    static{
        MainActivity.loadLibrary();
    }

    /*
    #define FULL_LED1 9
#define FULL_LED2 8
#define FULL_LED3 7
#define FULL_LED4 6
#define ALL_LED   5
*/
    public static final int FULL_LED1 = 9;
    public static final int FULL_LED2 = 8;
    public static final int FULL_LED3 = 7;
    public static final int FULL_LED4 = 6;
    public static final int ALL_LED = 5;

    public static native int FLEDControl(int led_num, int vval1, int val2, int val3);

    static int temp;
    final static int[] led_val = new int[] {FULL_LED1, 0, 0, 0};

    public static int FLEDControl(){
        return FLEDControl(led_val[0], led_val[1], led_val[2], led_val[3]);
    }

    public static int random(){
        int i, j, res, ret;
        for(i=6;i<10;i++){
            led_val[0] = i;
            for(j=1;j<4;j++) {
                res = 1 + MainActivity.dice.nextInt(154);
                led_val[j] = res;
            }
            ret = FLEDControl();
            if(ret != 0){
                Log.e("FLED", "ret="+ret);
                return ret;
            }
        }
        return 0;
    }

}
