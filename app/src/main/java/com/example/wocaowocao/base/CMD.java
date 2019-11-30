package com.example.wocaowocao.base;



import android.util.Log;
import java.io.IOException;


public class CMD {

    private static void exec(String cmd) {
        try {
            long startTime=System.currentTimeMillis();

            Process SuProcess = Runtime.getRuntime().exec("su");
            SuProcess.getOutputStream().write((cmd + "\n").getBytes());
            SuProcess.getOutputStream().write(("exit\n").getBytes());
            SuProcess.getOutputStream().flush();
            SuProcess.waitFor();
            long endTime=System.currentTimeMillis();
            Log.e("xx", "模拟点击花费时间： "+(endTime - startTime)+"ms");


    } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void simulateClick(int x, int y) {
         exec("input tap " + x + " " + y );
    }

    public static void simulateSwipe(int downX,int downY, int x,  int y) {
        exec("input swipe "+  downX+ " " + downY + " "+ x  + " " + y+" 1000");
    }

    //    //系统目录
//    private static String rootPath = Environment.getExternalStorageDirectory().getPath()+"/";
//    //文件目录
//    public static String dataPath = rootPath+"1test/";
//    //MOV目录
//    public static String MOVPath = dataPath + "MOV";


    //    // 刷新目录
//    public static void initMovFile(int MOVnub)  {
//        try {
//            CMD.delFile(MOVPath+MOVnub);
//            File f1 = new File(CMD.dataPath);
//            if (!f1.exists()) {
//                if (!f1.mkdirs()) throw new Exception("你创建不了文件夹");
//            }
//            File f2 = new File(MOVPath+MOVnub);
//            if (!f2.exists()) {
//                if (!f2.mkdirs()) throw new Exception("你创建不了文件夹");
//            }
//            File f3 = new File(MOVPath+MOVnub+"/images");
//            if (!f3.exists()) {
//                if (!f3.mkdirs()) throw new Exception("你创建不了文件夹");
//            }
//            File f4 = new File(MOVPath+MOVnub+"/gesture.txt");
//            if (!f4.exists()) {
//                if (!f4.createNewFile()) throw new Exception("你创建不了文件");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


//    private static void delFile(File file) {
//        if (!file.exists()) {
//            return;
//        }
//
//        if (file.isDirectory()) {
//            File[] files = file.listFiles();
//            for (File f : files) {
//                delFile(f);
//            }
//        }
//       file.delete();
//    }
//
//    public static boolean delFile(String path) {
//        File file = new File(path);
//        if (!file.exists()) {
//            return false;
//        }
//
//        if (file.isDirectory()) {
//            File[] files = file.listFiles();
//            for (File f : files) {
//                delFile(f);
//            }
//        }
//        return file.delete();
//    }
//
//    public static void delFile(int MOVnub) {
//        CMD.delFile(MOVPath+MOVnub);
//    }

}
