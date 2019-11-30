package com.example.wocaowocao.witch.elfDepository;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wocaowocao.R;
import com.example.wocaowocao.RootShell.RootShell;
import com.example.wocaowocao.base.CMD;
import com.example.wocaowocao.witch.elf.CoreActivity;

import java.util.ArrayList;

import static com.example.wocaowocao.witch.elfDepository.DepositoryActivity.elfManager;


public class DepositoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<singleBean> data;
    private final Context mContext;
    //传入数据
    DepositoryAdapter(ArrayList<singleBean> data, Context mContext)
    {
        this.data = data;
        this.mContext = mContext;
    }


    //自定义Holder
    class ItemHolder extends RecyclerView.ViewHolder {
        TextView length;
        TextView desc;
        Button btn;
        ItemHolder(View itemView) {
            super(itemView);
            length = itemView.findViewById(R.id.length);
            desc = itemView.findViewById(R.id.desc);
            btn = itemView.findViewById(R.id.enter);
        }
    }

    //自定义Holder
    class ButtonHolder extends RecyclerView.ViewHolder {
        Button add;
        Button kill;
        ButtonHolder(View itemView) {
            super(itemView);
            add = itemView.findViewById(R.id.newelf);
            kill = itemView.findViewById(R.id.killelf);
        }
    }

    //自定义Holder
    class NullHolder extends RecyclerView.ViewHolder {
        NullHolder(View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
            {
                //将我们自定义的item布局R.layout.item_one转换为View
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alf, parent, false);
                //返回这个MyHolder实体
                return new ItemHolder(view);
            }
            case 1:
            {
                //将我们自定义的item布局R.layout.item_one转换为View
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_elfmag, parent, false);
                //返回这个MyHolder实体
                return new ButtonHolder(view);
            }
        }
        return new NullHolder(null);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case 0:
                ItemHolder itemHolder = (ItemHolder)holder;
                itemHolder.length.setText(data.get(position).length);
                itemHolder.desc.setText(data.get(position).desc);
                itemHolder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!RootShell.isRootAvailable()) Toast.makeText(mContext,"没ROOT你用不了,放弃吧", Toast.LENGTH_SHORT).show();
                        else {
                            DepositoryActivity.MOVnub = position + 1;
                            Intent intent = new Intent(mContext, CoreActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            mContext.startActivity(intent);
                        }
                    }
                });
                break;
            case 1:
                ButtonHolder buttonHolder = (ButtonHolder)holder;
                buttonHolder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int MOVnub = data.size()+1;
                        elfManager.create_MOV(MOVnub);
                        data.add(new singleBean(0,MOVnub));
                        notifyDataSetChanged();
                    }
                });
                if(position!=0)
                { buttonHolder.kill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int MOVnub = data.size();
                        elfManager.delete_MOV(MOVnub);
                        data.remove(MOVnub-1);
                        notifyDataSetChanged();

                    }
                });
                    buttonHolder.kill.setVisibility(View.VISIBLE);
                }else{
                    buttonHolder.kill.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    void updateData(ArrayList<singleBean> data)
    {
        this.data = data;
    }

    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == data.size())return 1;
        else return 0;
    }
}
