package com.example.wocaowocao.witch.elfDepository;

import com.example.wocaowocao.base.CMD;

import java.io.File;
import java.util.ArrayList;



class DataManager {

     ArrayList<singleBean> mList = new ArrayList<>();

     DataManager(){
         update();
     }

    void update(){
        ArrayList<singleBean> List = new ArrayList<>();
        int MovNub = new File(CMD.dataPath).listFiles().length;
        for(int i =1;i<=MovNub;i++)
        {
            int imageNub = new File(CMD.dataPath+"MOV"+i+"/images" ).listFiles().length;
            List.add(new singleBean(String.valueOf(imageNub),String.valueOf(i)));
        }
        mList=List;
    }

}
