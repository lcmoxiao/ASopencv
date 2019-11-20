package com.example.wocaowocao;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.wocaowocao.base.BaseActivity;
import com.example.wocaowocao.base.CMD;
import com.example.wocaowocao.base.ViewInject;
import com.example.wocaowocao.elf.CoreActivity;
import com.example.wocaowocao.depository.DepositoryActivity;
import com.example.wocaowocao.recogImg.recogImgActivity;
import com.example.wocaowocao.recogImg.useOpencv;
import com.google.android.material.snackbar.Snackbar;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import butterknife.BindView;

import static com.example.wocaowocao.base.CMD.initFloatParams;
import static com.example.wocaowocao.base.CMD.isRoot;



@ViewInject(main_layout_id = R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @BindView(R.id.main_btn1)
    Button mainBtn1;
    @BindView(R.id.main_btn2)
    Button mainBtn2;
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
        mainBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, recogImgActivity.class));
            }
        });

        mainBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRoot())Toast.makeText(getApplicationContext(),"没ROOT不给用", Toast.LENGTH_SHORT).show();
                else startActivity(new Intent(MainActivity.this, CoreActivity.class));
            }
        });
        mainBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DepositoryActivity.class));
            }
        });
    }

    // 初始化权限
    private void initPermission() {
        if(!Settings.canDrawOverlays(this)) {
            Toast.makeText(getApplicationContext(), "麻烦手动开启一下悬浮窗权限 ", Toast.LENGTH_SHORT).show();
            finish();
        }
        //读写权限检查
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 67);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 67) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),"授权成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),"不给权限拉倒", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("xxx", "Activity销毁");
    }
}