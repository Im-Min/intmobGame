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
import java.util.Date;
import java.util.Objects;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {
    BackThread thread = new BackThread();
    protected static final int DIALOG_SIMPLE_MESSAGE = 1;
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
        System.out.println("BackThread started");

        TextLCD.UpdateValue("HANBACK", "Electronics!");

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
                    System.err.println("err:jni function initialize fail");
                }
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                gl.glViewport(0, 0, width, height);
                if(setOrthographicMatrix(width, height) != 0){
                    System.err.println("err:setOrthographicMatrix fail");
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
            try{

                while(!stop) {
                    Segment.set7SegmentNumber(count);
                    sleep(1);
                }

            } catch (InterruptedException e) {
                System.out.println("BackThread:Interrupted");
                return;
            }

        }
    }


    // Program exit
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // KEYCODE_BACK is a back button on the table board.
        if(keyCode == KeyEvent.KEYCODE_BACK){
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

    void setScore(int score){
        count = score;
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

    int handleKeypadInput(String key){
        // keypad key mapping:
        // 1 2 3 ?
        // 4 5 6 ?
        // 7 8 9 ?
        // ? 0 ? A

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
            // test
            setScore(98765);
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
        }
        else if(Objects.equals(key, "8")){
            // 8
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

    static final int UP = 0;
    static final int DOWN = 1;
    static final int LEFT = 2;
    static final int RIGHT = 3;
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


    int setDirectionDown(){
        int ret = setDirection(DOWN);
        if(ret != 0){
            System.err.println("err:setDirection="+ret);
        }
        return ret;
    }
    int setDirectionRight(){
        int ret = setDirection(RIGHT);
        if(ret != 0){
            System.err.println("err:setDirection="+ret);
        }
        return ret;
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
}
