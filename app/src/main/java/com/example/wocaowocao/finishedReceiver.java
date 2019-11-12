package com.example.wocaowocao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.wocaowocao.Tshot.Tshotwaiting;
import static com.example.wocaowocao.Tshot.Tshotnotified;
import static com.example.wocaowocao.simulateservice.SimulateService.tshot;

public class finishedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("xxx","Receiveshoted");
        Tshotnotified = true;
        synchronized (tshot) {
            if(Tshotwaiting) {
                Tshotwaiting = false;
                Log.e("xxx", "Tshot notify");
                tshot.notify();
            }else{
                Log.e("xxx", "Tshot not wait");
            }
        }

    }


}
