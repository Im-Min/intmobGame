package com.example.intmob;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.intmob.databinding.ActivityMainBinding;
import java.io.DataOutputStream;
import java.lang.Process;
import java.util.Objects;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setTextHandler = new SetTextHandler();

        thread = new Thread(this::eventloop);
        thread.start();

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
                init();
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                gl.glViewport(0, 0, width, height);
                setOrthographicMatrix(width, height);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                step();
            }
        });

        Button buttonUp = findViewById(R.id.button_up);
        Button buttonDown = findViewById(R.id.button_down);
        Button buttonLeft = findViewById(R.id.button_left);
        Button buttonRight = findViewById(R.id.button_right);

        buttonUp.setOnClickListener(v -> setDirection(0));
        buttonDown.setOnClickListener(v -> setDirection(1));
        buttonLeft.setOnClickListener(v -> setDirection(2));
        buttonRight.setOnClickListener(v -> setDirection(3));

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
    private void setText(Object text){
        Message msg1 = setTextHandler.obtainMessage(0, text);
        setTextHandler.sendMessage(msg1);
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
            return 1;
        }
        return 0;
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

    private void enterFullScreenMode() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    /**
     * A native method that is implemented by the 'intmob' native library,
     * which is packaged with this application.
     */
    private native String stringFromJNI(String event);
    private native String idev();
    private native void init();
    private native void step();
    private native void setDirection(int direction);
    private native void setOrthographicMatrix(int width, int height);

}
