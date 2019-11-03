package com.example.wocaowocao;

import android.util.Log;
import android.view.ViewDebug;

import java.io.DataOutputStream;
import java.io.OutputStream;

public class CMD {
    /**
     * 执行shell指令
     *
     * @param cmd
     *            指令
     */
    void exec(String cmd, DataOutputStream os) {
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
    void simulateKey(int keyCode, DataOutputStream os) {
        exec("input keyevent " + keyCode,os);
    }

    /**
            * 模拟点击
     *
             * @param x 横坐标
     * @param y 纵坐标
     */
    void simulateClick(int x, int y, DataOutputStream os) {
        exec("input tap " + x + " " + y ,os);
    }

    public void simulateShot(DataOutputStream os) {
        exec("screencap -p /sdcard/screen.png",os);
    }


}
