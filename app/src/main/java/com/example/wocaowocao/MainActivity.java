package com.example.wocaowocao;


import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;

import com.example.wocaowocao.Base.BaseActivity;
import com.example.wocaowocao.Base.ViewInject;
import com.example.wocaowocao.recogImg.recogImgActivity;
import com.example.wocaowocao.recogImg.useOpencv;
import com.example.wocaowocao.recordservice.rFloatService;
import com.example.wocaowocao.simulateservice.sFloatService;
import com.google.android.material.snackbar.Snackbar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

import butterknife.BindView;

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

    BroadcastReceiver receiver ;

    IntentFilter intentfilter;


    @Override
    public void afterBindView()  {
        initClick();
        initPermission();
        useOpencv.staticLoadCVLibraries();
        initFloatParams();
        initFilter();
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
                Bitmap bitmap ;
                try {

                    long startTime=System.currentTimeMillis();

                    bitmap = BitmapFactory.decodeStream( new FileInputStream(new File(dataPath + "MOV1/images/", 1 + ".png")));
                    Log.e("xx",  bitmap.getHeight()+"");
                    Log.e("xx",  bitmap.getWidth()+"");

                    long endTime=System.currentTimeMillis();
                    Log.e("xx",  +(endTime - startTime)+"ms");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });


    }



    private void initFilter()
    {
        Intent intent = new Intent();
        intent.setAction("finishShot");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    CMD.screen = CMD.Shot11(getWindow().getDecorView(), Objects.requireNonNull(intent.getExtras()).getInt("motivationNub"));
                    sendBroadcast(intent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        intentfilter = new IntentFilter();
        intentfilter.addAction("shot");
        registerReceiver(receiver,intentfilter);
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
        unregisterReceiver(receiver);


    }
}