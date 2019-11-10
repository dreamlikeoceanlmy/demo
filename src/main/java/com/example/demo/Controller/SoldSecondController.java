package com.example.demo.Controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.Entity.SoldSecond;
import com.example.demo.MyException.EmptyClassException;
import com.example.demo.MyException.ParamNotFoundException;
import com.example.demo.Service.SoldSecondService;
import com.example.demo.Utils.MyResponseBodyJsonUtil;
import com.example.demo.Utils.MySessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/SoldSecond")
public class SoldSecondController {
    @Autowired
    private SoldSecondService soldSecondService;
    @Value(value = "${soldSecond.photo.path}")
    private String photoPath;
    @PostMapping("/user/userGetSoldSecond")
    public JSONObject getSoldSecond(HttpSession httpSession, int id){
        var loginUserFromSession = MySessionUtil.getLoginUserFromSession(httpSession);
        soldSecondService.userSoldHisSecond(id,loginUserFromSession.getId());
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @GetMapping("/user/showMySoldSecond")
    public JSONObject showMyRequireSecond(HttpSession httpSession,
                                      @RequestParam(value = "1")int current,
                                      @RequestParam(value = "5")int size){
        var loginUserFromSession = MySessionUtil.getLoginUserFromSession(httpSession);
        var mySoldSecondInfo= soldSecondService.showMySoldSecondByUserId(current,size,loginUserFromSession.getId());
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson()
                .fluentPut("total",mySoldSecondInfo.size())
                .fluentPut("mySoldSecondInfo",mySoldSecondInfo);
    }
    @PostMapping("/user/addNewSoldSecond")
    public JSONObject addNewSoldSecond(@Validated SoldSecond soldSecond,
                                   BindingResult bindingResult,
                                   HttpSession httpSession,
                                   MultipartFile multipartFile){
        if (bindingResult.hasErrors()) {
            var errorList = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(errorList);
        }
        var loginUserFromSession = MySessionUtil.getLoginUserFromSession(httpSession);
        var originalFilename = multipartFile.getOriginalFilename();
        var newPhotoPath=photoPath+ LocalDate.now()+loginUserFromSession.getId()+originalFilename.substring(originalFilename.lastIndexOf('.'), originalFilename.length());
        try {
            multipartFile.transferTo(new File(newPhotoPath));
        } catch (IOException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson("upload fail");
        }
        soldSecond.setId(loginUserFromSession.getId());
        soldSecond.setPhotoPath(newPhotoPath);
        soldSecondService.addNewSoldSecond(soldSecond);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @GetMapping("/public/showSoldSecondByClass")
    public JSONObject showRequireByClass(String firstClass, String secondClass,
                                     @RequestParam(defaultValue = "1")int current,
                                     @RequestParam(defaultValue = "5")int size){
        List<SoldSecond> soldSecondByClass;
        try {
            soldSecondByClass = soldSecondService.showAllSoldSecondByClass(current,size,firstClass,secondClass);
        } catch (EmptyClassException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson()
                .fluentPut("total", soldSecondByClass.size())
                .fluentPut("soldSecondInfo",soldSecondByClass);
    }
    @PostMapping("/user/modifyMySoldSecond")
    public JSONObject modifyMySoldSecond(@Validated SoldSecond soldSecond,
                                   BindingResult bindingResult,
                                   HttpSession httpSession,
                                   MultipartFile multipartFile){
        if (bindingResult.hasErrors()) {
            var errorList = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(errorList);
        }
        var loginUserFromSession = MySessionUtil.getLoginUserFromSession(httpSession);
        var originalFilename = multipartFile.getOriginalFilename();
        var newPhotoPath=photoPath+ LocalDate.now()+"-"+loginUserFromSession.getId()+originalFilename.substring(originalFilename.lastIndexOf('.'), originalFilename.length());
        try {
            multipartFile.transferTo(new File(newPhotoPath));
        } catch (IOException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson("upload fail");
        }
        soldSecond.setId(loginUserFromSession.getId());
        soldSecond.setPhotoPath(newPhotoPath);
        try {
            soldSecondService.updateMySoldByUserId(soldSecond);
        } catch (ParamNotFoundException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
}
