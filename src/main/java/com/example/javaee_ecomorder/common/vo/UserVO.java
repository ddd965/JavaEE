package com.example.javaee_ecomorder.common.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private Date createTime;
    private String realName;
    private String address;
    private Integer points;
}
