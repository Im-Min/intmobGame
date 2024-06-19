package com.example.intmob;

public class TextLCD{

    public static native int control(String str, String str2);
    public static native int clear();
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

    static void UpdateValue(String s, String s2){
        assert s.length() <= 16;
        assert s2.length() <= 16;
        clear();
        IOCtlReturnHome();
        IOCtlDisplay(true);
        IOCtlCursor(false);
        IOCtlBlink(false);
        control(padLeft(s, ' ', 16), padLeft(s2, ' ', 16));


    }
}
