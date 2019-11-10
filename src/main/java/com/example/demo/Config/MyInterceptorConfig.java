package com.example.demo.Config;

import com.example.demo.Utils.MyResponseBodyJsonUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class MyInterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new uploadInterceptor()).addPathPatterns("/**");
    }
}
class uploadInterceptor implements HandlerInterceptor{
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request instanceof MultipartHttpServletRequest) {
            var multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            var filename = multipartHttpServletRequest.getFile("multipartFile").getOriginalFilename();
            filename=filename.substring(filename.lastIndexOf('.'));
            if (filename.equals(".jpg")) {
                return true;
            }
            else {
                response.getWriter().print(MyResponseBodyJsonUtil.FAILURE.getFailureJson("wrong photo format"));
                return false;}
        }
        else {
            return true;}
    }
}
