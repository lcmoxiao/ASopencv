package com.example.wocaowocao.witch.elfDepository;

class singleBean {
    String length ;
    String desc ;


    singleBean(String _length,String _desc)
    {
        length=_length;
        desc=_desc;
    }

    singleBean(int _length,int _desc)
    {
        length= String.valueOf(_length);
        desc= String.valueOf(_desc);
    }


}
