package com.example.wocaowocao.witch.elfDepository;

import java.util.ArrayList;

import static com.example.wocaowocao.witch.elfDepository.DepositoryActivity.elfManager;


class depositoryDataManager {

     ArrayList<singleBean> mList = new ArrayList<>();


     depositoryDataManager(){
         update();
     }

    void update(){
        ArrayList<singleBean> List = new ArrayList<>();
        int MovNub = elfManager.getMovNub();
        for(int i =1;i<=MovNub;i++)
        {
            int imageNub = elfManager.getMovSize(i);
            List.add(new singleBean(String.valueOf(imageNub),String.valueOf(i)));
        }
        mList = List;
    }

//    void oldupdate(){
//        ArrayList<singleBean> List = new ArrayList<>();
//        int MovNub = new File(CMD.dataPath).listFiles().length;
//        for(int i =1;i<=MovNub;i++)
//        {
//            int imageNub = new File(CMD.dataPath+"MOV"+i+"/images" ).listFiles().length;
//            List.add(new singleBean(String.valueOf(imageNub),String.valueOf(i)));
//        }
//        mList=List;
//    }


}
