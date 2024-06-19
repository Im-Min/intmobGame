package com.example.intmob.fpga;

public class TextLCD{

    static {System.loadLibrary("intmob");}

    public static native int TextLCDOut(String str, String str2);

    public static native int IOCtlClear();

    public static native int IOCtlDisplay (boolean data);

    public static native int IOCtlReturnHome ();

    public static native int IOCtlCursor(boolean data);

    public static native int IOCtlBlink(boolean data);

    public static String padLeft(String s, char fill, int padSize) {
        if (padSize < 0) {
            String err = "padSize must be >= 0 (was " + padSize + ")";
            throw new IllegalArgumentException(err);
        }

        int repeats = Math.max(0, padSize - s.length());
        return repeat(Character.toString(fill), repeats) + s;
    }

    public static String repeat(String x, int y) {
        String ret = "";
        for(int i=0;i<y;i++) {
            ret += x;
        }
        return ret;
    }

    public static int UpdateValue(String s, String s2){
        assert s.length() <= 16;
        assert s2.length() <= 16;

        int ret = IOCtlClear();
        System.out.println("IOCtlClear="+ret);
        if(ret < 0){
            return ret;
        }

        ret = IOCtlReturnHome();
        System.out.println("IOCtlReturnHome="+ret);
        if(ret < 0){
            return ret;
        }

        ret = IOCtlDisplay(true);
        System.out.println("IOCtlDisplay(true)="+ret);
        if(ret < 0){
            return ret;
        }

        ret = IOCtlCursor(false);
        System.out.println("IOCtlCursor(false)="+ret);
        if(ret < 0){
            return ret;
        }

        ret = IOCtlBlink(false);
        System.out.println("IOCtlBlink(false)="+ret);
        if(ret < 0){
            return ret;
        }

        ret = TextLCDOut(s, s2);
        System.out.println("TextLCDOut="+ret);
        if(ret < 0){
            return ret;
        }

        return 0;
    }
}
