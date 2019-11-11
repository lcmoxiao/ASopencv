package com.example.wocaowocao.recordservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.wocaowocao.CMD;
import com.example.wocaowocao.R;



public class rFloatService extends Service {

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
    void initWindow(){
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        float_img = new ImageView(this);
        float_img.setImageResource(R.color.colorRecordingWait);
        windowManager.addView(float_img, CMD.floatParams);
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
                        CMD.RparamX = CMD.floatParams.x += mTouchCurrentX - lastX;
                        CMD.RparamY = CMD.floatParams.y += mTouchCurrentY - lastY;
                        windowManager.updateViewLayout(float_img, CMD.floatParams);
                        lastX = (int)mTouchCurrentX;
                        lastY = (int)mTouchCurrentY;
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getRawX();
                        y2 = event.getRawY();
                        double distance = Math.sqrt(Math.abs(x1-x2)*Math.abs(x1-x2)+Math.abs(y1-y2)*Math.abs(y1-y2));//两点之间的距离
                        Log.i("i", "x1 - x2>>>>>>"+ distance);
                        if (distance < 15) { // 距离较小，当作click事件来处理
                            if(!CMD.isRecording) {
                                //点击后开始录制
                                CMD.isRecording = true;
                                startService(new Intent(rFloatService.this, RecordService.class));
                                //更新执行颜色
                                float_img.setImageResource(R.color.colorExecuting);
                                windowManager.updateViewLayout(float_img, CMD.floatParams);
                                Toast.makeText(getBaseContext(), "准备就绪", Toast.LENGTH_SHORT).show();
                                return true;
                            }else {
                                Log.e("xxx", "下一次录制就绪");
                                stopService(new Intent(rFloatService.this, RecordService.class));
                                float_img.setImageResource(R.color.colorRecordingWait);
                                windowManager.updateViewLayout(float_img, CMD.floatParams);
                                startService(new Intent(rFloatService.this, RecordService.class));
                            }
                        } else {
                            if(!CMD.isRecording) {
                                Toast.makeText(getBaseContext(), "请找个好点的位置", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                CMD.isRecording = false;
                                Toast.makeText(getBaseContext(), "录制结束", Toast.LENGTH_SHORT).show();
                                stopService(new Intent(rFloatService.this, RecordService.class));
                                float_img.setImageResource(R.color.colorRecordingWait);
                                windowManager.updateViewLayout(float_img, CMD.floatParams);
                                return true ;
                            }
                        }
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


