package com.example.demo.Controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.Entity.Role;
import com.example.demo.MyException.EmptyClassException;
import com.example.demo.MyException.ParamNotFoundException;
import com.example.demo.Service.LoginUserService;
import com.example.demo.Service.ReportService;
import com.example.demo.Service.RequireSecondService;
import com.example.demo.Service.SoldSecondService;
import com.example.demo.Utils.MyResponseBodyJsonUtil;
import com.example.demo.Utils.MySessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/Management")
public class ManagementController {
    @Autowired
    private LoginUserService loginUserService;
    @Autowired
    private RequireSecondService requireSecondService;
    @Autowired
    private SoldSecondService soldSecondService;
    @Autowired
    private ReportService reportService;
    //region loginUser
    @PostMapping("/User/banOneUserByStudentNumber")
    public JSONObject banOneUserByStudentNumber(String studentNumber, String banReason){
        try {
            loginUserService.banLoginUserByStudentNumber(studentNumber,banReason);
        } catch (ParamNotFoundException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @PostMapping("/User/banOneUserByUserId")
    public JSONObject banOneUserByUserId(int userId,String banReason){
        try {
            loginUserService.banUserById(userId, banReason);
        } catch (ParamNotFoundException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @PostMapping("/User/deBanUserOrAuthByStudentNumber")
    public JSONObject deBanUserOrAuthByStudentNumber(String studentNumber){
        try {
            loginUserService.deBanUserOrAuthByStudentNumber(studentNumber);
        } catch (ParamNotFoundException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @PostMapping("/User/deBanUserOrAuthByUserId")
    public JSONObject deBanUserOrAuthByUserId(int userId){
        try {
            loginUserService.deBanUserOrAuthByUserId(userId);
        } catch (ParamNotFoundException e) {
            return MyResponseBodyJsonUtil.SUCCESS.getFailureJson(e.getMessage());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @GetMapping("/User/showAllUserByRole")
    public JSONObject showAllUserByRole(@RequestParam(defaultValue = "1")int current,
                                    @RequestParam(defaultValue = "5")int size,
                                    String role){
        Role userRole;
        //流式操作判断是否存在,想了想还是用异常的方法吧
        //var isMatch = Stream.of(Role.values()).map(Role::getRole).anyMatch(r -> r.substring(r.lastIndexOf('_') + 1).equals(role));
        try {
            userRole=Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson("wrong role");
        }
        var loginUsersInfoList = loginUserService.showUserByRole(current, size, userRole);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson()
                .fluentPut("total", loginUsersInfoList.size())
                .fluentPut("loginUserInfo", loginUsersInfoList);
    }
    @PostMapping("/User/ShowOneUserByStudentNumber")
    public JSONObject showOneUserByStudentNumber(String studentNumber){
        try {
            return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson().fluentPut("UserInfo",loginUserService.showOneUserByStudentNumber(studentNumber));
        } catch (ParamNotFoundException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
    }
    @PostMapping("/User/ShowOneUserByUserId")
    public JSONObject showOneUserByUserId(int id){
        try {
            var userById = loginUserService.showOneUserById(id);
            return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson().fluentPut("userInfo", userById);
        } catch (ParamNotFoundException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
    }
    //endregion
    //region requireSecond
    @PostMapping("/RequireSecond/checkRequireSecondById")
    public JSONObject checkRequire(HttpSession httpSession,int id,String banReason){
        try {
            requireSecondService.checkRequireSecondById(id,banReason, MySessionUtil.getLoginUserFromSession(httpSession).getId());
        } catch (ParamNotFoundException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @PostMapping("/RequireSecond/modifyClassById")
    public JSONObject modifyClassByIdRequire(int id,String firstReason,String secondFirst){
        try {
            requireSecondService.modifyClassById(id,firstReason,secondFirst);
        } catch (EmptyClassException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @GetMapping("/RequireSecond/showRequiredSecondUnchecked")
    public JSONObject showRequiredSecondUnchecked(@RequestParam(defaultValue = "1")int current,
                       @RequestParam(defaultValue = "5")int size){
        var requireSecondList = requireSecondService.showRequiredSecondUnchecked(current, size);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson().fluentPut("total", requireSecondList.size())
                .fluentPut("RequiredSecondUnchecked", requireSecondList);
    }
    @GetMapping("/RequireSecond/showAllRequiredSecond")
    public JSONObject showAllRequiredSecond(@RequestParam(defaultValue = "1")int current,
                      @RequestParam(defaultValue = "5")int size){
        var requireSecondList = requireSecondService.showAllRequiredSecond(current, size);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson().fluentPut("total", requireSecondList.size())
                .fluentPut("showAllRequiredSecond", requireSecondList);
    }
    //endregion
    //region soldSecond
    @PostMapping("/SoldSecond/checkSoldSecondById")
    public JSONObject checkSoldSecondById(int id,String banReason,HttpSession httpSession){
        var checkUserId = MySessionUtil.getLoginUserFromSession(httpSession).getId();
        try {
            soldSecondService.checkSoldSecondById(id,banReason,checkUserId);
        } catch (ParamNotFoundException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @PostMapping("/SoldSecond/modifyClassById")
    public JSONObject modifyClassByIdSold(int id,String firstClass,String secondClass){
        try {
            soldSecondService.modifyClassById(id,firstClass,secondClass);
        } catch (EmptyClassException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage());
        }
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @GetMapping("/SoldSecond/showAllSoldSecondUnchecked")
    public JSONObject showAllSoldSecondUnchecked(@RequestParam(defaultValue = "1")int size,
                                             @RequestParam(defaultValue = "5")int current){
        var soldSecondList = soldSecondService.showAllSoldSecondUnchecked(current, size);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson().fluentPut("total", soldSecondList.size())
                .fluentPut("RequiredSecondUnchecked", soldSecondList);
    }
    @GetMapping("/SoldSecond/showAllSoldSecond(")
    public JSONObject showAllSoldSecond(@RequestParam(defaultValue = "1")int current,
                                    @RequestParam(defaultValue = "5")int size){
        var requireSecondList = soldSecondService.showAllSoldSecond(current, size);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson().fluentPut("total", requireSecondList.size())
                .fluentPut("soldSecondInfo", requireSecondList);
    }
    //endregion
    //region report
    @PostMapping("/Report/checkReportById")
    public JSONObject checkReportById(HttpSession httpSession,int id,String failReason){
        var checkUserId = MySessionUtil.getLoginUserFromSession(httpSession).getId();
        reportService.checkReportById(id,checkUserId,failReason);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @GetMapping("/Report/showAllReportUnchecked")
    public JSONObject showAllReportUnchecked(@RequestParam(defaultValue = "1")int current,
                                         @RequestParam(defaultValue = "5")int size){
        var reportList = reportService.showAllReportUnchecked(current, size);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson().fluentPut("total", reportList.size())
                .fluentPut("reportInfo", reportList);
    }
    @GetMapping("/Report/showAllReportChecked")
    public JSONObject showAllReportChecked(@RequestParam(defaultValue = "1")int current,
                                         @RequestParam(defaultValue = "5")int size){
        var reportList = reportService.showAllReportChecked(current, size);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson().fluentPut("total", reportList.size())
                .fluentPut("reportInfo", reportList);
    }
    @GetMapping("/Report/showAllReport")
    public JSONObject showAllReport(@RequestParam(defaultValue = "1")int current,
                                         @RequestParam(defaultValue = "5")int size){
        var reportList = reportService.showAllReport(current, size);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson().fluentPut("total", reportList.size())
                .fluentPut("reportInfo", reportList);
    }
    @GetMapping("/Report/showAllReportFailed")
    public JSONObject showAllReportFailed(@RequestParam(defaultValue = "1")int current,
                                         @RequestParam(defaultValue = "5")int size){
        var reportList = reportService.showAllReportFailed(current, size);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson().fluentPut("total", reportList.size())
                .fluentPut("reportInfo", reportList);
    }

    //endregion
}

