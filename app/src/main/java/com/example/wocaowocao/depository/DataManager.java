package com.example.wocaowocao.depository;

import com.example.wocaowocao.base.CMD;

import java.io.File;
import java.util.ArrayList;

import static com.example.wocaowocao.base.CMD.getMovLength;

class DataManager {


     ArrayList<singleBean> mList = new ArrayList<>();

     DataManager(){
         update();
     }


     int getSize(){
         return mList.size();
     }

    void update(){
        ArrayList<singleBean> List = new ArrayList<>();
        int MovNub = getMovLength();
        for(int i =1;i<=MovNub;i++)
        {
            int imageNub = new File(CMD.dataPath+"MOV"+i+"/images" ).listFiles().length;
            List.add(new singleBean(String.valueOf(imageNub),String.valueOf(i)));
        }
        mList=List;
    }

}
