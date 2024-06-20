package com.example.intmob;

import android.app.Dialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.Handler;
import android.os.Message;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intmob.fpga.DipSW;
import com.example.intmob.fpga.DotMatrix;
import com.example.intmob.fpga.FLED;
import com.example.intmob.fpga.Keypad;
import com.example.intmob.fpga.LED;
import com.example.intmob.fpga.OLED;
import com.example.intmob.fpga.Segment;
import com.example.intmob.fpga.TextLCD;
import com.example.intmob.lang.DaemonThread;

import java.io.DataOutputStream;
import java.lang.Process;
import java.util.Objects;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static Context context;
    Vibrator mVibrator;
    private SensorManager sensorManager;
    private Sensor prox;
    boolean stop;
    int count;
    private GLSurfaceView glSurfaceView;
    private DotMatrix dotMatrix;

    private static boolean isLibraryLoaded;
    public static void loadLibrary(){
        if(!isLibraryLoaded) {
            System.loadLibrary("intmob");
            isLibraryLoaded = true;
        }
    }

    // Static block is only called once when the class itself is initialized.
    // Used to load the 'intmob' library on application startup.
    static {
        loadLibrary();
        System.out.println("MainActivity load intmob library done");
    } // JNI Library Load

    public MainActivity(){
        context = this;
    }

    EventHandler m_eventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("super.onCreate(savedInstanceState) begin");
        super.onCreate(savedInstanceState);
        System.out.println("super.onCreate(savedInstanceState) done");

        // requestFeature() must be called before adding content
        // or get runtime exception
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        dotMatrix = new DotMatrix();

        if(chmod777() != 0){
            System.out.println("err:chmod777 fail. return");
            return;
        }

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        setContentView(R.layout.activity_main);

        prox = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        m_eventHandler = new EventHandler();

        // Thread Start
        new SevenSegmentThread().start();
        new DipSWThread().start();
        new KeypadThread().start();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        glSurfaceView = findViewById(R.id.glSurfaceView);
        if(glSurfaceView == null){
            throw new RuntimeException("err:glSurfaceView null");
        }

        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new Renderer2());

        System.out.println("-------------------------- onCreate done --------------------------------");
    }

    public class Renderer2 implements Renderer {
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            if(init() != 0){
                System.err.println("err:jni function init fail");
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
    }

    public static class EventHandler extends Handler{
        public void handleMessage(@NonNull Message msg){
            System.out.println("EventHandler:handle message...");

            try{
                if(msg.what == 2){
                    OLED.displayImage();
                }
            }
            catch(Exception ex){
                Log.e("handleMessage", ex.toString());
            }
        }
    }

    class SevenSegmentThread extends DaemonThread{
        @Override
        public void run(){
            try{
                while(!stop) {

                    if(!paused) {
                        if (Segment.set7SegmentNumber(count) != 0) {
                            sleep(1000);
                        }
                    }

                    sleep(1);
                }

            } catch (InterruptedException ex) {
                Log.e("7seg", ex.toString());
            }
        }
    }

    private class DipSWThread extends DaemonThread {
        @Override
        public void run() {
            int value = DipSW.GetValue();
            if(value < 0){
                System.out.println("err0:DipSW.GetValue returned "+value);
                return;
            }
            while(!stop){
                int ret = DipSW.GetValue();
                if(ret < 0){
                    System.out.println("err1:DipSW.GetValue returned "+value);
                    return;
                }
                if(value != ret){
                    value = ret;
                    System.out.println("DipSW value changed: "+value);

                    // Write code below which will be executed every dip switch value change

                    if(value == 1){

                        // vibrate in 300ms
                        mVibrator.vibrate(300);

                    }
                    else if(value == 2){

                        //FATAL EXCEPTION: Thread-104
                        //java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()

                        // Show image on OLED
                        Message msg0 = m_eventHandler.obtainMessage();
                        msg0.what = 2;
                        m_eventHandler.sendMessage(msg0);


                    }
                    else if(value == 4){
                        dotMatrix.startf("HANBACKK.");
                    }

                }
                try {

                    // Poll dipsw value every second.
                    sleep(1000);

                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }


    // Program exit
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // KEYCODE_BACK is a back button on the table board.
        if(keyCode == KeyEvent.KEYCODE_BACK){
            stop = true;
        }
        return super.onKeyDown(keyCode, event);
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


    @Override
    protected void onResume() {
        super.onResume();

        paused = false;

        System.out.println("onResume");

        if(prox != null){
            sensorManager.registerListener(this, prox,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        glSurfaceView.onResume();
        enterFullScreenMode();
    }

    private boolean paused;

    @Override
    protected void onPause() {
        super.onPause();

        paused = true;

        System.out.println("onPause");

        glSurfaceView.onPause();

        LED.off();

        sensorManager.unregisterListener(this);
        super.onStop();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void onSensorChanged(SensorEvent event){
        switch(event.sensor.getType()){
            case Sensor.TYPE_PROXIMITY:

                // 0.0 means near
                // 5.0 means far
                float proximity = event.values[0];

                Log.d("proximity", String.valueOf(proximity));

                break;
        }
    }

    private class KeypadThread extends DaemonThread{
        @Override
        public void run() {
            try{
                while(!stop){
                    Thread.sleep(1);
                    String keypadInput = Keypad.read();
                    if(keypadInput == null){
                        return;
                    }

                    if(Objects.equals(keypadInput, "open:Permisson denied")){
                        // if permission denied while opening a device
                        System.err.println(keypadInput);
                        return;
                    }

                    System.out.println("keypad pressed: '" + keypadInput + "'");
                    if(handleKeypadInput(keypadInput) != 0){
                        return;
                    }
                }
            }
            catch(Exception ex){
                Log.e("keypad", ex.toString());
            }
        }
    }

    public static Random dice = new Random();

    int handleKeypadInput(String key){
        // keypad key mapping:
        // 1 2 3 ?
        // 4 5 6 ?
        // 7 8 9 ?
        // ? 0 ? A

        // ? is denoted as not recognized key.
        // That means, even if the user press that key, the program cannot detect, poll or listen it.

        if(Objects.equals(key, "1")){
            // 1


        }
        else if(Objects.equals(key, "2")){
            // 2 (up)

            setDirectionUp();
        }
        else if(Objects.equals(key, "3")){
            // 3

            setScore(dice.nextInt(1000000));
        }
        else if(Objects.equals(key, "4")){
            // 4 (left)
            FLED.random();
        }
        else if(Objects.equals(key, "5")){
            // 5 (down)
            setDirectionDown();
        }
        else if(Objects.equals(key, "6")){
            // 6 (right)
            setDirectionRight();
        }
        else if("7".equals(key)){
            // 7

            // Set cursor home.
            assert TextLCD.IOCtlReturnHome() >= 0;
        }
        else if(Objects.equals(key, "8")){
            // 8

            // Clear TextLCD.
            assert TextLCD.IOCtlClear() >= 0;
        }
        else if(Objects.equals(key, "9")){
            // 9

            // Hide cursor.
            assert TextLCD.IOCtlCursor(false) >= 0;
        }
        else if(Objects.equals(key, ":")){
            // 0

            // Show cursor.
            assert TextLCD.IOCtlCursor(true) >= 0;
        }
        else if(Objects.equals(key, "=")){
            // A

            assert TextLCD.write("0123456789ABCDEFGHIJ") == 0;
        }
        else{
            System.err.println("err:Unknown keypad input. key='"+key+"', length="+key.length());
            return 1;
        }
        return 0;
    }

    private void enterFullScreenMode() {
        Window window =getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.getDecorView().setSystemUiVisibility(
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

    public static int chmod777() {
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
            Log.e("chmod", ex.toString());
            return 1;
        }
        return 0;
    }

    /**
     * A native method that is implemented by the 'intmob' native library,
     * which is packaged with this application.
     */
    private native int init();
    private native int step();
    private native int setDirection(int direction);
    private native int setOrthographicMatrix(int width, int height);
}
