package com.example.wocaowocao;


import android.Manifest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.wocaowocao.Base.BaseActivity;
import com.example.wocaowocao.Base.ViewInject;
import com.example.wocaowocao.receiver.finishedReceiver;
import com.example.wocaowocao.receiver.shotReceiver;
import com.example.wocaowocao.recogImg.recogImgActivity;
import com.example.wocaowocao.recogImg.useOpencv;
import com.example.wocaowocao.recordservice.rFloatService;
import com.example.wocaowocao.simulateservice.sFloatService;
import com.google.android.material.snackbar.Snackbar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;

import static com.example.wocaowocao.CMD.LBmanager;
import static com.example.wocaowocao.CMD.dataPath;
import static com.example.wocaowocao.CMD.delFile;
import static com.example.wocaowocao.CMD.exec;
import static com.example.wocaowocao.CMD.initFloatParams;


@ViewInject(main_layout_id = R.layout.activity_main)
public class MainActivity extends BaseActivity {
    @BindView(R.id.select_btn1)
    Button selectBtn1;
    @BindView(R.id.select_btn2)
    Button selectBtn2;
    @BindView(R.id.activity_main)
    LinearLayout mLayout;
    @BindView(R.id.select_btn3)
    Button selectBtn3;
    @BindView(R.id.select_btn4)
    Button selectBtn4;
    @BindView(R.id.select_btn5)
    Button selectBtn5;

    // 是否打开录制悬浮窗
    Boolean isrFloating = false;
    // 是否打开模拟悬浮窗
    Boolean issFloating = false;

    com.example.wocaowocao.receiver.shotReceiver shotReceiver = new shotReceiver();
    com.example.wocaowocao.receiver.finishedReceiver finishedReceiver = new finishedReceiver();
    IntentFilter shotIntentFilter;
    IntentFilter finishedIntentFilter;
    Intent ShotedIntent = new Intent();

    @Override
    public void afterBindView()  {
        initClick();
        initPermission();
        useOpencv.staticLoadCVLibraries();
        initFloatParams();
        initBroadCast();
    }

    public void xxx(){

        LBmanager.sendBroadcast(ShotedIntent);
    }

    private void initBroadCast()
    {
        CMD.mContext =this;
        LBmanager = LocalBroadcastManager.getInstance(this);
        shotIntentFilter = new IntentFilter();
        shotIntentFilter.addAction("shot");
        LBmanager.registerReceiver(shotReceiver,shotIntentFilter);
        finishedIntentFilter = new IntentFilter();
        finishedIntentFilter.addAction("finished");
        LBmanager.registerReceiver(finishedReceiver,finishedIntentFilter);
        ShotedIntent.setAction("finished");
        ShotedIntent.setComponent( new ComponentName( "com.example.wocaowocao" , "com.example.wocaowocao.receiver.finishedReceiver") );
    }

    void initClick() {
        selectBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, recogImgActivity.class));
            }
        });
        selectBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isrFloating)
                {
                    stopService(new Intent(MainActivity.this, rFloatService.class));
                    isrFloating=false;
                }
                else {
                    startService(new Intent(MainActivity.this, rFloatService.class));
                    isrFloating=true;
                }
            }
        });

        selectBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(issFloating)
                {
                    stopService(new Intent(MainActivity.this, sFloatService.class));
                    issFloating=false;
                }
                else {
                    startService(new Intent(MainActivity.this, sFloatService.class));
                    issFloating=true;
                }
            }
        });
        selectBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delFile(dataPath+"MOV1");
            }
        });

        selectBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });


    }








    // 初始化权限
    private void initPermission() {
        getPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);
        getPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        try (DataOutputStream os = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream())){
            exec("\"chmod 777 \"+getPackageCodePath()", os);
        } catch (Exception e) { e.printStackTrace(); }
    }

    //获取读写的权限
    private void getPermission(String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mLayout,
                    "你可以" + permission,
                    Snackbar.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Snackbar.make(mLayout, "尝试获取权限" + permission, Snackbar.LENGTH_INDEFINITE).show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, 67);
            } else {
                Snackbar.make(mLayout, "你选择过了" + permission, Snackbar.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, 67);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LBmanager.unregisterReceiver(shotReceiver);
        Log.e("xxx","Activity销毁");

    }
}