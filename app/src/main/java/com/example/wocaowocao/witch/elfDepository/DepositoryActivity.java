package com.example.wocaowocao.witch.elfDepository;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wocaowocao.R;
import com.example.wocaowocao.base.BaseActivity;
import com.example.wocaowocao.base.ViewInject;
import com.example.wocaowocao.witch.elfDataBaseManager;

import butterknife.BindView;

@ViewInject(main_layout_id = R.layout.activity_depository)
public class DepositoryActivity extends BaseActivity {

    //动作控制中心MOVnub
    public static int MOVnub = 1;

    //主页中的RecycleView
    @BindView(R.id.mRecycleView)
    RecyclerView mRecycleView;

    depositoryDataManager dataManager;
    DepositoryAdapter madapter;
    static elfDataBaseManager elfManager;

    //初始化界面
    private void initRecycleView() {
        elfManager = new elfDataBaseManager(this);
        dataManager = new depositoryDataManager();
        //设置布局管理器
        mRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //设置适配器adapter
        madapter = new DepositoryAdapter(dataManager.mList, this);
        mRecycleView.setAdapter(madapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataManager.update();
        madapter.updateData(dataManager.mList);
        madapter.notifyDataSetChanged();
    }

    @Override
    public void afterBindView() {
        initRecycleView();
    }

    @Override
    protected void onDestroy() {
        elfManager.close();
        super.onDestroy();
    }


}
