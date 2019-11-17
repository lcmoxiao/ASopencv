package com.example.wocaowocao.elf;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import java.nio.ByteBuffer;



/**
 * Created by dzjin on 2018/1/9.
 */
public class shotService extends Service {


    private int resultCode;
    private Intent resultData=null;

    private MediaProjection mediaProjection=null;
    private VirtualDisplay virtualDisplay=null;
    static ImageReader mImageReader;
    static Image image;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try{
            resultCode=intent.getIntExtra("resultCode",-1);
            resultData=intent.getParcelableExtra("resultData");
            int mScreenWidth = intent.getIntExtra("mScreenWidth", 0);
            int mScreenHeight = intent.getIntExtra("mScreenHeight", 0);
            int mScreenDensity = intent.getIntExtra("mScreenDensity", 0);
            mediaProjection=createMediaProjection();
            mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 1);
            virtualDisplay=mediaProjection.createVirtualDisplay("ScreenShotDemo",
                    mScreenWidth, mScreenHeight, mScreenDensity,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return Service.START_NOT_STICKY;
    }

    //createMediaProjection
    public MediaProjection createMediaProjection(){

        return ((MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE))
                .getMediaProjection(resultCode,resultData);

    }


    public static Bitmap startCapture() {

        mImageReader.getSurface();
        image = mImageReader.acquireNextImage();

        int width = image.getWidth();

        int height = image.getHeight();

        final Image.Plane[] planes = image.getPlanes();

        final ByteBuffer buffer = planes[0].getBuffer();

        int pixelStride = planes[0].getPixelStride();

        int rowStride = planes[0].getRowStride();

        int rowPadding = rowStride - pixelStride * width;

        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);

        bitmap.copyPixelsFromBuffer(buffer);

        image.close();

        return Bitmap.createBitmap(bitmap, 0, 0, width, height);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        if(image!=null){
            image.close();
            image=null;
        }
        if(mImageReader!=null){
            mImageReader.close();
            mImageReader=null;
        }
        if(virtualDisplay!=null){
            virtualDisplay.release();
            virtualDisplay=null;
        }

        if(mediaProjection!=null){
            mediaProjection.stop();
            mediaProjection=null;
        }
        //Toast.makeText(getApplicationContext(), "截屏service已注销", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}