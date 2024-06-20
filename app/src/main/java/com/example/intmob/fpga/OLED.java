package com.example.intmob.fpga;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.example.intmob.MainActivity;
import com.example.intmob.R;

public class OLED extends Activity {
    static{
        MainActivity.loadLibrary();
    }

    public static native int OLEDImage(int[] image, int width, int height);

    private static java.io.InputStream is0;
    private static Bitmap bm;

    private static int[] pixels;
    private static int width, height;



    // err:OLED does not work at all.
    public static int displayImage() {

        // Draw Picture
        Resources resources = MainActivity.context.getResources();
        is0 = resources.openRawResource(R.raw.pacman);

        bm = BitmapFactory.decodeStream(is0);

        // create a deep copy of it using getPixels() into different configs
        width = bm.getWidth();
        height = bm.getHeight();
        pixels = new int[width * height];
        bm.getPixels(pixels, 0, width, 0, 0, width, height);

        Handler mHandle = new Handler();
        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                int ret = OLEDImage(pixels, width, height);
                if(ret < 0){
                    System.out.println("err:OLEDImage() returned "+ ret);
                }
                else{
                    System.out.println("OLEDImage() done");
                }
            }
        }, 1000);

        System.out.println("displayImage() done");

        return 0;
    }

}
