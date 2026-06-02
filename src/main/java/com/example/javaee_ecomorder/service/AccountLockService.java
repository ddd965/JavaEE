package com.example.javaee_ecomorder.service;

import com.example.javaee_ecomorder.common.CacheKeyPrefix;
import com.example.javaee_ecomorder.entity.User;
import com.example.javaee_ecomorder.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AccountLockService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean isLockedInRedis(String username) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(CacheKeyPrefix.LOCK + username));
    }

    /**
     * Redis 锁已过期时，同步恢复数据库锁定状态（登录前兜底）
     */
    public void releaseExpiredLockIfNeeded(String username) {
        if (!StringUtils.hasText(username) || isLockedInRedis(username)) {
            return;
        }
        syncDatabaseUnlock(username);
    }

    /**
     * Redis 锁 key 过期回调，同步恢复数据库
     */
    public void onLockKeyExpired(String username) {
        if (!StringUtils.hasText(username)) {
            return;
        }
        syncDatabaseUnlock(username);
    }

    private void syncDatabaseUnlock(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return;
        }
        if (user.getAccountNonLocked() != null && !user.getAccountNonLocked()) {
            userMapper.updateAccountStatus(user.getId(), true, 0);
        }
    }
}
