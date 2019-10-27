package com.example.wocaowocao;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.InputStream;


import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.resize;

public class MainActivity extends AppCompatActivity {

    private double max_size = 1024;
    private int PICK_IMAGE_REQUEST = 1;
    private ImageView myImageView1;
    private Bitmap selectbp1;
    private Bitmap selectbp2;
    Button selectImageBtn1;
    Button processBtn;
    private ImageView myImageView2;
    private Button selectImageBtn2;

    //OpenCV库静态加载并初始化
    private void staticLoadCVLibraries(){
        boolean load = OpenCVLoader.initDebug();
        if(load) {
            Log.i("CV", "Open CV Libraries loaded...");
        }
    }

    private  void initbind()
    {
        setContentView(R.layout.activity_main);
        staticLoadCVLibraries();
        myImageView1 = findViewById(R.id.imageView1);
        myImageView1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        selectImageBtn1 = findViewById(R.id.select_btn1);
        myImageView2 = findViewById(R.id.imageView2);
        myImageView2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        selectImageBtn2 = findViewById(R.id.select_btn2);
        processBtn = findViewById(R.id.process_btn);
    }


    private void select1Image() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"选择图像..."), 1);
    }
    private void select2Image() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"选择图像..."), 2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initbind();
        selectImageBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select1Image();
            }
        });
        selectImageBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select2Image();
            }
        });


        processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashCompare(selectbp1,selectbp2);
            }
        });
    }

    private void HashCompare( Bitmap Bp1, Bitmap Bp2) {
        Mat src1 = new Mat();
        Mat dst1 = new Mat();
        Mat src2 = new Mat();
        Mat dst2 = new Mat();
        //读取位图到MAT
        Utils.bitmapToMat(Bp1, src1);
        Utils.bitmapToMat(Bp2, src2);
        //四通变三通，三通变一通
        cvtColor(src1, dst1, Imgproc.COLOR_BGR2GRAY);
        cvtColor(src2, dst2, Imgproc.COLOR_BGR2GRAY);
        //缩成8*8
        resize(dst1, dst1,new Size(8,8) , 0, 0,  INTER_CUBIC);
        resize(dst2, dst2,new Size(8,8) , 0, 0,  INTER_CUBIC);

        //核心部分
        //这里变成二维数组才可以用Mat.get去获取，二维是因为每个像素点里面可能有很多条属性（ARGB），但是变成灰度之后就只有一个G了，这个是Gray，前面那个是Green。
        double[][] data1 = new double[64][1];
        double[][] data2 = new double[64][1];
        //iAvg 记录平均像素灰度值，arr记录像素灰度值，data是个跳板。
        int iAvg1 = 0, iAvg2 = 0;
        double[] arr1 = new double[64];
        double[] arr2 = new double[64];
        //get灰度给data，用data给arr充值，算平均灰度值iAvg。
        for (int i = 0; i < 8; i++)
        {
            int tmp = i * 8;
            for (int j = 0; j < 8; j++)
            {
                int tmp1 = tmp + j;
                data1[tmp1] = dst1.get(i,j);
                data2[tmp1] = dst2.get(i,j);
                arr1[tmp1] = data1[tmp1][0];
                arr2[tmp1] = data2[tmp1][0];
                iAvg1 += arr1[tmp1];
                iAvg2 += arr2[tmp1];
            }
        }
        iAvg1 /= 64;
        iAvg2 /= 64;
        //比对每个像素灰度值和平均灰度值大小
        for (int i = 0; i < 64; i++)
        {
            arr1[i] = (arr1[i] >= iAvg1) ? 1 : 0;
            arr2[i] = (arr2[i] >= iAvg2) ? 1 : 0;
        }
        //计算差异值
        int iDiffNum = 0;
        for (int i = 0; i < 64; i++)
            if (arr1[i] != arr2[i])
                ++iDiffNum;
        //输出什么看个人喜好
        if (iDiffNum <= 5)
            Toast.makeText(getBaseContext(), "真像", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getBaseContext(), "我觉得不像", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Log.d("image-tag", "start to decode selected image now...");
                InputStream input = getContentResolver().openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(input, null, options);

                options.inSampleSize = 2 ;
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                selectbp1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

                myImageView1.setImageBitmap(selectbp1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Log.d("image-tag", "start to decode selected image now...");
                InputStream input = getContentResolver().openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(input, null, options);


                options.inSampleSize = 2;
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                selectbp2 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

                myImageView2.setImageBitmap(selectbp2);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
