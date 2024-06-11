package com.example.intmob;

import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import java.io.DataOutputStream;
import java.lang.Process;
import java.util.Objects;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;

    // Used to load the 'intmob' library on application startup.
    static {
        System.loadLibrary("intmob");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        System.out.println("test: " + div0());

        new Thread(this::eventloop).start();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        glSurfaceView = findViewById(R.id.glSurfaceView);
        if(glSurfaceView == null){
            throw new RuntimeException("[ERROR] glSurfaceView is null!");
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
                if(ret != 0){
                    System.out.println("step:" + ret);
                }
            }
        });
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
        // keypad:
        // 1 2 3 X
        // 4 5 6 X
        // 7 8 9 X
        // X 0 X A

        // X is an unused key.

        if(Objects.equals(key, "1")){
            // 1
        }
        else if(Objects.equals(key, "2")){
            // 2
            if(setDirection(0) != 0){
                System.err.println("setDirection error");
            }
        }
        else if(Objects.equals(key, "3")){
            // 3
        }
        else if(Objects.equals(key, "4")){
            // 4
            if(setDirection(2) != 0){
                System.err.println("setDirection error");
            }
        }
        else if(Objects.equals(key, "5")){
            // 5
            if(setDirection(1) != 0){
                System.err.println("setDirection error");
            }
        }
        else if(Objects.equals(key, "6")){
            // 6
            if(setDirection(3) != 0){
                System.err.println("setDirection error");
            }
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
