package com.example.wocaowocao.fileManager;

import com.example.wocaowocao.base.CMD;

import java.io.File;
import java.util.ArrayList;

 class dataManager {
    ArrayList<singleBean> mList = new ArrayList<>();

     dataManager(){
         int MovNub = new File(CMD.dataPath).listFiles().length;
         for(int i =1;i<=MovNub;i++)
         {
             String desc = "MOV"+i;
             int imageNub = new File(CMD.dataPath+desc+"/images" ).listFiles().length;
             mList.add(new singleBean(imageNub,desc));
         }
     }




}
