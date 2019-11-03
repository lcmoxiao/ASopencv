package com.example.wocaowocao.floatwin;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.wocaowocao.Base.BaseActivity;
import com.example.wocaowocao.R;
import com.example.wocaowocao.Base.ViewInject;

import butterknife.BindView;



@ViewInject(main_layout_id = R.layout.activity_floatwin)
public class floatwinActivity extends BaseActivity {


    @BindView(R.id.select_btn1)
    Button selectBtn1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void afterBindView() {

        selectBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(floatwinActivity.this, FloatService.class);
                    startService(intent);
            }
        });
    }


}
