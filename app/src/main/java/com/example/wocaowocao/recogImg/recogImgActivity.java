package com.example.wocaowocao.recogImg;



import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.wocaowocao.Base.BaseActivity;
import com.example.wocaowocao.R;
import com.example.wocaowocao.Base.ViewInject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;


@ViewInject(main_layout_id = R.layout.activity_recog)
public class recogImgActivity extends BaseActivity {

    @BindView(R.id.select_btn1)
    Button selectBtn1;
    @BindView(R.id.select_btn2)
    Button selectBtn2;
    @BindView(R.id.process_btn)
    Button processBtn;
    @BindView(R.id.imageView1)
    ImageView ImageView1;
    @BindView(R.id.imageView2)
    ImageView ImageView2;
    @BindView(R.id.recog)
    LinearLayout mLayout;

    private Bitmap Bp1;
    private Bitmap Bp2;
    private static String imgPath = Environment.getExternalStorageDirectory()+"/images/" ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void afterBindView() {

        imgRecognition();

    }

    public void imgRecognition()
    {
        useOpencv.staticLoadCVLibraries();
        selectBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select1Image(imgPath);
            }
        });
        selectBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select2Image(imgPath);
            }
        });
        processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(useOpencv.HashCompare(Bp1, Bp2))
                    Toast.makeText(getBaseContext(),"真像",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getBaseContext(),"像个屁",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void select1Image(String path) {
        try {
            File f = new File(path, "home.png");
            Bp1 = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView1.setImageBitmap(Bp1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void select2Image(String path) {
        try {
            File f = new File(path, "temp.png");
            Bp2 = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView2.setImageBitmap(Bp2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //startActivityForResult(Intent.createChooser(new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT), "选择图像..."), 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
                Bp1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

                ImageView1.setImageBitmap(Bp1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
                Bp2 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

                ImageView2.setImageBitmap(Bp2);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





}
