package com.example.wocaowocao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class shotReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("xxx","ReceiveShot");
        ((MainActivity)CMD.mContext).xxx();
    }


}
