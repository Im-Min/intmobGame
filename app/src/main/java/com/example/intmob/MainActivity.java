package com.example.intmob;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intmob.fpga.DotMatrix;
import com.example.intmob.fpga.FLED;
import com.example.intmob.fpga.Keypad;
import com.example.intmob.fpga.LED;
import com.example.intmob.fpga.OLED;
import com.example.intmob.fpga.Segment;
import com.example.intmob.fpga.TextLCD;

import java.io.DataOutputStream;
import java.lang.Process;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static Context context;
    Vibrator mVibrator;
    private SensorManager sensorManager;
    private Sensor prox;
    private GLSurfaceView glSurfaceView;

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

    Handler m_eventHandler;
    private Segment segment = new Segment();
    private DotMatrix dotMatrix = new DotMatrix();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("super.onCreate(savedInstanceState) begin");
        super.onCreate(savedInstanceState);
        System.out.println("super.onCreate(savedInstanceState) done");

        // NOTE: requestFeature() must be called before adding content
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);



        new Keypad(new Keypad.KeypadHandler() {
            @Override
            public int handle(int x) {
                return handleKeypadInput(x);
            }
        }).start();


        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        setContentView(R.layout.activity_main);

        prox = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        m_eventHandler = new Handler();

        // Thread Start
        segment.start();
        dotMatrix.start();

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

    void setScore(int score){
        segment.value = score;
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
        Log.d("MainActivity", "resumed");

        enterFullScreenMode();

        glSurfaceView.onResume();

        if(prox != null){
            sensorManager.registerListener(this, prox, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "paused");

        glSurfaceView.onPause();

        sensorManager.unregisterListener(this);
        super.onStop();
    }


    int handleKeypadInput(int key){
        // keypad key mapping:
        // 1 2 3 ?
        // 4 5 6 ?
        // 7 8 9 ?
        // ? 0 ? 10

        // ? is denoted as not recognized key.
        // That means, even if the user press that key, the program cannot detect, poll or listen it.

        switch (key) {
            case 1:
                TextLCD.TextLCDOut(" HaNbAcK ", " eLeCtRoNiCs!! ");
                break;
            case 2:
                TextLCD.IOCtlDisplay(true);
                break;
            case 3:
                TextLCD.IOCtlDisplay(false);
                break;
            case 4:
                dotMatrix.startf("PACMAN", 18);
                break;
            case 5:
                TextLCD.IOCtlBlink(false);
                break;
            case 6:
                TextLCD.IOCtlBlink(true);
                break;
            case 7:
                TextLCD.IOCtlReturnHome();
                break;
            case 8:
                TextLCD.IOCtlClear();
                break;
            case 9:
                TextLCD.IOCtlCursor(false);
                break;
            case 0:
                TextLCD.IOCtlCursor(true);
                break;
            case 10:
                TextLCD.write(
                        nextString(dice, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", 20)
                );
                break;
        }

        Log.d("keypad", "keypad input="+key);
        return 0;
    }

    public static String nextString(Random rng, String characters, int length)
    {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
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



    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void onSensorChanged(SensorEvent event){
        switch(event.sensor.getType()){
            case Sensor.TYPE_PROXIMITY:
                float proximity = event.values[0]; // 0=near, 5=far
                Log.d("proximity", "proximity="+proximity);
                break;
        }
    }

    public static Random dice = new Random();

    /**
     * A native method that is implemented by the 'intmob' native library,
     * which is packaged with this application.
     */
    private native int init();
    private native int step();
    private native int setDirection(int direction);
    private native int setOrthographicMatrix(int width, int height);
}
