package com.example.demo.Controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.Entity.LoginUser;
import com.example.demo.Service.LoginUserService;
import com.example.demo.Utils.MyResponseBodyJsonUtil;
import com.example.demo.Utils.MySessionUtil;
import com.example.demo.Utils.MyStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/User")
public class LoginUserController {
    @Autowired
    private LoginUserService loginUserService;
    @Value(value = "${loginUser.photo.path}")
    private String photoPath;
    @PostMapping("/normal/modifyNickname")
    @ResponseBody
    public JSONObject modifyNickName(String newNickname,
                                     HttpSession httpSession){
//        var newNickname=JSON.parseObject(nickname).getString("newNickname");
        if (MyStringUtil.isBlank(newNickname)) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson("your new nickname is blank");
        }
        var loginUserFromSession = MySessionUtil.getLoginUserFromSession(httpSession);
        loginUserService.modifyMyNickName(loginUserFromSession);
        loginUserFromSession.setNickname(newNickname);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson()
                .fluentPut("msg", "your new nickname is "+newNickname);
    }
    @PostMapping("/normal/submitAuthInfo")
    @ResponseBody
    public JSONObject submitAuthInfo(@Validated LoginUser loginUser,
                         BindingResult bindingResult,
                         HttpSession httpSession){
        if (bindingResult.hasErrors()) {
            var errorList = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(errorList);
        }
        loginUserService.submitAuthInfo(loginUser,MySessionUtil.getLoginUserFromSession(httpSession).getId());
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson().fluentPut("msg", "please wait for admin to check it");
    }
    @PostMapping("/normal/modifyPhoto")
    @ResponseBody
    public JSONObject modifyPhoto(MultipartFile multipartFile,
                              HttpSession httpSession){
        var loginUserFromSession = MySessionUtil.getLoginUserFromSession(httpSession);
        var originalFilename = multipartFile.getOriginalFilename();
        var newPhotoPath=photoPath+ LocalDate.now()+"-"+loginUserFromSession.getId()+originalFilename.substring(originalFilename.lastIndexOf('.'), originalFilename.length());
        try {
            multipartFile.transferTo(new File(newPhotoPath));
        } catch (IOException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson("upload fail");
        }
        //上传和修改值不能反
        loginUserFromSession.setPhotoPath(newPhotoPath);
        loginUserService.modifyMyPhotoPath(loginUserFromSession);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }

    //todo 注册后自动登录
    @PostMapping("/public/register")
    @ResponseBody
    public JSONObject register(String password, String username, String nickname, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        try {
           loginUserService.register(username, password, nickname);
        } catch (DataIntegrityViolationException e) {
         return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getClass());
        }
        try {
            httpServletRequest.getRequestDispatcher("/Login").forward(httpServletRequest,httpServletResponse);
        } catch (ServletException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getClass());
        } catch (IOException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getClass());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }

}
