package com.example.intmob;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build;
import android.os.Looper;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.text.format.Time;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataOutputStream;
import java.lang.Process;
import java.util.Objects;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {
    BackThread thread = new BackThread();
    protected static final int THREAD_FLAGS_PRINT = 0; // Countdown
    protected static final int THREAD_FLAGS_CLOCK = 1; // Clock
    protected static final int DIALOG_SIMPLE_MESSAGE = 1;
    int flag = -1;
    boolean stop = false;
    int count = 0;
    private GLSurfaceView glSurfaceView;
    // Static block is only called once when the class itself is initialized.
    // Used to load the 'intmob' library on application startup.
    static {System.loadLibrary("intmob");} // JNI Library Load

    EventHandler m_eventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // requestFeature() must be called before adding content
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        m_eventHandler = new EventHandler();

        // Thread Start
        thread.setDaemon(true);
        thread.start();

        new Thread(this::eventloop).start();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        glSurfaceView = findViewById(R.id.glSurfaceView);
        if(glSurfaceView == null){
            throw new RuntimeException("error:glSurfaceView is null");
        }

        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                if(init() != 0){
                    System.err.println("[ERROR] jni function init failed!");
                }
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                gl.glViewport(0, 0, width, height);
                if(setOrthographicMatrix(width, height) != 0){
                    System.err.println("[ERROR] setOrthographicMatrix failed!");
                }
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                int ret = step();
                if(ret == 2){
                    // Ghost and Pacman collided
                    OnPacmanGhostCollision();
                }
            }
        });
    }

    public void UpdateValue(){
        showDialog(DIALOG_SIMPLE_MESSAGE);
    }

    public class EventHandler extends Handler{
        EventHandler(){}
        public void handleMessage(Message msg){
            try{
                if(msg.what==1){
                    UpdateValue();
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    class BackThread extends Thread{
        public void run(){
            while(!stop){
                switch(flag){
                    default:
                        // do nothing
                        break;
                    case THREAD_FLAGS_PRINT:
                        // Countdown
                        SegmentIOControl(THREAD_FLAGS_PRINT);
                        while(count > 0 && flag == THREAD_FLAGS_PRINT){
                            for (int i=0;i<14&&flag == THREAD_FLAGS_PRINT;){
                                SegmentControl(count);
                            }
                            count--;
                        }
                        // flag = 0;
                        break;

                    case THREAD_FLAGS_CLOCK:
                        // Clock
                        SegmentIOControl(THREAD_FLAGS_CLOCK);
                        int result = 0;

                        Time t = new Time();
                        t.set(System.currentTimeMillis());
                        result = t.hour * 10000 + t.minute * 100 + t.second;
                        result += 1000000;
                        for(int i=0;i<20;i++)
                            SegmentControl(result);
                        break;
                }
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    // Program exit
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            flag = -1;
            stop = true;
            thread.interrupt();
        }
        return super.onKeyDown(keyCode, event);
    }

    // Exception processing for input countdown value
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        Dialog d = new Dialog(MainActivity.this);
        Window window = d.getWindow();

        window.setFlags(WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW,
                WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW);

        switch(id){
            case DIALOG_SIMPLE_MESSAGE:
                d.setTitle("Maximum input digit is 6");
                d.show();
                return d;
        }
        return super.onCreateDialog(id);
    }

    void OnPacmanGhostCollision(){
        System.out.println("The pacman and a ghost collided.");
        // TODO
        OnGameEnd();
    }

    void OnGameEnd(){
        // TODO
    }

    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
        enterFullScreenMode();
    }

    private void printex(Exception ex){
        System.err.println(ex.toString());
    }

    private int chmod777() {
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("chmod 777 /dev/input/*\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            p.waitFor();
        }
        catch(Exception ex){
            printex(ex);
            return 1;
        }
        return 0;
    }

    public void eventloop(){
        try{
            if(chmod777() != 0){
                return;
            }

            final String eventname = idev();

            while(true){
                Thread.sleep(1);
                String str1 = stringFromJNI(eventname);

                if(Objects.equals(str1, "open:Permisson denied")){
                    // if permission denied while opening a device
                    System.err.println(str1);
                    return;
                }

                System.out.println("keypad pressed: '" + str1 + "'");
                if(handleKeypadInput(str1) != 0){
                    return;
                }
            }
        }
        catch(Exception ex){
            printex(ex);
        }
    }
    static final int UP = 0;
    static final int LEFT = 2;
    int setDirectionUp(){
        int ret = setDirection(UP);
        if(ret != 0){
            System.err.println("err:setDirection="+ret);
        }
        return ret;
    }
    int setDirectionLeft(){
        int ret = setDirection(LEFT);
        if(ret != 0){
            System.err.println("err:setDirection="+ret);
        }
        return ret;
    }
    static final int DOWN = 1;
    int setDirectionDown(){
        int ret = setDirection(DOWN);
        if(ret != 0){
            System.err.println("err:setDirection="+ret);
        }
        return ret;
    }
    static final int RIGHT = 3;
    int setDirectionRight(){
        int ret = setDirection(RIGHT);
        if(ret != 0){
            System.err.println("err:setDirection="+ret);
        }
        return ret;
    }
    int handleKeypadInput(String key){
        // keypad:
        // 1 2 3 X
        // 4 5 6 X
        // 7 8 9 X
        // X 0 X A

        // X is an unused key.

        if(Objects.equals(key, "1")){
            // 1
            // Can't create handler inside thread that has not called Looper.prepare()
            Message msg1 = m_eventHandler.obtainMessage();
            msg1.what = 1;
            m_eventHandler.sendMessage(msg1);
        }
        else if(Objects.equals(key, "2")){
            // 2
            setDirectionUp();
        }
        else if(Objects.equals(key, "3")){
            // 3
            count = 123456;
            flag = THREAD_FLAGS_PRINT;
        }
        else if(Objects.equals(key, "4")){
            // 4
            setDirectionLeft();
        }
        else if(Objects.equals(key, "5")){
            // 5
            setDirectionDown();
        }
        else if(Objects.equals(key, "6")){
            // 6
            setDirectionRight();
        }
        else if("7".equals(key)){
            // 7
            flag = THREAD_FLAGS_CLOCK;
        }
        else if(Objects.equals(key, "8")){
            // 8
            flag = -1;
        }
        else if(Objects.equals(key, "9")){
            // 9
        }
        else if(Objects.equals(key, ":")){
            // 0
        }
        else if(Objects.equals(key, "=")){
            // A
        }
        else{
            System.err.println("[ERROR] Unhandled keypad input occured! key='"+key+"', length="+key.length());
            return 1;
        }
        return 0;
    }

    private void enterFullScreenMode() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }



    /**
     * A native method that is implemented by the 'intmob' native library,
     * which is packaged with this application.
     */
    private native String stringFromJNI(String event);
    private native String idev();
    private native int init();
    private native int step();
    private native int setDirection(int direction);
    private native int setOrthographicMatrix(int width, int height);
    private native int div0();
    native int SegmentControl(int value);
    native int SegmentIOControl(int value);
}
