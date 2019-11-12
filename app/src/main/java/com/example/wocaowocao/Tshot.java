package com.example.wocaowocao;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import static com.example.wocaowocao.CMD.LBmanager;


public class Tshot extends Thread{

    //Tshot是否提醒过了
    static Boolean Tshotnotified = false;
    //Tshot是否在睡眠
    static Boolean Tshotwaiting = false;

    private Thread T;
    public boolean thisnotified = false;
    public boolean thiswaiting = false;



    public Tshot(Thread _T)
    {
        T=_T;
    }



    @Override
    public void run() {
        super.run();
        Intent intent = new Intent();
        intent.setAction("shot");
        intent.setComponent( new ComponentName( "com.example.wocaowocao" , "com.example.wocaowocao.shotReceiver") );

        Log.e("xxx","Tshot start");
        try {

            synchronized (this) {
                LBmanager.sendBroadcast(intent);
                if(!Tshotnotified) {
                    Tshotwaiting = true;
                    Log.e("xxx", "Tshot wait");
                    this.wait();
                }else{Log.e("xxx", "Tshot skip wait");}
            }
            Tshotnotified = false;
            Log.e("xxx","Tshot finished");

            synchronized (T) {
                thisnotified = true;
                if(thiswaiting) {
                    T.notify();
                    Log.e("xxx", "t notify");
                }else Log.e("xxx", "t no wait");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
