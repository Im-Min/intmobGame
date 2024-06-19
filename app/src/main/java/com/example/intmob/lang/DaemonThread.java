package com.example.intmob.lang;

public class DaemonThread extends Thread{
    public DaemonThread(){
        setDaemon(true);
    }
}
