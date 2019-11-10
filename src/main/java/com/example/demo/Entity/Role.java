package com.example.demo.Entity;

public enum Role {
    ADMIN("ROLE_admin"),
    USER("ROLE_user"),
    BANNED("ROLE_banned"),
    //ALL仅用于查询全部
    BLACKLIST("ROLE_banned"),
    ALL("ROLE_ALL");
    private String role;
    Role(String role){this.role=role;}
    public String getRole(){
        return this.role;
    }
}
