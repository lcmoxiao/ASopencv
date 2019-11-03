package com.example.wocaowocao.floatwin;

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

import com.example.wocaowocao.R;


public class FloatService extends Service {
    private static final String TAG = "MyService";

    //绑定的图片
    ImageView float_img;
    //布局参数.
    WindowManager.LayoutParams params;
    //实例化的WindowManager.
    WindowManager windowManager;
    private float x1,y1,x2,y2,mTouchCurrentX,mTouchCurrentY;
    private int lastX, lastY;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "into MyService onCreate");
        createToucher();
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
        //params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 0;

        //设置悬浮窗口长宽数据.
        params.width =200;// 设置悬浮窗口长宽数据
        params.height =200;



        float_img = new ImageView(this);
        float_img.setImageResource(R.color.colorPrimary);

        windowManager.addView(float_img, params);

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
                        params.x += mTouchCurrentX - lastX;
                        params.y += mTouchCurrentY - lastY;
                        windowManager.updateViewLayout(float_img, params);
                        lastX = (int) mTouchCurrentX;
                        lastY = (int)mTouchCurrentY;
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getRawX();
                        y2 = event.getRawY();
                        double distance = Math.sqrt(Math.abs(x1-x2)*Math.abs(x1-x2)+Math.abs(y1-y2)*Math.abs(y1-y2));//两点之间的距离
                        Log.i("i", "x1 - x2>>>>>>"+ distance);
                        if (distance < 15) { // 距离较小，当作click事件来处理
                            Toast.makeText(getBaseContext(), "点了", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            Toast.makeText(getBaseContext(), "滑了", Toast.LENGTH_SHORT).show();
                            return true ;
                        }
                }
                return false;
            }
        });

    }
}


