package com.example.wocaowocao.elf.simulateservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.example.wocaowocao.base.CMD.br;
import static com.example.wocaowocao.base.CMD.getScreen;

public class simulateFloatService extends Service {


    //绑定的图片
    ImageView float_img;
    //实例化的WindowManager.
    WindowManager windowManager;

    private float x1,y1,x2,y2,mTouchCurrentX,mTouchCurrentY;
    private int lastX, lastY;

    //读取的动作位置
    int imagesNub;
    int[] downXs,downYs,xs,ys,types;
    Bitmap[] bitmaps;
    simulateThread t;
    boolean isforced =false;

    @Override
    public void onCreate() {
        super.onCreate();
        imagesNub = new File(CMD.dataPath + "MOV"+CMD.MOVnub+"/images").listFiles().length;
        xs = new int [imagesNub];
        ys = new int [imagesNub];
        downXs  = new int [imagesNub];
        downYs = new int [imagesNub];
        types = new int [imagesNub];
        bitmaps = new Bitmap[imagesNub];

        try {
            CMD.ReadGestureInit(CMD.MOVnub);
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
            while (motivationNub < imagesNub && CMD.isSimulating) {

                //加点延迟，给界面刷新用
                synchronized (this){
                    try {
                        this.wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try{
                    if (useOpencv.NewCompare(getScreen(), bitmaps[motivationNub])) {
                        Log.e("xx", "找到了图片" + (motivationNub));
                        if(types[motivationNub]==0) {
                            CMD.simulateClick(xs[motivationNub], ys[motivationNub]);
                            Log.e("xx", "将要点击");
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
                CMD.isSimulating = false;
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
        windowManager.updateViewLayout(float_img, CMD.floatParams);
    }

    void updateViewToExecute()
    {
        float_img.setImageResource(R.color.colorExecuting);
        windowManager.updateViewLayout(float_img, CMD.floatParams);
    }

    void initFloatWindow(){
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        float_img = new ImageView(this);
        float_img.setImageResource(R.color.colorSimulateWait);
        windowManager.addView(float_img, CMD.floatParams);
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
                bitmaps[i] = BitmapFactory.decodeStream(new FileInputStream(new File(CMD.dataPath + "MOV"+CMD.MOVnub+"/images/", (i) + ".png")));
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
                                isforced = false;
                                t = new simulateThread();
                                t.start();
                                updateViewToExecute();
                                Toast.makeText(getBaseContext(), "开始模拟", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                isforced = true;
                                CMD.isSimulating = false;
                                updateViewToWait();
                                Toast.makeText(getBaseContext(), "强制终止模拟", Toast.LENGTH_SHORT).show();
                                Log.e("xxx","t的状态"+t.getState());
                            }
                            return true;
                        } else {
                            Log.e("xxx","t的状态"+t.getState());
                            Toast.makeText(getBaseContext(), "一定要选个好位置", Toast.LENGTH_SHORT).show();
                            return true ;
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
        try {
            CMD.ReadGestureDestroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}


