package com.example.demo.Controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.Entity.Report;
import com.example.demo.Service.ReportService;
import com.example.demo.Utils.MyResponseBodyJsonUtil;
import com.example.demo.Utils.MySessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Report")
public class ReportController {
    @Value(value = "${report.photo.path}")
    private String photoPath;
    @Autowired
    private ReportService reportService;
    @PostMapping("/normal/addNewReport")
    public JSONObject report(@Validated Report report,
                             BindingResult bindingResult,
                             HttpSession httpSession,
                             MultipartFile multipartFile){
        if (bindingResult.hasErrors()) {
            var errorList = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson(errorList);
        }
        var loginUserFromSession = MySessionUtil.getLoginUserFromSession(httpSession);
        var originalFilename = multipartFile.getOriginalFilename();
        var newPhotoPath=photoPath+ LocalDate.now()+"-"+loginUserFromSession.getId()+originalFilename.substring(originalFilename.lastIndexOf('.'), originalFilename.length());
        report.setInformerId(loginUserFromSession.getId());
        report.setPhotoPath(newPhotoPath);
        try {
            multipartFile.transferTo(new File(newPhotoPath));
        } catch (IOException e) {
            return MyResponseBodyJsonUtil.FAILURE.getFailureJson("upload fail");
        }
        reportService.addNewReport(report);
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson();
    }
    @GetMapping("/normal/showMyReport")
    public JSONObject showMyReport(HttpSession httpSession,
                         @RequestParam(value = "current",defaultValue = "1")int current,
                         @RequestParam(value = "size",defaultValue = "5")int size){
        var loginUserFromSession = MySessionUtil.getLoginUserFromSession(httpSession);
        var reportInfoList = reportService.showAllReportByUserId(current, size, loginUserFromSession.getId());
        return MyResponseBodyJsonUtil.SUCCESS.getSuccessJson()
                .fluentPut("total", reportInfoList.size())
                .fluentPut("reportInfo", reportInfoList);
    }

}
