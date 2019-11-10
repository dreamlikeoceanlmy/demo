package com.example.demo.Utils;

import com.example.demo.Entity.LoginUser;
import org.springframework.security.core.context.SecurityContextImpl;

import javax.servlet.http.HttpSession;

public class MySessionUtil {
    public static LoginUser getLoginUserFromSession(HttpSession httpSession){
        return (LoginUser)(((SecurityContextImpl) httpSession.getAttribute("SPRING_SECURITY_CONTEXT")).getAuthentication().getPrincipal());
    }
}
