package com.example.demo.Entity;

public enum CAMPUS {
    XIANLIN("仙林"),
    SANPAILOU("三牌楼"),
    SUOJINCUN("锁金村");
    private String campus;
    CAMPUS(String campus){
        this.campus=campus;
    }
   public String getCampus(){
        return campus;
    }
}
