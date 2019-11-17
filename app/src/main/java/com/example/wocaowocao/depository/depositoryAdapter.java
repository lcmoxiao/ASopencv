package com.example.wocaowocao.depository;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wocaowocao.R;
import com.example.wocaowocao.base.CMD;
import com.example.wocaowocao.elf.CoreActivity;

import java.util.ArrayList;


public class depositoryAdapter extends RecyclerView.Adapter<depositoryAdapter.MyHolder>{

    private final ArrayList<singleBean> data;
    private final Context mContext;
    //传入数据
    depositoryAdapter(ArrayList<singleBean> data, Context mContext)
    {
        this.data = data;
        this.mContext = mContext;
    }


    //自定义Holder
    class MyHolder extends RecyclerView.ViewHolder {
        TextView length;
        TextView desc;
        Button btn;
        MyHolder(View itemView) {
            super(itemView);
            length = itemView.findViewById(R.id.length);
            desc = itemView.findViewById(R.id.desc);
            btn = itemView.findViewById(R.id.enter);
        }
    }

    @NonNull
    //创建ViewHolder
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //将我们自定义的item布局R.layout.item_one转换为View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simulateitem, parent, false);
        //返回这个MyHolder实体
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.length.setText(data.get(position).length);
        holder.desc.setText(data.get(position).desc);
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    CMD.MOVnub = position + 1;
                    Intent intent = new Intent(mContext, CoreActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
