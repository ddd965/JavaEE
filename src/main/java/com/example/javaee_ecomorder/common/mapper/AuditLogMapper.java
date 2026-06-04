package com.example.javaee_ecomorder.common.mapper;

import com.example.javaee_ecomorder.common.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper {
    int insert(AuditLog log);
}
