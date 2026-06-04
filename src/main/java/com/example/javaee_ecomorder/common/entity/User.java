package com.example.javaee_ecomorder.common.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Date createTime;
    private Boolean enabled;
    private Boolean accountNonLocked;
    private Integer failCount;
    private Date lastLoginTime;
}
