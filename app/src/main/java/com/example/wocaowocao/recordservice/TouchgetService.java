package com.example.wocaowocao.recordservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.wocaowocao.CMD;
import com.example.wocaowocao.R;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TouchgetService extends Service {


    //绑定的图片
    ImageView float_img;
    //布局参数.
    WindowManager.LayoutParams params;
    //实例化的WindowManager.
    WindowManager windowManager;
    private float x,y;
    //动作标号
    int motivationNub = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            createToucher();
            startService(new Intent(TouchgetService.this, rFloatService.class));
            CMD.WriteIGInit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("ClickableViewAccessibility")
    private void createToucher() {
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);

        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 0;

        //设置悬浮窗口长宽数据.
        params.width =getResources().getDisplayMetrics().widthPixels;// 设置悬浮窗口长宽数据
        params.height =getResources().getDisplayMetrics().heightPixels+200;

        float_img = new ImageView(this);
        float_img.setImageResource(R.color.colorClear);
        windowManager.addView(float_img, params);
        //刷新上层悬浮窗权限
        rFloatService.windowManager.removeView(rFloatService.float_img);
        rFloatService.windowManager.addView(rFloatService.float_img, rFloatService.params);
        float_img.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN){
                    try {
                        //设置图像失效
                        windowManager.removeView(float_img);
                        windowManager.updateViewLayout(float_img, params);
                        x = event.getRawX();//得到相对应屏幕左上角的坐标
                        y = event.getRawY();
                        //记录动作
                        CMD.Shot(motivationNub);
                        CMD.WriteGesture((int) x, (int) y, motivationNub);
                        //记录触点
                        motivationNub++;
                        Toast.makeText(getBaseContext(), "MOV"+motivationNub+"录制成功"+x+","+y, Toast.LENGTH_SHORT).show();
                        Thread.sleep(80);
                        {
                            windowManager.addView(float_img, params);
                            //刷新上层悬浮窗权限
                            rFloatService.windowManager.removeView(rFloatService.float_img);
                            rFloatService.windowManager.addView(rFloatService.float_img, rFloatService.params);
                        }
                        return true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        try {
            float_img.setEnabled(false);
            windowManager.removeView(float_img);
            CMD.WriteIGDestroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}


