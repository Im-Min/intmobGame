package com.example.intmob;

import androidx.appcompat.app.*;

import android.opengl.GLSurfaceView;
import android.os.*;
import android.widget.*;
import com.example.intmob.databinding.*;
import java.io.*;
import java.lang.Process;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;

    // Used to load the 'intmob' library on application startup.
    static {
        System.loadLibrary("intmob");
    }

    private ActivityMainBinding binding;
    private Thread thread;
    private SetTextHandler setTextHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        GLRenderer renderer = new GLRenderer();
        glSurfaceView.setRenderer(renderer);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(glSurfaceView);

        setTextHandler = new SetTextHandler();

        thread = new Thread(()->eventloop());
        thread.start();
    }

    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }
    private void setText(Object text){
        Message msg1 = setTextHandler.obtainMessage(0, text);
        setTextHandler.sendMessage(msg1);
    }

    public void eventloop(){
        try{
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("chmod 777 /dev/input/*\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();


            try {
                p.waitFor();
            } catch (InterruptedException e) {
                System.err.println(e);
            }

            final String eventname = idev();

            while(true){
                Thread.sleep(1);
                String str1 = stringFromJNI(eventname);
                System.out.println("keypad pressed: '" + str1 + "'");
                handleKeypadInput(str1);
            }
        }
        catch(Exception e){
            System.err.println(e);
        }
    }

    void handleKeypadInput(String key){
        // keypad:
        // 1 2 3 X
        // 4 5 6 X
        // 7 8 9 X
        // X 0 X A

        // X is an unused key.

        if(Objects.equals(key, "1")){
            // 1
            setText("↖");
        }
        else if(Objects.equals(key, "2")){
            // 2
            setText("^");
        }
        else if(Objects.equals(key, "3")){
            // 3

        }
        else if(Objects.equals(key, "4")){
            // 4

        }
        else if(Objects.equals(key, "5")){
            // 5

        }
        else if(Objects.equals(key, "6")){
            // 6

        }
        else if("7".equals(key)){
            // 7

        }
        else if(Objects.equals(key, "8")){
            // 8
            setText("v");
        }
        else if(Objects.equals(key, "9")){
            // 9
            setText("↘");
        }
        else if(Objects.equals(key, ":")){
            // 0

        }
        else if(Objects.equals(key, "=")){
            // A
            setText("A");
        }
        else{
            System.err.println("Unhandled keypad input occured! key='"+key+"', length="+key.length());
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

    private native String idev();

}