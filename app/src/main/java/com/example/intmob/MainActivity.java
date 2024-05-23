package com.example.intmob;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.intmob.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'intmob' library on application startup.
    static {
        System.loadLibrary("intmob");
    }

    private ActivityMainBinding binding;
    private MyThread m_thread;
    private SetTextHandler setTextHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setTextHandler = new SetTextHandler();


        m_thread = new MyThread();
        m_thread.start();
    }

    private void setText(Object text){
        Message msg1 = setTextHandler.obtainMessage(0, text);
        setTextHandler.sendMessage(msg1);
    }

    private static String getFileType(String path){
        File file = new File(path);
        if(file.isFile()){
            return "file";
        }
        if(file.isDirectory()){
            return "directory";
        }
        if(file.exists()){
            return "exists";
        }
        return "does not exist";
    }

    private static void exec(String command) throws IOException {
        Runtime.getRuntime().exec(command);
    }

    private class MyThread extends Thread
    {
        public void run(){
            try{

                final String eventname = idev();

                while(true){
                    Thread.sleep(1000);
                    String str1 = stringFromJNI(eventname);
                    setText(str1);
                }
            }
            catch(Exception e){
                System.err.println(e);
            }
        }
    }


    private class SetTextHandler extends Handler
    {
        public void handleMessage(Message msg ){
            TextView v = binding.sampleText;
            Object obj = msg.obj;
            String sobj = obj.toString();
            v.setText(sobj);
        }
    }

    /**
     * A native method that is implemented by the 'intmob' native library,
     * which is packaged with this application.
     */
    private native String stringFromJNI(String event);
    private native void div0();
    private native String idev();

}