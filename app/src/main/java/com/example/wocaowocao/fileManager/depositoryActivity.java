package com.example.wocaowocao.fileManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wocaowocao.base.BaseActivity;
import com.example.wocaowocao.base.ViewInject;
import com.example.wocaowocao.R;

import butterknife.BindView;

@ViewInject(main_layout_id = R.layout.activity_depository)
public class depositoryActivity extends BaseActivity {



    //主页中的RecycleView
    @BindView(R.id.mRecycleView)
    RecyclerView mRecycleView;

    //初始化界面
    private void initRecycleView() {
        dataManager data = new dataManager();
        //设置布局管理器
        mRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置适配器adapter
        mRecycleView.setAdapter( new depositoryAdapter(data.mList));
    }

    @Override
    public void afterBindView()   {

        initRecycleView();

    }
}
