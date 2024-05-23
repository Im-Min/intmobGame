package com.example.intmob;

import android.os.*;
import androidx.annotation.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class Devices {
    private final String PATH = "/proc/bus/input/devices";

    void makeReadable(){
        new File(PATH).setReadable(true);
    }

    List<String> readAllLines() throws IOException {
        return Files.readAllLines(Paths.get(PATH));
    }

    List<List<String>> splitLines(List<String> allLines){

        int a = 0;
        List<List<String>> ret = new ArrayList<List<String>>();

        for(int i=0;i<allLines.size();i++){

            String line = allLines.get(i);

            if(line.isEmpty()){

                List<String> sublist = allLines.subList(a, i);

                a = i+1;

                ret.add(sublist);

            }

        }

        return ret;
    }

    public List<Device> devices;

    public Devices() throws IOException{
        makeReadable();
        List<String> allLines = readAllLines();
        List<List<String>> splitedlines = splitLines(allLines);

        devices = new ArrayList<>();
        for (List<String> splitedline : splitedlines) {
            devices.add(new Device(splitedline));
        }

    }



}
