package com.example.wocaowocao;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.wocaowocao.Base.BaseActivity;
import com.example.wocaowocao.Base.ViewInject;
import com.example.wocaowocao.floatwin.floatwinActivity;
import com.example.wocaowocao.recogImg.recogImgActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import butterknife.BindView;


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

    @Override
    public void afterBindView() throws Exception {
        initClick();
        initPermission();
        initFile();
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
                startActivity(new Intent(MainActivity.this, floatwinActivity.class));
            }
        });
        selectBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






            }
        });
    }

    // 初始化目录
    private void initFile() throws Exception {
        File f1 = new File(CMD.dataPath);
        if (!f1.exists()) {
            if (!f1.mkdirs()) throw new Exception("你创建不了文件夹");
        }
        File f2 = new File(CMD.dataPath + "MOV1");
        if (!f2.exists()) {
            if (!f2.mkdirs()) throw new Exception("你创建不了文件夹");
        }
        File f3 = new File(CMD.dataPath + "MOV1/images");
        if (!f3.exists()) {
            if (!f3.mkdirs()) throw new Exception("你创建不了文件夹");
        }
        File f4 = new File(CMD.dataPath, "MOV1/gesture.txt");
        if (!f4.exists()) {
            if (!f4.createNewFile()) throw new Exception("你创建不了文件");
        }
    }

    // 初始化权限
        private void initPermission() {
        try (DataOutputStream os = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream())){
            CMD.exec("\"chmod 777 \"+getPackageCodePath()", os);
        } catch (Exception e) { e.printStackTrace(); }
        getPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);
        getPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
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
                ActivityCompat.requestPermissions(this, new String[]{permission}, 67);
            }
        }
    }

}