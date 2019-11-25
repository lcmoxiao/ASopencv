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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


import static com.example.wocaowocao.witch.elfDepository.DepositoryActivity.MOVnub;
import static com.example.wocaowocao.witch.elf.shotService.startCapture;

public class simulateFloatService extends Service {


    //绑定的图片
    ImageView float_img;
    //实例化的WindowManager.
    WindowManager windowManager;
    //悬浮窗的参数
    public WindowManager.LayoutParams floatParams;
    //是否在模拟操作
    public static Boolean isSimulating = false;



    public static BufferedReader br = null;

    //读取的动作位置
    int imagesNub;
    int[] downXs,downYs,xs,ys,types;
    Bitmap[] bitmaps;
    boolean isforced =false;

    @Override
    public void onCreate() {
        super.onCreate();
        initFloatParams();
        imagesNub = new File(CMD.dataPath + "MOV"+MOVnub+"/images").listFiles().length;
        xs = new int [imagesNub];
        ys = new int [imagesNub];
        downXs  = new int [imagesNub];
        downYs = new int [imagesNub];
        types = new int [imagesNub];
        bitmaps = new Bitmap[imagesNub];
        try {
            ReadGestureInit(MOVnub);
            initXYOP();
            initBitmaps();
        } catch (IOException e) {
            e.printStackTrace();
        }
        createToucher();
    }

    public class simulateThread extends Thread
    {
        @Override
        public void run() {
            super.run();
            //开始模拟
            int motivationNub=0;
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
                    if (useOpencv.NewCompare(startCapture(), bitmaps[motivationNub])) {
                        Log.e("xx", "找到了图片" + (motivationNub));
                        if(types[motivationNub]==0) {
                            CMD.simulateClick(xs[motivationNub], ys[motivationNub]);
                            Log.e("xx", "将要点击"+"x:"+xs[motivationNub]+"y:"+ys[motivationNub]);
                    }
                        else if(types[motivationNub]==1){
                            CMD.simulateSwipe(downXs[motivationNub],downYs[motivationNub],xs[motivationNub], ys[motivationNub]);
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

    //初始化操作坐标
    void initXYOP()
    {
        int motivationNub=0;
        String [] str;
        String line;
        try {
            while((line=br.readLine())!=null) {
                str = line.split(",");
                types[motivationNub] = Integer.parseInt(str[0]);
                if(types[motivationNub]==0) {
                    xs[motivationNub] = Integer.parseInt(str[1]);
                    ys[motivationNub] = Integer.parseInt(str[2]);
                }
                else if(types[motivationNub]==1)
                {
                    downXs[motivationNub] = Integer.parseInt(str[1]);
                    downYs[motivationNub] = Integer.parseInt(str[2]);
                    xs[motivationNub] = Integer.parseInt(str[3]);
                    ys[motivationNub] = Integer.parseInt(str[4]);
                }
                motivationNub++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化操作图库
    void initBitmaps() {
        for (int i = 0; i < imagesNub; i++)
        {
            try {
                bitmaps[i] = BitmapFactory.decodeStream(new FileInputStream(new File(CMD.dataPath + "MOV"+MOVnub+"/images/", (i) + ".png")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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

    public static void ReadGestureInit(int MOVnub) throws IOException {
        br = new BufferedReader(new FileReader(CMD.dataPath+"MOV"+MOVnub+"/gesture.txt"));
    }

    public static void ReadGestureDestroy() throws IOException {
        br.close();
    }

    @Override
    public void onDestroy() {
        float_img.setEnabled(false);
        windowManager.removeView(float_img);
        CoreActivity.issFloating = false;
        try {
            ReadGestureDestroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}


