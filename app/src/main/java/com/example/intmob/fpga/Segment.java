package com.example.intmob.fpga;

import com.example.intmob.MainActivity;

public class Segment {

    static {
        MainActivity.loadLibrary();}

    private static native int SegmentControl(String value);
    public static int set7SegmentNumber(int x){
        // if x is 12345,
        // 012345 will display on 7 segment.

        assert x >= 0;
        assert x < 1000000;
        String y = String.format("%06d", x);
        return SegmentControl(y);
    }
}
