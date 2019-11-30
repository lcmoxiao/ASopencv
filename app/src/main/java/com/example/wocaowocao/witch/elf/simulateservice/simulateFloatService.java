package com.example.wocaowocao.witch.elf.simulateservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.wocaowocao.base.CMD;
import com.example.wocaowocao.R;
import com.example.wocaowocao.recogImg.useOpencv;
import com.example.wocaowocao.witch.elf.CoreActivity;

import static com.example.wocaowocao.witch.elf.CoreActivity.elfManager;
import static com.example.wocaowocao.witch.elf.shotService.startCapture;
import static com.example.wocaowocao.witch.elfDepository.DepositoryActivity.MOVnub;

public class simulateFloatService extends Service {


    //绑定的图片
    ImageView float_img;
    //实例化的WindowManager.
    WindowManager windowManager;
    //悬浮窗的参数
    public WindowManager.LayoutParams floatParams;
    //是否在模拟操作
    public static Boolean isSimulating = false;



    //读取的动作位置
    int imagesNub;
    boolean isforced =false;

    @Override
    public void onCreate() {
        super.onCreate();
        initFloatParams();
        elfManager.getInitData(MOVnub);
        createToucher();
    }

    public class simulateThread extends Thread
    {
        @Override
        public void run() {
            super.run();
            //开始模拟
            int motivationNub=0;
            imagesNub = elfManager.getMovSize(MOVnub);
            Log.e("xx", "t start with" + (motivationNub));
            while (motivationNub < imagesNub && isSimulating) {
                //加点延迟，给界面刷新用
                synchronized (this){
                    try {
                        this.wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try{
                    if (useOpencv.NewCompare(startCapture(), elfManager.bitmaps[motivationNub])) {
                        Log.e("xx", "找到了图片" + (motivationNub));
                        if(elfManager.types[motivationNub]==1) {
                            CMD.simulateClick(elfManager.downXs[motivationNub], elfManager.downYs[motivationNub]-100);
                            Log.e("xx", "将要点击"+"x:"+elfManager.downXs[motivationNub]+"y:"+elfManager.downXs[motivationNub]);
                    }
                        else if(elfManager.types[motivationNub]==2){
                            CMD.simulateSwipe(elfManager.downXs[motivationNub],elfManager.downYs[motivationNub],elfManager.xs[motivationNub], elfManager.ys[motivationNub]);
                            Log.e("xx", "将要滑动");
                        }
                        motivationNub++;
                    }
                    else  Log.e("xx", "长得不一样和" + (motivationNub));}
                catch (Exception ignored){}
            }
            if(!isforced) {
                isSimulating = false;
                Log.e("xx", "t end with" +1);
                Log.e("xx", "t end with" + 2);
                Log.e("xx", "t end with" + 3);
                Looper.prepare();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        updateViewToWait();
                        Log.e("xx", "t end with" );
                    }
                });
                Log.e("xx", "t end with4" );
                Toast.makeText(getApplicationContext(), "正常终止模拟", Toast.LENGTH_SHORT).show();
            }
        }
    }



    void updateViewToWait()
    {
        float_img.setImageResource(R.color.colorSimulateWait);
        windowManager.updateViewLayout(float_img, floatParams);
    }

    void updateViewToExecute()
    {
        float_img.setImageResource(R.color.colorExecuting);
        windowManager.updateViewLayout(float_img, floatParams);
    }

    void initFloatWindow(){
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        float_img = new ImageView(this);
        float_img.setImageResource(R.color.colorSimulateWait);
        windowManager.addView(float_img, floatParams);
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("ClickableViewAccessibility")
    private void createToucher() {
        initFloatWindow();
        float_img.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP) {

                        if (!isSimulating) {
                            isSimulating = true;
                            isforced = false;
                            new simulateThread().start();
                            updateViewToExecute();
                            Toast.makeText(getBaseContext(), "开始模拟", Toast.LENGTH_SHORT).show();
                        } else {
                            isforced = true;
                            isSimulating = false;
                            updateViewToWait();
                            Toast.makeText(getBaseContext(), "强制终止模拟", Toast.LENGTH_SHORT).show();
                            stopSelf();
                        }
                        return true;
                    }
                return false;
            }
        });
    }

    public void initFloatParams() {
        //赋值WindowManager&LayoutParam.
        floatParams = new WindowManager.LayoutParams();
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        floatParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        //设置效果为背景透明.
        floatParams.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        floatParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        floatParams.gravity = Gravity.TOP | Gravity.START;
        floatParams.x = 980;
        floatParams.y = 1024;

        //设置悬浮窗口长宽数据.
        floatParams.width = 100;// 设置悬浮窗口长宽数据
        floatParams.height = 100;
    }



    @Override
    public void onDestroy() {
        float_img.setEnabled(false);
        windowManager.removeView(float_img);
        CoreActivity.issFloating = false;
        super.onDestroy();
    }
}


