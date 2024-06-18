package com.example.intmob;

import android.util.Log;
public class TextLCD{

    static{
        System.loadLibrary("intmob");

        // Test open (optional)
        boolean ret = open();
        if (!ret)
            Log.d("TextLCD", "Open fail");
    }

    public static native boolean open();
    public static native int control(String str, String str2);
    public static native int clear();
    public static native int IOCtlDisplay (boolean data);
    public static native int IOCtlReturnHome ();
    public static native int IOCtlCursor(boolean data);
    public static native int IOCtlBlink(boolean data);

    static void UpdateValue(int count){
        clear();
        IOCtlReturnHome();
        IOCtlDisplay(true);
        IOCtlCursor(false);
        IOCtlBlink(false);

       switch (count){
           case 1:
               control("    Doke'Mon", "  stage 1 - 1");
               break;
           case 2:
               control("    Doke'Mon", "  stage 1 - 2");
               break;
           case 3:
               control("    Doke'Mon", "  stage 1 - 3");
               break;
           case 4:
               control("    Doke'Mon", "  stage 2 - 1");
               break;
           case 5:
               control("    Doke'Mon", "  stage 2 - 2");
               break;
           case 6:
               control("    Doke'Mon", "  stage 2 - 3");
               break;
           case 7:
               control("    Doke'Mon", "  stage 3 - 1");
               break;
           case 8:
               control("    Doke'Mon", "  stage 3 - 2");
               break;
           case 9:
               control("    Doke'Mon", "  stage 3 - 3");
               break;
       }
    }
}
