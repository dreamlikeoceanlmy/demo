package com.example.demo.Entity;

public enum UserAccountState {
    BANNED("your account is banned"),
    FREE("your account is free");
    private String msg;
    UserAccountState(String msg){
        this.msg=msg;
    }
    public String getState(){
        return msg;
    }
}
