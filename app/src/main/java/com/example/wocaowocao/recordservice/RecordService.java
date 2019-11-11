package com.example.wocaowocao.recordservice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.wocaowocao.CMD;
import com.example.wocaowocao.R;
import com.example.wocaowocao.recogImg.useOpencv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.example.wocaowocao.CMD.motivationNub;

public class RecordService extends Service {

    //绑定的图片
    ImageView record_img;
    //布局参数.
    WindowManager.LayoutParams params;
    //实例化的WindowManager.
    WindowManager windowManager;
    private float x,y;




    @Override
    public void onCreate() {
        super.onCreate();
        try {
            initFile();
            CMD.WriteIGInit();
            createToucher();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    void initParams()
    {
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
    }


    @SuppressLint("ClickableViewAccessibility")
    private void createToucher() {
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        initParams();
        record_img = new ImageView(this);
        record_img.setImageResource(R.color.colorClear);
        windowManager.addView(record_img, params);

        record_img.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN){
                    //得到相对应屏幕左上角的坐标
                    x = event.getRawX();
                    y = event.getRawY();
                    windowManager.removeView(record_img);
                    Log.e("xxx", "start");
                    t.start();
                }
                return true;
            }
        });
    }

    final Thread t = new Thread()
    {
        @Override
        public void run() {
            super.run();
            //开始模拟
            try {
                Log.e("xxx","开始记录");
                Record();
                Log.e("xxx","记录完毕");
                CMD.simulateClick(CMD.RparamX+50, CMD.RparamY+150, CMD.execOS);
                Log.e("xxx","尝试点击录制按钮"+(CMD.RparamX+50)+(CMD.RparamY+150));
            } catch (FileNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    void Record() throws FileNotFoundException, InterruptedException {
        Bitmap screen,bitmap;
        synchronized (t)
        {


            CMD.Shot(motivationNub);
            CMD.WriteGesture((int) x, (int) y, motivationNub);
            t.wait(100);
            //读取刚才的动作图

            bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(CMD.dataPath + "MOV1/images/", motivationNub + ".png")));



            //识别动作图，如果一致则通过识别，不然就反复读取。
            while (CMD.isRecording) {
                CMD.Shot(0);
                t.wait(1000);
                screen = BitmapFactory.decodeStream(new FileInputStream(new File(CMD.dataPath + "MOV1/images/", 0 + ".png")));
                try {
                    if (useOpencv.NewCompare(screen, bitmap)) {
                        //通过识别，移除覆盖层，模拟点击
                        motivationNub++;
                        CMD.simulateClick((int)x,(int)y,CMD.execOS);
                        Log.e("xxx","通过识别1");
                        break;
                    }
                    else
                        Log.e("xxx","不一致再次识别");
                 }catch(Exception ignored){ Log.e("xxx","为空");}
            }
            //识别模拟点击是否通过，如果一致则通过识别,不然就再点一次
            while (CMD.isRecording) {
                t.wait(500);
                CMD.Shot(0);
                t.wait(1000);
                screen = BitmapFactory.decodeStream(new FileInputStream(new File(CMD.dataPath + "MOV1/images/", 0 + ".png")));
                try {
                    if (!useOpencv.NewCompare(screen, bitmap)) {
                        Log.e("xxx", "通过识别2");
                        break;
                    } else {
                        Log.e("xxx", "一致再次点击");
                        CMD.simulateClick((int) x, (int) y, CMD.execOS);
                    }
                }catch(Exception ignored){}
            }
        }


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


