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

import java.io.File;
import java.io.IOException;

public class RecordService extends Service {


    //绑定的图片
    static ImageView record_img;
    //布局参数.
    static WindowManager.LayoutParams params;
    //实例化的WindowManager.
    static WindowManager windowManager;
    private float x,y;
    //动作标号
    int motivationNub = 1;
    Thread t=Thread.currentThread();

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            try {
                initWriteFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            CMD.WriteIGInit();
            createToucher();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 初始化目录
    private void initWriteFile() throws Exception {
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

        record_img = new ImageView(this);
        record_img.setImageResource(R.color.colorClear);


        windowManager.addView(record_img, params);

        record_img.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN){

                    //点到之后，会进行截图和记录触点，为了保证截图效果，会延时0.5s。
                    //之后解除view，等待操作。
                    x = event.getRawX();//得到相对应屏幕左上角的坐标
                    y = event.getRawY();
                    //记录动作
                    CMD.Shot(motivationNub);
                    CMD.WriteGesture((int) x, (int) y, motivationNub);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    windowManager.removeView(record_img);
                    rFloatService.float_img.setImageResource(R.color.colorPrimary);
                    rFloatService.windowManager.updateViewLayout(rFloatService.float_img,rFloatService.params);
                    motivationNub++;
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        try {
            record_img.setEnabled(false);
            CMD.WriteIGDestroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}


