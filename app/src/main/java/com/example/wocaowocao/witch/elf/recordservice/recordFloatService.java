package com.example.wocaowocao.witch.elf.recordservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
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

import com.example.wocaowocao.RootShell.RootShell;
import com.example.wocaowocao.RootShell.execution.Command;
import com.example.wocaowocao.R;
import com.example.wocaowocao.witch.elf.CoreActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import static com.example.wocaowocao.base.CMD.dataPath;
import static com.example.wocaowocao.base.CMD.delFile;
import static com.example.wocaowocao.witch.elfDepository.DepositoryActivity.MOVnub;
import static com.example.wocaowocao.witch.elf.shotService.startCapture;


public class recordFloatService extends Service {

    //绑定的图片
    ImageView float_img;
    //实例化的WindowManager.
    WindowManager windowManager;
    private static OutputStream writeOS = null;
    private static PrintWriter pw=null;
    // 是否在录制
    public static Boolean isRecording = false;
    private int motivationNub = 0;

    //悬浮窗的参数
    public WindowManager.LayoutParams floatParams;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            initFloatParams();
            InitWriteGesture(MOVnub);
            createToucher();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    void initWindow(){
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        float_img = new ImageView(this);
        float_img.setImageResource(R.color.colorRecordingWait);
        windowManager.addView(float_img, floatParams);
    }

    void updateViewToExecute()
    {
        float_img.setImageResource(R.color.colorExecuting);
        windowManager.updateViewLayout(float_img, floatParams);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void createToucher() {
        initWindow();

        float_img.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP) {

                    if (!isRecording) {
                        //点击后开始录制
                        updateViewToExecute();
                        isRecording = true;
                        new GeteventThread().start();
                        Toast.makeText(getBaseContext(), "准备就绪", Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        isRecording = false;
                        stopSelf();
                        Toast.makeText(getBaseContext(), "录制结束", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }

    public class GeteventThread extends Thread {
        private ArrayList<String> lines = new ArrayList<>();
        private int [] clickPosition = {-1,-1,-1,-1};
        private boolean fastShot = false;


        @Override
        public void run() {
            try {
                Command command = new Command(0, false,"getevent /dev/input/event3") {
                    @Override
                    public void commandOutput(int id, String line) {
                            lines.add(line);
                            if(lines.size()>=10) {
                                clickPosition[0] = Integer.parseInt(lines.get(3).split(" ")[2],16);
                                clickPosition[1] = Integer.parseInt(lines.get(4).split(" ")[2],16);
                                if(!fastShot){
                                    fastShot=true;
                                    Shot(motivationNub, MOVnub);
                                }
                                if(isRecording) {
                                    if (lines.get(6).split(" ")[2].equals("ffffffff")) {
                                        fastShot = false;
                                        Log.e("xxx", "isclick x " + clickPosition[0] + " and y " + clickPosition[1]);
                                        WriteGesture(clickPosition[0], clickPosition[1]);
                                        Log.e("xxx", "录制了一次点击");
                                        motivationNub++;
                                        lines.clear();
                                    } else if (lines.get(lines.size() - 4).split(" ")[2].equals("ffffffff")) {
                                        fastShot = false;
                                        clickPosition[2] = Integer.parseInt(lines.get(lines.size() - 7).split(" ")[2], 16);
                                        clickPosition[3] = Integer.parseInt(lines.get(lines.size() - 6).split(" ")[2], 16);
                                        Log.e("xxx", "isslip x " + clickPosition[0] + "and y" + clickPosition[1] + " to " + clickPosition[2] + " " + clickPosition[3]);
                                        WriteGesture(clickPosition[0], clickPosition[1], clickPosition[2], clickPosition[3]);
                                        Log.e("xxx", "录制了一次滑动");
                                        motivationNub++;
                                        lines.clear();
                                    }
                                }
                            super.commandOutput(id, line);
                            }
                    }
                };
                RootShell.getShell(true).add(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //初始化 写手势
    public void InitWriteGesture(int MOVnub) throws IOException {
        writeOS = new FileOutputStream(dataPath+"MOV"+MOVnub+"/gesture.txt",true);
        pw=new PrintWriter(writeOS);
        pw.flush();
    }

    public void DestroyWriteGesture() throws IOException {
        pw.close();
        writeOS.close();
    }

    public void WriteGesture(int x,int y){
        String s=0+","+x+","+y;
        pw.println(s);
        pw.flush();
    }

    public void WriteGesture(int downX, int downY, int x, int y) {
        String s=1+","+downX+","+downY+","+x+","+y;
        pw.println(s);
        pw.flush();
    }

    //
    public int getMovImageLength()
    {
        return new File(dataPath+"MOV"+MOVnub+"/images").listFiles().length;
    }

    public void delLastImage()
    {
        delFile(dataPath+"MOV"+MOVnub+"/images/"+(getMovImageLength()-1)+".png");//getMovImageLength()-1因为标号从0开始
    }

    public static int getMovGestureLength()
    {
        LineNumberReader br = null;
        try {
            br = new LineNumberReader(new FileReader(dataPath+"MOV"+MOVnub+"/gesture.txt"));
            br.skip(Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert br != null;
        return br.getLineNumber();
    }

    public void Shot(int motivationNub, int MOVnub){
        Bitmap screen = startCapture();
        try {
            screen.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File(dataPath+"MOV"+MOVnub+"/images/"+motivationNub+".png")) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onDestroy() {
        float_img.setEnabled(false);
        windowManager.removeView(float_img);
        CoreActivity.isrFloating = false;
        try {
            DestroyWriteGesture();
            if(getMovImageLength()!=getMovGestureLength()) {
                delLastImage();
                Log.e("xxx","清除了多余的一张快照");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

}


