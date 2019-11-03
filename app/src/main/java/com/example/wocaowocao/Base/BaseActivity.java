package com.example.wocaowocao.Base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import butterknife.ButterKnife;


//实现ViewInject注释，进行Activity的初始化
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInject annotation = this.getClass().getAnnotation(ViewInject.class);
        if(annotation != null)
        {
            int main_layout_id = annotation.main_layout_id();
            if(main_layout_id > 0)
            {
                setContentView(main_layout_id);
                bindView();
                afterBindView();
            }
            else throw new RuntimeException("main_layout_id < 0");
        }
        else throw new RuntimeException("annotation == null");
    }

    public abstract void afterBindView();

    //view的依赖注入绑定
    private void bindView() {
        ButterKnife.bind(this);
    }
}