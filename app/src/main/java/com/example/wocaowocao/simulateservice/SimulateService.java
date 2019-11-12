package com.example.wocaowocao.simulateservice;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.wocaowocao.CMD;
import com.example.wocaowocao.Tshot;
import com.example.wocaowocao.recogImg.useOpencv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.example.wocaowocao.CMD.br;
import static com.example.wocaowocao.CMD.execOS;
import static com.example.wocaowocao.CMD.screen;

public class SimulateService extends Service {
    //读取的动作位置
    static int imagesNub;
    static int[] xOP;
    static int[] yOP;
    static Bitmap[] bitmaps;
    //循环和终止线程用的
    static int motivationNub = 0;
    //用来录像的进程
    public static Tshot tshot;

    @Override
    public void onCreate() {
        try {
            CMD.WriteIInit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imagesNub = new File(CMD.dataPath + "MOV1/images").listFiles().length-1;
        xOP = new int [imagesNub+1];
        yOP = new int [imagesNub+1];
        bitmaps = new Bitmap[imagesNub+1];
        initXYOP();
        initBitmaps();
        t.start();
        super.onCreate();
    }

    static final Thread t = new Thread()
    {
        @Override
        public void run() {
            super.run();
            //开始模拟
            int nub=0;
            tshot = new Tshot(t);

            while (motivationNub < imagesNub) {

                Log.e("xx", "nub:" + nub);
                nub++;
                synchronized (t) {
                    try {
                        tshot.run();
                        if(!tshot.thisnotified) {
                            tshot.thiswaiting = true;
                            Log.e("xxx", "t wait");
                            t.wait();
                        }else{Log.e("xxx", "t skip wait");}
                        tshot.thisnotified =false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (screen != null) {
                    try{
                        if (useOpencv.NewCompare(screen, bitmaps[motivationNub])) {
                            Log.e("xx", "找到了" + (motivationNub + 1));
                            CMD.simulateClick(xOP[motivationNub], yOP[motivationNub], execOS);
                            Log.e("xx", "将要点击" + xOP[motivationNub]+ yOP[motivationNub]);
                            motivationNub++;
                        }
                        else  Log.e("xx", "长得不一样和" + (motivationNub + 1));}
                    catch (Exception ignored){}
                }
                break;
            }
            CMD.isSimulating = false;
        }
    };



    //初始化操作坐标
    void initXYOP()
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化操作图库
    void initBitmaps() {
        for (int i = 0; i <= imagesNub; i++)
        {
            try {
                bitmaps[i] = BitmapFactory.decodeStream(new FileInputStream(new File(CMD.dataPath + "MOV1/images/", (i+1) + ".png")));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        motivationNub = imagesNub+1;
        Log.e("xxx","xx"+t.getState());
        try {
            CMD.WriteIDestroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
