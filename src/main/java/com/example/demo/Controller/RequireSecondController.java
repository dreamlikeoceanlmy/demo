package com.example.demo.Controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.Entity.RequireSecond;
import com.example.demo.MyException.EmptyClassException;
import com.example.demo.MyException.ParamNotFoundException;
import com.example.demo.Service.RequireSecondService;
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
@RequestMapping("/RequireSecond")
public class RequireSecondController {
    @Value(value = "${requireSecond.photo.path}")
    private String photoPath;
    @Autowired
    private RequireSecondService requireSecondService;
    @PostMapping("/user/test")
    public JSONObject fun(
                      MultipartFile multipartFile){
        if (!multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf('.')).equals(".jpg")) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson("ASD");
        }
        try {
            System.out.println("Asdasdas");
            multipartFile.transferTo(new File(photoPath+"asd.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(true).fluentPut("Dsa","dasd");
    }
    @PostMapping("/user/getRequireSecond")
    public JSONObject getRequireSecond(HttpSession httpSession,int id){
        var loginUserFromSession = MySessionUtil.getLoginUserFromSession(httpSession);
        requireSecondService.userGetHisRequiredSecond(id,loginUserFromSession.getId());
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @GetMapping("/user/showMyRequireSecond")
    public JSONObject showMyRequireSecond(HttpSession httpSession,
                                      @RequestParam(defaultValue = "1")int current,
                                      @RequestParam(defaultValue = "5")int size){
        var loginUserFromSession = MySessionUtil.getLoginUserFromSession(httpSession);
        var myRequiredSecondInfo= requireSecondService.showMyRequiredSecondByUserId(loginUserFromSession.getId(), current, size);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson()
                .fluentPut("total", myRequiredSecondInfo.size())
                .fluentPut("myRequiredSecondInfo",myRequiredSecondInfo);
    }
    @PostMapping("/user/addNewRequireSecond")
    public JSONObject addNewRequireSecond(@Validated RequireSecond requireSecond,
                                          BindingResult bindingResult,
                                      HttpSession httpSession,
                                      MultipartFile multipartFile){
        if (bindingResult.hasErrors()) {
            var errorList = bindingResult.getAllErrors().stream().map(e->e.getDefaultMessage()).collect(Collectors.toList());
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
        requireSecond.setUserId(loginUserFromSession.getId());
        requireSecond.setPhotoPath(newPhotoPath);
        requireSecondService.addNewRequireSecond(requireSecond);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @GetMapping("/public/showRequireSecondByClass")
    public JSONObject showRequireByClass(String firstClass, String secondClass,
                                     @RequestParam(defaultValue = "1")int current,
                                     @RequestParam(defaultValue = "5")int size){
        List<RequireSecond> requireSecondByClass;
        try {
            requireSecondByClass = requireSecondService.showRequireSecondByClass(firstClass, secondClass, current, size);
        } catch (EmptyClassException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson()
                .fluentPut("total", requireSecondByClass.size())
                .fluentPut("requireSecondInfo",requireSecondByClass);
    }
    @PostMapping("/user/modifyMyRequireSecond")
    public JSONObject modifyMyRequireSecond(@Validated RequireSecond requireSecond,
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
        requireSecond.setUserId(loginUserFromSession.getId());
        requireSecond.setPhotoPath(newPhotoPath);
        try {
            requireSecondService.updateMyRequireById(requireSecond);
        } catch (ParamNotFoundException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
}
