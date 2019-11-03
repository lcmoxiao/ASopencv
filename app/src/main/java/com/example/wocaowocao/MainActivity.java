package com.example.wocaowocao;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;

import com.example.wocaowocao.Base.BaseActivity;
import com.example.wocaowocao.Base.ViewInject;
import com.example.wocaowocao.floatwin.floatwinActivity;
import com.example.wocaowocao.recogImg.recogImgActivity;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;


@ViewInject(main_layout_id = R.layout.activity_main)
public class MainActivity extends BaseActivity {


    @BindView(R.id.select_btn1)
    Button selectBtn1;
    @BindView(R.id.select_btn2)
    Button selectBtn2;
    @BindView(R.id.activity_main)
    LinearLayout mLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void afterBindView() {
        initClick();
        initPermission();



    }






    void initClick(){
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
    }

    private void initPermission(){
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