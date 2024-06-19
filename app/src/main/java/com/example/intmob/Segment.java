package com.example.intmob;

public class Segment {
    private static native int SegmentControl(String value);
    public static int set7SegmentNumber(int x){
        assert x >= 0;
        assert x < 1000000;
        String y = String.format("%06d", x);
        return SegmentControl(y);
    }
}
