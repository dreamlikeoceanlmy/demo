package com.example.demo.Utils;

import com.alibaba.fastjson.JSONObject;

public enum MyResponseBodyJsonUtil {
    SUCCESS(true),
    FAILURE(false);
    Boolean isSuccess;
    MyResponseBodyJsonUtil(boolean isSuccess){
        this.isSuccess=isSuccess;
    }
    public JSONObject getFailureJson(Object error){
     return new JSONObject(true).fluentPut("isSuccess", false).fluentPut("errorMsg", error);
    }
    public JSONObject getSuccessJson(){
        return new JSONObject(true).fluentPut("isSuccess", true);
    }

}
