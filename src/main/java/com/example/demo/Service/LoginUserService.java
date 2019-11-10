package com.example.demo.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.DAO.LoginUserMapper;
import com.example.demo.Entity.LoginUser;
import com.example.demo.Entity.Role;
import com.example.demo.MyException.ParamNotFoundException;
import com.example.demo.Utils.MyStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoginUserService implements UserDetailsService {
    @Autowired
    private LoginUserMapper loginUserMapper;
    @Autowired
    private MessageService messageService;
    public void modifyMyPhotoPath(LoginUser loginUser){
        loginUserMapper.updateById(loginUser);
    }
    public void modifyMyNickName(LoginUser loginUser){
        loginUserMapper.updateById(loginUser);
    }
    public String showOneUserById(int id)throws ParamNotFoundException {
        var loginUser = loginUserMapper.selectById(id);
        if (loginUser == null) {
            throw new ParamNotFoundException("id not found");
        }
        return JSONObject.toJSONString(loginUser);
}
    public String showOneUserByStudentNumber(String studentNumber) throws ParamNotFoundException {
        LoginUser loginUser = loginUserMapper.selectOne(new QueryWrapper<LoginUser>().eq("student_number", studentNumber));
        if (loginUser == null) {
            throw new ParamNotFoundException("student number not found");
        }
        return JSONObject.toJSONString(loginUser);
    }
    public void banUserById(int id,String banReason)throws ParamNotFoundException{
        if (MyStringUtil.isBlank(banReason)) {
            throw new ParamNotFoundException("ban reason is blank");
        }
        LoginUser loginUser = LoginUser.builder().id(id).role(Role.BANNED.getRole()).banReason(banReason).build();
        if (loginUserMapper.updateById(loginUser)==0) {
            throw new ParamNotFoundException("id not found");
        }
//        messageService.sendMessage(UserAccountState.BANNED.getState(), id);
    }
    public void banLoginUserByStudentNumber(String studentNumber, String banReason)throws ParamNotFoundException{
        if (MyStringUtil.isBlank(banReason)) {
            throw new ParamNotFoundException("banReason is blank");
        }
        LoginUser build = LoginUser.builder().banReason(banReason).role(Role.BANNED.getRole()).build();
        if (loginUserMapper.update(build,new QueryWrapper<LoginUser>().eq("student_number",studentNumber))==0) {
            throw new ParamNotFoundException("student number not found");
        }
    }
    //如果某一账号降为用户也可用这个
    public void deBanUserOrAuthByUserId(int id)throws ParamNotFoundException{
        LoginUser loginUser = LoginUser.builder().role(Role.USER.getRole()).build();
        if (loginUserMapper.update(loginUser, new UpdateWrapper<LoginUser>().set("ban_reason", null).eq("id",id))==0) {
            throw new ParamNotFoundException("id not found");
        }
        //messageService.sendMessage(UserAccountState.FREE.getState(), id);
    }
    public void deBanUserOrAuthByStudentNumber(String studentNumber)throws ParamNotFoundException{
        if (MyStringUtil.isBlank(studentNumber)) {
            throw new ParamNotFoundException("student number is blank");
        }
        LoginUser loginUser = LoginUser.builder().role(Role.USER.getRole()).build();
        if (loginUserMapper.update(loginUser, new UpdateWrapper<LoginUser>().set("ban_reason", null).eq("student_number",studentNumber))==0) {
            throw  new ParamNotFoundException("student number not found");
        }
        //messageService.sendMessage(UserAccountState.FREE.getState(), id);
    }
    public void submitAuthInfo(LoginUser loginUser,int id){
        loginUser.setId(id);
        //防止恶意修改
        loginUser.setBanReason(null);
        loginUser.setRole(Role.BANNED.getRole());
        loginUserMapper.updateById(loginUser);
    }
    //传入ALL就全部查询
    public List<LoginUser.loginUser> showUserByRole(int current, int size,Role role){
        List<LoginUser.loginUser> list;
       switch (role){
           case BLACKLIST:
               list = loginUserMapper.selectPage(new Page<>(current,size),new QueryWrapper<LoginUser>().eq("role", Role.BLACKLIST.getRole()).ne("ban_reason", "account is not authenticated")).getRecords().stream()
                       .map(LoginUser::show).collect(Collectors.toList());
               break;
           case BANNED:
               list = loginUserMapper.selectPage(new Page<>(current,size),new QueryWrapper<LoginUser>().eq("role", Role.BANNED.getRole()).eq("ban_reason", "account is not authenticated")).getRecords().stream()
                       .map(LoginUser::show).collect(Collectors.toList());
               break;
           case USER:
               list = loginUserMapper.selectPage(new Page<>(current,size),new QueryWrapper<LoginUser>().eq("role", Role.USER.getRole())).getRecords().stream()
                       .map(LoginUser::show).collect(Collectors.toList());
               break;
           case ADMIN:
               list = loginUserMapper.selectPage(new Page<>(current,size),new QueryWrapper<LoginUser>().eq("role", Role.ADMIN.getRole())).getRecords().stream()
                       .map(LoginUser::show).collect(Collectors.toList());
               break;
               default:
                   list = loginUserMapper.selectPage(new Page<>(current,size),null).getRecords().stream()
                           .map(LoginUser::show).collect(Collectors.toList());
       }
       return list;
    }
    public void becomeOneToBeAdminById(int id){
        LoginUser loginUser = LoginUser.builder().id(id).role(Role.ADMIN.getRole()).build();
        loginUserMapper.updateById(loginUser);
    }
    public String register(String username,String password,String nickname)throws DataIntegrityViolationException {
        var encodePassword = new BCryptPasswordEncoder(10).encode(password);
        LoginUser loginUser = LoginUser.builder()
                .username(username)
                .password(encodePassword)
                .build();
        if (nickname==null||nickname.isBlank()) {
            loginUser.setNickname("NJUPT-"+ LocalDateTime.now().getYear()+"-"+UUID.randomUUID().toString().split("-")[0]);
        }
        loginUser.setNickname(nickname);
        loginUserMapper.insert(loginUser);
        return encodePassword;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUser loginUser = loginUserMapper.selectOne(new QueryWrapper<LoginUser>().eq("username", username));
        if (loginUser == null) {
            throw new UsernameNotFoundException("not find this account");
        }
        return loginUser;
    }
}
