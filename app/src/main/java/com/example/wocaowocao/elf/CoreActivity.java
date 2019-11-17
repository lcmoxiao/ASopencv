package com.example.wocaowocao.elf;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.wocaowocao.base.BaseActivity;
import com.example.wocaowocao.base.ViewInject;
import com.example.wocaowocao.base.CMD;
import com.example.wocaowocao.R;
import com.example.wocaowocao.elf.recordservice.rFloatService;
import com.example.wocaowocao.elf.simulateservice.simulateFloatService;

import butterknife.BindView;

import static com.example.wocaowocao.base.CMD.dataPath;


@ViewInject(main_layout_id = R.layout.activity_core)
public class CoreActivity extends BaseActivity {

    @BindView(R.id.core_btn1)
    Button selectBtn2;
    @BindView(R.id.core_btn2)
    Button selectBtn3;
    @BindView(R.id.core_btn3)
    Button selectBtn4;

    // 是否打开录制悬浮窗
    static Boolean isrFloating = false;
    // 是否打开模拟悬浮窗
    static Boolean issFloating = false;

    @Override
    public void afterBindView()  {
        initClick();
        startScreenRecord();
        getScreenBaseInfo();
    }

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    void initClick() {
        selectBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isrFloating) {
                    stopService(new Intent(CoreActivity.this, rFloatService.class));
                    isrFloating = false;
                } else {
                    startService(new Intent(CoreActivity.this, rFloatService.class));
                    isrFloating = true;
                }
            }
        });

        selectBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (issFloating) {
                    stopService(new Intent(CoreActivity.this, simulateFloatService.class));
                    issFloating = false;
                } else {
                    startService(new Intent(CoreActivity.this, simulateFloatService.class));
                    issFloating = true;
                }
            }
        });

        selectBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CMD.delFile(dataPath + "MOV"+CMD.MOVnub);
            }
        });
    }
    /**
     * 获取屏幕基本信息
     */
    private void getScreenBaseInfo() {
        //A structure describing general information about a display, such as its size, density, and font scaling.
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mScreenDensity = metrics.densityDpi;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                //获得录屏权限，启动Service进行录制
                Intent intent = new Intent(this, shotService.class);
                intent.putExtra("resultCode", resultCode);
                intent.putExtra("resultData", data);
                intent.putExtra("mScreenWidth", mScreenWidth);
                intent.putExtra("mScreenHeight", mScreenHeight);
                intent.putExtra("mScreenDensity", mScreenDensity);
                startService(intent);
                //Toast.makeText(this, "成功开启服务", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "服务开启失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //start screen record
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startScreenRecord() {
        //Manages the retrieval of certain types of MediaProjection tokens.
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        //Returns an Intent that must passed to startActivityForResult() in order to start screen capture.
        Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(permissionIntent, 1000);
    }

    //stop screen record.
    public void stopScreenRecord() {
        Intent service = new Intent(this, shotService.class);
        stopService(service);
    }


    @Override
    protected void onDestroy() {
        stopScreenRecord();
        super.onDestroy();
    }
}
