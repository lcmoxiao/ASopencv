package com.example.wocaowocao.base;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;


import android.os.Environment;

import android.view.Gravity;

import android.view.View;
import android.view.WindowManager;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import static com.example.wocaowocao.elf.shotService.startCapture;

public class CMD {

    //系统目录
    private static String rootPath = Environment.getExternalStorageDirectory().getPath()+"/";
    //文件目录
    public static String dataPath = rootPath+"1test/";
    // 是否在录制
    public static Boolean isRecording = false;
    //是否在模拟操作
    public static Boolean isSimulating = false;

    //悬浮窗的位置
    public static int RparamX =980, RparamY =1024;
    //悬浮窗的位置
    public static int SparamX =980, SparamY =1024;
    //悬浮窗的参数
    public static WindowManager.LayoutParams floatParams;
    //临时截屏
    private static Bitmap screen = null;

    private static OutputStream writeOS = null;
    private static PrintWriter pw=null;
    public static BufferedReader br = null;
    public static DataOutputStream execOS = null;

    /**
     * 执行shell指令
     *
     * @param cmd
     *            指令
     */
     static void exec(String cmd, DataOutputStream os) {
        try {
            os.writeBytes(cmd + "\n");
            os.flush();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 后台模拟全局按键
     *
     * @param keyCode 键值
     *
     */
    public static void simulateKey(int keyCode, DataOutputStream os) {
        exec("input keyevent " + keyCode,os);
    }

    /**
     * 模拟点击
     *
     * @param x 横坐标
     * @param y 纵坐标
     */
    public static void simulateClick(int x, int y, DataOutputStream os) {
        exec("input tap " + x + " " + y ,os);
    }


    // 初始化MOV目录
    public static void initMovFile(int Movnb) throws Exception {
        File f1 = new File(CMD.dataPath);
        if (!f1.exists()) {
            if (!f1.mkdirs()) throw new Exception("你创建不了文件夹");
        }
        File f2 = new File(CMD.dataPath + "MOV"+Movnb);
        if (!f2.exists()) {
            if (!f2.mkdirs()) throw new Exception("你创建不了文件夹");
        }
        File f3 = new File(CMD.dataPath + "MOV"+Movnb+"/images");
        if (!f3.exists()) {
            if (!f3.mkdirs()) throw new Exception("你创建不了文件夹");
        }
        File f4 = new File(CMD.dataPath, "MOV"+Movnb+"/gesture.txt");
        if (!f4.exists()) {
            if (!f4.createNewFile()) throw new Exception("你创建不了文件");
        }
    }


    public static Bitmap Shot(int motivationNub){
        screen = startCapture();
        try {
            screen.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File(dataPath+"MOV1/images/"+motivationNub+".png")) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return screen;
    }

    public static Bitmap getScreen(){
        screen = startCapture();
        return screen;
    }


    /**
     * 本应用内部的截屏，需要先初始化Context
     */
    public static Bitmap Shot11(View view, int MotivationNub)  {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File(dataPath+"MOV1/images/"+MotivationNub+".png")) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 写入手势,需要先WriteInit()，之后不要忘记WriteDestroy()
     *
     * @param x 横坐标
     * @param y 纵坐标
     * @param MotivationNub 动作数
     */
    public static void WriteGesture(int x,int y,int MotivationNub){
        String s=MotivationNub+","+x+","+y;
        pw.println(s);
    }

    /**
     *  删除文件
     *
     * @param file 文件
     *
     */
    static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }

    /**
     *  删除文件
     *
     * @param path 文件路径
     *
     */
    public static boolean delFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }


    //初始化录入图片
    public static void WriteGestureInit() throws IOException {
        br = new BufferedReader(new FileReader(CMD.dataPath+"MOV1/gesture.txt"));
    }

    public static void WriteGestureDestroy() throws IOException {
        br.close();
    }


    //初始化 读写手势 和录入图片
    public static void WriteIGInit() throws IOException {
        execOS = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
        writeOS = new FileOutputStream(CMD.dataPath+"MOV1/gesture.txt",true);
        pw=new PrintWriter(writeOS);
        pw.flush();
    }

    public static void WriteIGDestroy() throws IOException {
        pw.close();
        writeOS.close();
        execOS.close();
    }

    static void initFloatParams() {
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
        floatParams.x = RparamX;
        floatParams.y = RparamY;

        //设置悬浮窗口长宽数据.
        floatParams.width = 100;// 设置悬浮窗口长宽数据
        floatParams.height = 100;
    }

}
