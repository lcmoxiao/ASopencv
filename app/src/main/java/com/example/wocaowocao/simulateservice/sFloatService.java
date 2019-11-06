package com.example.wocaowocao.simulateservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
import com.example.wocaowocao.recogImg.useOpencv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.example.wocaowocao.CMD.br;
import static com.example.wocaowocao.CMD.execOS;


public class sFloatService extends Service {


    //绑定的图片
    ImageView float_img;
    //布局参数.
    WindowManager.LayoutParams params;
    //实例化的WindowManager.
    WindowManager windowManager;

    //读取的动作位置
    int imagesNub;
    int[] xOP;
    int[] yOP;
    Bitmap[] bitmaps;


    private float x1,y1,x2,y2,mTouchCurrentX,mTouchCurrentY;
    private int lastX, lastY;
    private boolean isPlaying = false;


    @Override
    public void onCreate() {
        super.onCreate();
        try {
            CMD.WriteIInit();
            createToucher();
            useOpencv.staticLoadCVLibraries();

            imagesNub = new File(CMD.dataPath + "MOV1/images").listFiles().length-1;
            xOP = new int [imagesNub];
            yOP = new int [imagesNub];
            bitmaps = new Bitmap[imagesNub];
            initxyOP();
            initBitmaps();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //初始化操作坐标
    void initxyOP()
    {
        int motivationNub=0;
        String [] str;
        String line;
        try {
            while((line=br.readLine())!=null) {
                str = line.split(",");
                xOP[motivationNub] = Integer.parseInt(str[1]);
                yOP[motivationNub] = Integer.parseInt(str[2]);
                motivationNub++;
            }
            Toast.makeText(getBaseContext(), "初始化操作库完毕", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化操作图库
    void initBitmaps() {
        for (int i = 0; i <= imagesNub; i++)
        {
            try {
                bitmaps[i] = BitmapFactory.decodeStream(new FileInputStream(new File(CMD.dataPath + "MOV1/images/", i + ".png")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getBaseContext(), "初始化图库完毕", Toast.LENGTH_SHORT).show();
    }


    void clickOP() throws FileNotFoundException, InterruptedException {
        for (int i = 0; i <= imagesNub;) {
            CMD.Shot(0);


            Thread.sleep(800);
            Bitmap screen = BitmapFactory.decodeStream(new FileInputStream(new File(CMD.dataPath + "MOV1/images/" , 0 + ".png")));
            Log.e("fuck","尝试"+(i+1)+"的动作");
            if(screen==null)
            {
                Log.e("fuck","获取"+(i+1)+"的动作截图失败");
            }
            else
            {
                if(useOpencv.NewCompare(screen, bitmaps[i]))
                {
                CMD.simulateClick(xOP[i], yOP[i], execOS);
                i++;
                }
            }
        }
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
                        CMD.paramX = params.x += mTouchCurrentX - lastX;
                        CMD.paramY = params.y += mTouchCurrentY - lastY;
                        windowManager.updateViewLayout(float_img, params);
                        lastX = (int)mTouchCurrentX;
                        lastY = (int)mTouchCurrentY;
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getRawX();
                        y2 = event.getRawY();
                        double distance = Math.sqrt(Math.abs(x1-x2)*Math.abs(x1-x2)+Math.abs(y1-y2)*Math.abs(y1-y2));//两点之间的距离
                        if (distance < 15) { // 距离较小，当作click事件来处理

                                try {
                                    clickOP();
                                } catch (FileNotFoundException | InterruptedException e) {
                                    e.printStackTrace();
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
        params.x =CMD.paramX;
        params.y =CMD.paramY;
        //设置悬浮窗口长宽数据.
        params.width =100;// 设置悬浮窗口长宽数据
        params.height =100;
        float_img = new ImageView(this);
        float_img.setImageResource(R.color.colorSimulate);
        windowManager.addView(float_img, params);
    }

    @Override
    public void onDestroy() {
        try {
            float_img.setEnabled(false);
            windowManager.removeView(float_img);
            CMD.WriteIDestroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}

