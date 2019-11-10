package com.example.demo.Entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoginUser implements UserDetails {
    @TableId(value ="id",type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String role;
    private Boolean enable;
    private String nickname;
    private String photoPath;
    @NotBlank(message = "student number is blank")
    private String studentNumber;
    @NotBlank(message = "sex is empty")
    @Pattern(regexp = "M|F",message = "wrong sex,please choose M or F")
    private String sex;
    @NotBlank(message = "last name is blank")
    private String lastName;
    @NotBlank(message = "major is blank")
    private String major;
    private String banReason;

    //适配器原理
    public interface loginUser{}

    public loginUser show(){
        if (role.equals("ROLE_banned")) {
            return new LoginUserBan(this);
        }
        return  new LoginUserAuth(this);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }

    @Data
    private class LoginUserAuth implements loginUser {
        private Integer id;
        private String nickname;
        private String username;
        private String role;
        private Boolean enable;
        private String studentNumber;
        private String sex;
        private String lastName;
        private String major;
        private String photoPath;
        private LoginUserAuth(LoginUser loginUser){
            this.photoPath=loginUser.photoPath;
            this.id=loginUser.id;
            this.username=loginUser.username;
            this.role=loginUser.role;
            this.studentNumber=loginUser.studentNumber;
            this.lastName=loginUser.lastName;
            this.major= loginUser.major;
            this.sex= loginUser.sex;
            this.nickname=loginUser.nickname;
        }
    }
    @Data
    private class LoginUserBan implements loginUser{
        private Integer id;
        private String nickname;
        private String username;
        private Boolean enable;
        private String banReason;
        private String role;
        private String photoPath;
        private LoginUserBan(LoginUser loginUser){
            photoPath= loginUser.photoPath;
            id= loginUser.id;
            username= loginUser.username;
            enable=loginUser.enable;
            banReason= loginUser.banReason;
            role=loginUser.role;
            nickname=loginUser.nickname;
        }
    }
}
