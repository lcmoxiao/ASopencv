package com.example.wocaowocao;

import android.os.Environment;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class CMD {

    //系统目录
    private static String rootPath = Environment.getExternalStorageDirectory().getPath()+"/";
    //文件目录
    static String dataPath = rootPath+"/1test/";

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
     * @param os 输出流
     * @param MovNub 行数
     */
    public static void simulateShot(DataOutputStream os,int MovNub) {
        exec("screencap -p /sdcard/images/"+MovNub+".png",os);
    }

    /**
     * 写入手势
     *
     * @param x 横坐标
     * @param y 纵坐标
     * @param MovNub 行数
     */
    public static void simulateWritegesture(int x,int y,int MovNub) throws FileNotFoundException {
        new PrintWriter(new FileOutputStream(rootPath+"/gesture.txt",true)).println(MovNub+","+x+","+y);
    }
}
