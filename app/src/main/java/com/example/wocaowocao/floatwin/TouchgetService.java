package com.example.wocaowocao.floatwin;

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

import com.example.wocaowocao.R;

import java.io.DataOutputStream;



public class TouchgetService extends Service {

    static DataOutputStream os = null;
    //绑定的图片
    ImageView float_img;
    //布局参数.
    WindowManager.LayoutParams params;
    //实例化的WindowManager.
    WindowManager windowManager;
    private float x,y;


    @Override
    public void onCreate() {
        super.onCreate();
        try {
            os = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
            createToucher();
        } catch (Exception e) {
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
        params.height =getResources().getDisplayMetrics().heightPixels;

        float_img = new ImageView(this);
        float_img.setImageResource(R.color.colorClear);

        windowManager.addView(float_img, params);

        float_img.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN){
                    x =  event.getRawX();//得到相对应屏幕左上角的坐标
                    y =  event.getRawY();
                    if(x<200&&y<200)
                    {
                        Toast.makeText(getBaseContext(), "停止服务", Toast.LENGTH_SHORT).show();
                        float_img.setEnabled(false);
                        stopSelf();
                        return false;
                    }
                     else Toast.makeText(getBaseContext(), "点击的是"+x+"??"+y, Toast.LENGTH_SHORT).show();

                   return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        float_img.setEnabled(false);
        windowManager.removeView(float_img);
        super.onDestroy();
    }
}


