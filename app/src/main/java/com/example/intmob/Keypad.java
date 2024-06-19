package com.example.intmob;

public class Keypad {

    static {System.loadLibrary("intmob");}

    static native String read(String event);

}
