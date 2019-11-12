package com.example.wocaowocao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.wocaowocao.CMD;
import com.example.wocaowocao.MainActivity;

public class shotReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("xxx","ReceiveShot");
        ((MainActivity) CMD.mContext).xxx();
    }


}
