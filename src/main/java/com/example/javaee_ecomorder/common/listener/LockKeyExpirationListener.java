package com.example.javaee_ecomorder.common.listener;

import com.example.javaee_ecomorder.common.basic.CacheKeyPrefix;
import com.example.javaee_ecomorder.admin.service.AccountLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(AccountLockService.class)
public class LockKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Autowired
    private AccountLockService accountLockService;

    public LockKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if (!expiredKey.startsWith(CacheKeyPrefix.LOCK)) {
            return;
        }
        String username = expiredKey.substring(CacheKeyPrefix.LOCK.length());
        accountLockService.onLockKeyExpired(username);
    }
}
