package com.example.wocaowocao;


import android.content.Context;
import android.content.Intent;

import android.media.projection.MediaProjectionManager;

import android.os.Build;

import android.os.Bundle;
import android.util.DisplayMetrics;

import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


public class shotActivity extends AppCompatActivity {
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getScreenBaseInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == RESULT_OK){
                //获得录屏权限，启动Service进行录制
                Intent intent=new Intent(shotActivity.this,shotService.class);
                intent.putExtra("resultCode",resultCode);
                intent.putExtra("resultData",data);
                intent.putExtra("mScreenWidth",mScreenWidth);
                intent.putExtra("mScreenHeight",mScreenHeight);
                intent.putExtra("mScreenDensity",mScreenDensity);
                startService(intent);
                Toast.makeText(this,"录屏开始",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"录屏失败",Toast.LENGTH_SHORT).show();
            }

        }
    }

    //start screen record
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startScreenRecord(){
        //Manages the retrieval of certain types of MediaProjection tokens.
        MediaProjectionManager mediaProjectionManager=
                (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        //Returns an Intent that must passed to startActivityForResult() in order to start screen capture.
        Intent permissionIntent=mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(permissionIntent,1000);
    }

    //stop screen record.
    private void stopScreenRecord(){
        Intent service = new Intent(this, shotService.class);
        stopService(service);
        Toast.makeText(this,"录屏成功",Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取屏幕基本信息
     */
    private void getScreenBaseInfo() {
        //A structure describing general information about a display, such as its size, density, and font scaling.
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mScreenDensity = metrics.densityDpi;
    }

}
