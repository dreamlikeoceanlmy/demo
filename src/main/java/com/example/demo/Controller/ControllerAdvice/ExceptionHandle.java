package com.example.demo.Controller.ControllerAdvice;

import com.example.demo.Utils.MyResponseBodyJsonUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.UUID;
@RestControllerAdvice
public class ExceptionHandle {
    @ExceptionHandler(Exception.class)
    public void exceptionHandle(HttpServletResponse httpServletResponse, Exception e,
                                HttpServletRequest httpServletRequest)throws IOException {
        var exceptionDESC = MyResponseBodyJsonUtil.FAILURE.getFailureJson(e.getMessage())
                .fluentPut("requestURI", httpServletRequest.getRequestURI())
                .fluentPut("timeStamp", LocalDateTime.now())
                .fluentPut("errorId", UUID.randomUUID().toString());
        PrintWriter writer = httpServletResponse.getWriter();
        writer.print(exceptionDESC);
        writer.flush();
        writer.close();
    }
}
