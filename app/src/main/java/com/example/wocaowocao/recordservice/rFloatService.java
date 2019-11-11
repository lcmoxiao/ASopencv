package com.example.wocaowocao.recordservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
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


public class rFloatService extends Service {

    //绑定的图片
    static ImageView float_img;
    //布局参数.
    static WindowManager.LayoutParams params;
    //实例化的WindowManager.
    static WindowManager windowManager;


    private float x1,y1,x2,y2,mTouchCurrentX,mTouchCurrentY;
    private int lastX, lastY;


    @Override
    public void onCreate() {
        super.onCreate();

        createToucher();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @SuppressLint("ClickableViewAccessibility")
    private void createToucher() {
        initWindow();


        float_img.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        x1 =  event.getRawX();//得到相对应屏幕左上角的坐标
                        y1 =  event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mTouchCurrentX = (int) event.getRawX();
                        mTouchCurrentY = (int) event.getRawY();
                        CMD.RparamX = params.x += mTouchCurrentX - lastX;
                        CMD.RparamY = params.y += mTouchCurrentY - lastY;
                        windowManager.updateViewLayout(float_img, params);
                        lastX = (int)mTouchCurrentX;
                        lastY = (int)mTouchCurrentY;
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getRawX();
                        y2 = event.getRawY();
                        double distance = Math.sqrt(Math.abs(x1-x2)*Math.abs(x1-x2)+Math.abs(y1-y2)*Math.abs(y1-y2));//两点之间的距离
                        Log.i("i", "x1 - x2>>>>>>"+ distance);
                        if (distance < 15) { // 距离较小，当作click事件来处理
                            if(!CMD.isRecording)
                            {
                                startService(new Intent(rFloatService.this, RecordService.class));
                                CMD.isRecording = true;
                            }
                            else
                            {
                                float_img.setImageResource(R.color.colorRecording);
                                windowManager.updateViewLayout(float_img, params);
                                RecordService.windowManager.addView(RecordService.record_img, RecordService.params);
                            }
                            return true;
                        } else {
                            if(!CMD.isRecording)
                            {
                                Toast.makeText(getBaseContext(), "找个好位置", Toast.LENGTH_SHORT).show();
                                return true ;
                            }
                            else
                            {
                                Toast.makeText(getBaseContext(), "结束录制", Toast.LENGTH_SHORT).show();
                                float_img.setImageResource(R.color.colorRecording);
                                windowManager.updateViewLayout(float_img, params);
                                stopService(new Intent(rFloatService.this, RecordService.class));
                                return true ;
                            }
                        }
                }
                return false;
            }
        });
    }

    void initWindow(){
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        //设置效果为背景透明.
        //params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //设置窗口初始停靠位置.
        params.gravity = Gravity.TOP | Gravity.START;
        params.x =CMD.RparamX;
        params.y =CMD.RparamY;
        //设置悬浮窗口长宽数据.
        params.width =100;// 设置悬浮窗口长宽数据
        params.height =100;
        float_img = new ImageView(this);
        float_img.setImageResource(R.color.colorRecording);
        windowManager.addView(float_img, params);
    }

    @Override
    public void onDestroy() {
        float_img.setEnabled(false);
        windowManager.removeView(float_img);
        super.onDestroy();
    }
}


