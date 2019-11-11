package com.example.wocaowocao.simulateservice;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.wocaowocao.CMD;
import com.example.wocaowocao.recogImg.useOpencv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.example.wocaowocao.CMD.br;
import static com.example.wocaowocao.CMD.execOS;

public class SimulateService extends Service {
    //读取的动作位置
    int imagesNub;
    int[] xOP;
    int[] yOP;
    Bitmap[] bitmaps;
    //循环和终止线程用的
    int movnb = 0;

    @Override
    public void onCreate() {
        try {
            CMD.WriteIInit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        useOpencv.staticLoadCVLibraries();
        imagesNub = new File(CMD.dataPath + "MOV1/images").listFiles().length-1;
        xOP = new int [imagesNub+1];
        yOP = new int [imagesNub+1];
        bitmaps = new Bitmap[imagesNub+1];
        initXYOP();
        initBitmaps();

        t.start();

        super.onCreate();
    }

    final Thread t = new Thread()
    {
        @Override
        public void run() {
            super.run();
            //开始模拟
            try {
                startSimulate();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
        CMD.Shot(0);
    }

    void startSimulate() throws FileNotFoundException {
        int nub=0;
        while (movnb < imagesNub) {
            CMD.Shot(0);
            try {
                synchronized (t) {
                    t.wait(1000);
                    Log.e("xx", "nub:" + nub);
                    nub++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Bitmap screen = BitmapFactory.decodeStream(new FileInputStream(new File(CMD.dataPath + "MOV1/images/", 0 + ".png")));
            if (screen != null) {

                try{
                if (useOpencv.NewCompare(screen, bitmaps[movnb])) {
                    Log.e("xx", "找到了" + (movnb + 1));
                    CMD.simulateClick(xOP[movnb], yOP[movnb], execOS);
                    Log.e("xx", "将要点击" + xOP[movnb]+ yOP[movnb]);
                    movnb++;
                }
                else  Log.e("xx", "长得不一样和" + (movnb + 1));}
                catch (Exception ignored){}
            }
        }
        CMD.isSimulating = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        movnb = imagesNub+1;

        if(t.isDaemon())Log.e("xx","dead") ;
        try {
            CMD.WriteIDestroy();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
