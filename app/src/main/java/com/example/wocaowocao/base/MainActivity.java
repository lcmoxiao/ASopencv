package com.example.wocaowocao.base;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.core.app.ActivityCompat;

import com.example.wocaowocao.R;
import com.example.wocaowocao.elf.CoreActivity;
import com.example.wocaowocao.fileManager.depositoryActivity;
import com.example.wocaowocao.recogImg.recogImgActivity;
import com.example.wocaowocao.recogImg.useOpencv;

import java.io.DataOutputStream;

import butterknife.BindView;

import static com.example.wocaowocao.base.CMD.exec;
import static com.example.wocaowocao.base.CMD.initFloatParams;


@ViewInject(main_layout_id = R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @BindView(R.id.main_btn1)
    Button selectBtn1;
    @BindView(R.id.main_btn2)
    Button selectBtn2;
    @BindView(R.id.main_btn3)
    Button mainBtn3;

    @BindView(R.id.activity_main)
    FrameLayout mLayout;


    @Override
    public void afterBindView() {
        initClick();
        initPermission();
        useOpencv.staticLoadCVLibraries();
        initFloatParams();
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
                startActivity(new Intent(MainActivity.this, CoreActivity.class));
            }
        });
        mainBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, depositoryActivity.class));
            }
        });
    }

    // 初始化权限
    private void initPermission() {
        //检查是否已经授予悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 67);
        }
        //读写权限检查
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 67);
        }
        try (DataOutputStream os = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream())) {
            exec("\"chmod 777 \"+getPackageCodePath()", os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("xxx", "Activity销毁");

    }


}