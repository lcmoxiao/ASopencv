package com.example.wocaowocao.depository;

import com.example.wocaowocao.base.CMD;

import java.io.File;
import java.util.ArrayList;

 class dataManager {
    ArrayList<singleBean> mList = new ArrayList<>();

     dataManager(){
         int MovNub = new File(CMD.dataPath).listFiles().length;
         for(int i =1;i<=MovNub;i++)
         {
             int imageNub = new File(CMD.dataPath+"MOV"+i+"/images" ).listFiles().length;
             mList.add(new singleBean(String.valueOf(imageNub),String.valueOf(i)));
         }
     }




}
