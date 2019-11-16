package com.example.wocaowocao.fileManager;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wocaowocao.R;

import java.util.ArrayList;

public class depositoryAdapter extends RecyclerView.Adapter<depositoryAdapter.MyHolder>{

    private final ArrayList<singleBean> data;

    //传入数据
    depositoryAdapter(ArrayList<singleBean> data)
    {
        this.data = data;
    }


    //自定义Holder
    class MyHolder extends RecyclerView.ViewHolder {
        TextView length;
        TextView desc;
        MyHolder(View itemView) {
            super(itemView);
            length = itemView.findViewById(R.id.length);
            desc = itemView.findViewById(R.id.desc);
        }
    }

    @NonNull
    //创建ViewHolder
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //将我们自定义的item布局R.layout.item_one转换为View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        //返回这个MyHolder实体
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.length.setText( String.valueOf(data.get(position).length));
        holder.desc.setText(data.get(position).desc);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
