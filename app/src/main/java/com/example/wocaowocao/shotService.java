package com.example.wocaowocao;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.os.IBinder;


import androidx.annotation.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by dzjin on 2018/1/9.
 */
public class shotService extends Service {


    private int resultCode;
    private Intent resultData=null;

    private MediaProjection mediaProjection=null;
    private MediaRecorder mediaRecorder=null;
    private VirtualDisplay virtualDisplay=null;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try{
            resultCode=intent.getIntExtra("resultCode",-1);
            resultData=intent.getParcelableExtra("resultData");
            mScreenWidth=intent.getIntExtra("mScreenWidth",0);
            mScreenHeight=intent.getIntExtra("mScreenHeight",0);
            mScreenDensity=intent.getIntExtra("mScreenDensity",0);

            mediaProjection=createMediaProjection();
            mediaRecorder=createMediaRecorder();
            virtualDisplay=createVirtualDisplay();
            mediaRecorder.start();

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

    private MediaRecorder createMediaRecorder(){

        String filePathName= CMD.dataPath+1+".mp4";
        //Used to record audio and video. The recording control is based on a simple state machine.
        MediaRecorder mediaRecorder=new MediaRecorder();
        //Set the video source to be used for recording.
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        //Set the format of the output produced during recording.
        //3GPP media file format
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //Sets the video encoding bit rate for recording.
        //param:the video encoding bit rate in bits per second.
        mediaRecorder.setVideoEncodingBitRate(5*mScreenWidth*mScreenHeight);
        //Sets the video encoder to be used for recording.
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //Sets the width and height of the video to be captured.
        mediaRecorder.setVideoSize(mScreenWidth,mScreenHeight);
        //Sets the frame rate of the video to be captured.
        mediaRecorder.setVideoFrameRate(60);
        try{
            //Pass in the file object to be written.
            mediaRecorder.setOutputFile(filePathName);
            //Prepares the recorder to begin capturing and encoding data.
            mediaRecorder.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
        return mediaRecorder;
    }

    private VirtualDisplay createVirtualDisplay(){
        return mediaProjection.createVirtualDisplay("mediaProjection",mScreenWidth,mScreenHeight,mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,mediaRecorder.getSurface(),null,null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(virtualDisplay!=null){
            virtualDisplay.release();
            virtualDisplay=null;
        }
        if(mediaRecorder!=null){
            mediaRecorder.stop();
            mediaRecorder=null;
        }
        if(mediaProjection!=null){
            mediaProjection.stop();
            mediaProjection=null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

