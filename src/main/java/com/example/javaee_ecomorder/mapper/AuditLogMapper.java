package com.example.javaee_ecomorder.mapper;

import com.example.javaee_ecomorder.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper {
    int insert(AuditLog log);
}
