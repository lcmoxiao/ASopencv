package com.example.wocaowocao.simulateservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

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


public class sFloatService extends Service {


    //绑定的图片
    ImageView float_img;
    //实例化的WindowManager.
    WindowManager windowManager;

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
                        CMD.SparamX = CMD.floatParams.x += mTouchCurrentX - lastX;
                        CMD.SparamY = CMD.floatParams.y += mTouchCurrentY - lastY;
                        windowManager.updateViewLayout(float_img, CMD.floatParams);
                        lastX = (int)mTouchCurrentX;
                        lastY = (int)mTouchCurrentY;
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getRawX();
                        y2 = event.getRawY();
                        double distance = Math.sqrt(Math.abs(x1-x2)*Math.abs(x1-x2)+Math.abs(y1-y2)*Math.abs(y1-y2));//两点之间的距离
                        if (distance < 15) { // 距离较小，当作click事件来处理
                            if(!CMD.isSimulating){
                                CMD.isSimulating = true;
                                startService(new Intent(sFloatService.this,SimulateService.class));
                                float_img.setImageResource(R.color.colorExecuting);
                                windowManager.updateViewLayout(float_img, CMD.floatParams);
                                Toast.makeText(getBaseContext(), "开始模拟", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                CMD.isSimulating = false;
                                stopService(new Intent(sFloatService.this,SimulateService.class));
                                Toast.makeText(getBaseContext(), "强制终止模拟", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        } else {
                            Toast.makeText(getBaseContext(), "一定要选个好位置", Toast.LENGTH_SHORT).show();
                            return true ;
                        }
                }
                return false;
            }
        });
    }


    void initWindow(){
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //赋值WindowManager

        float_img = new ImageView(this);
        float_img.setImageResource(R.color.colorSimulateWait);
        windowManager.addView(float_img, CMD.floatParams);
    }

    @Override
    public void onDestroy() {
        float_img.setEnabled(false);
        windowManager.removeView(float_img);
        super.onDestroy();
    }
}


