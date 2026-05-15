package com.example.javaee_ecomorder.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import java.util.Date;

/**
 * 用户查询条件 DTO（分页 + 动态条件）
 */
@Data
public class UserQueryDTO {

    // ========== 分页参数 ==========
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;          // 当前页码，默认1

    @Min(value = 1, message = "每页条数最小为1")
    private Integer pageSize = 10;        // 每页条数，默认10

    // ========== 动态查询条件 ==========
    private String username;              // 用户名（模糊查询）
    private String phone;                 // 手机号（精确或模糊，根据需要）
    private String email;                 // 邮箱（模糊查询）

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;               // 注册开始时间

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;                 // 注册结束时间
}
