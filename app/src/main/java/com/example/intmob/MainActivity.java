package com.example.intmob;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;

import com.example.intmob.databinding.ActivityMainBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'intmob' library on application startup.
    static {
        System.loadLibrary("intmob");
    }

    private ActivityMainBinding binding;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = super.onKeyDown(keyCode, event);

        TextView tv = binding.sampleText;
        //tv.setText(String.valueOf(keyCode));


        return ret;
    }

    MyThread m_thread;
    EventHandler m_eventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UpdateValue();

        m_eventHandler = new EventHandler();

        m_thread = new MyThread();
        m_thread.start();
    }

    class MyThread extends Thread
    {
        public void run(){
            try{
                while(true){
                    Thread.sleep(100);
                    Message msg1 = m_eventHandler.obtainMessage();
                    m_eventHandler.sendMessage(msg1);
                }
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
    }

    public void UpdateValue(){
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
    }

    class EventHandler extends Handler
    {
        EventHandler(){}
        public void handleMessage(Message msg){
            UpdateValue();
        }
    }

    /**
     * A native method that is implemented by the 'intmob' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}