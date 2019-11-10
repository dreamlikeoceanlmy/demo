package com.example.demo.Config;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.Entity.LoginUser;
import com.example.demo.Service.LoginUserService;
import com.example.demo.Utils.MyContentTypeUtil;
import com.example.demo.Utils.MyResponseBodyJsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.PrintWriter;
import java.util.HashMap;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class MySpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
     LoginUserService loginUserService;
    @Value("${tokenValiditySeconds}")
    int tokenValiditySeconds;

@Bean
    PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(10);
}
@Bean
//角色继承
    RoleHierarchy roleHierarchy(){
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    String hierarchy="ROLE_admin > ROLE_user \n ROLE_user > ROLE_banned";
    roleHierarchy.setHierarchy(hierarchy);
    return  roleHierarchy;
}

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(loginUserService);
    }

    @Override
       protected void configure(HttpSecurity http) throws Exception {
           http.authorizeRequests()
                   .antMatchers("/**/user/**").hasRole("user")
                   .antMatchers("/Management/**").hasRole("admin")
                   .antMatchers("/**/public/**").permitAll()
                   .anyRequest().authenticated()
                   .and()
                   .formLogin()
                   //默认参数名username，password
                   .usernameParameter("username")
                   .passwordParameter("password")
                   .loginProcessingUrl("/Login").permitAll()
                   .successHandler(((httpServletRequest, httpServletResponse, authentication) ->
                   {
                       httpServletResponse.setContentType("application/json;charset=utf-8");
                       httpServletResponse.getWriter().print(
                               MyResponseBodyJsonUtil.SUCCESS.getSuccessJson().fluentPut("msg", ((LoginUser) authentication.getPrincipal()).show())
                            //   .fluentPut("requestURI", httpServletRequest.getRequestURI())
                               .toJSONString());
                       httpServletResponse.getWriter().flush();
                       httpServletResponse.getWriter().close();
                   }
                   ))
                   .failureHandler(((httpServletRequest, httpServletResponse, e) -> {
                       HashMap<String, Object> hashMap = new HashMap<>();
                       httpServletResponse.setContentType("application/json;charset=utf-8");
                       httpServletResponse.setStatus(401);
                       hashMap.put("status", 500);
                       if (e instanceof BadCredentialsException) {
                           hashMap.put("msg", "login fail,password or username is wrong");
                       }else if(e instanceof DisabledException){
                           hashMap.put("msg", "login fail,your account is disabled");
                       }else {
                           hashMap.put("msg", "login fail");
                       }
                       httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(hashMap));
                       httpServletResponse.getWriter().flush();
                       httpServletResponse.getWriter().close();
                   }))
                   .and()
                   .logout()
                   .logoutUrl("/LoginOut").permitAll()
                   //清除身份认证，默认开
                   .clearAuthentication(true)
                   //是否使session失效，默认开
                   .invalidateHttpSession(true)
                   //进行数据清除工作，比如说cookie清除
                   // .addLogoutHandler()
                   .logoutSuccessHandler(((httpServletRequest, httpServletResponse, authentication) -> {
                       PrintWriter writer = httpServletResponse.getWriter();
                       writer.write(new ObjectMapper().writeValueAsString(new HashMap<String,String>().put("msg", "login out ready")));
                       writer.flush();
                       writer.close();
                   }))
                   .deleteCookies("remember-me")
                   .and()
                   .csrf().disable()
                   //配置全json
                   .exceptionHandling()
                   .authenticationEntryPoint((httpServletRequest,httpServletResponse,e)->{
                       httpServletResponse.setContentType(MyContentTypeUtil.JSON.getContentType());
                       httpServletResponse.getWriter().print(new JSONObject(true)
                               .fluentPut("message",e.getMessage())
                               .fluentPut("requestURI", httpServletRequest.getRequestURI())
                               .toJSONString());
                       httpServletResponse.getWriter().flush();
                       httpServletResponse.getWriter().close();
                   })
                   .and()
                   .rememberMe()
                   .userDetailsService(loginUserService)
                   //配置文件中修改
                   .tokenValiditySeconds(tokenValiditySeconds)
                   //设置参数名 默认remember me
                   .rememberMeParameter("remember-me");

   }
}
