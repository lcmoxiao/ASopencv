package com.example.wocaowocao;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

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
    public static  void simulateClick(int x, int y, DataOutputStream os) {
        exec("input tap " + x + " " + y ,os);
    }

    /**
     * 获取当前动作数
     */
    public static int getMovNub() {
        return  new File(rootPath+"/images").listFiles().length;
    }

    /**
     * 截图
     *
     *
     * @param MotivationNub 行数 0 是留给操作截图用的，》0的才是保存的文件
     */
    public static void Shot(int MotivationNub) {
        exec("screencap -p "+dataPath+"MOV1/images/"+MotivationNub+".png", execOS);
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
    static boolean delFile(String path) {
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
    public static void WriteIInit() throws IOException {
        br = new BufferedReader(new FileReader(CMD.dataPath+"MOV1/gesture.txt"));
        execOS = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
    }

    public static void WriteIDestroy() throws IOException {
        execOS.close();
        br.close();
    }


    //初始化 读写手势 和录入图片
    public static void WriteIGInit() throws IOException {
        execOS = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
        writeOS = new FileOutputStream(CMD.dataPath+"MOV1/gesture.txt",true);
        pw=new PrintWriter(writeOS);
    }

    public static void WriteIGDestroy() throws IOException {
        pw.close();
        writeOS.close();
        execOS.close();
    }



}
