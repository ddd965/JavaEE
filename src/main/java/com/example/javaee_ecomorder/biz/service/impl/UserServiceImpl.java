package com.example.javaee_ecomorder.biz.service.impl;

import com.example.javaee_ecomorder.common.basic.CacheKeyPrefix;
import com.example.javaee_ecomorder.common.dto.UserQueryDTO;
import com.example.javaee_ecomorder.common.dto.UserRegisterDTO;
import com.example.javaee_ecomorder.common.dto.UserUpdateDTO;
import com.example.javaee_ecomorder.common.entity.LoginLog;
import com.example.javaee_ecomorder.common.entity.User;
import com.example.javaee_ecomorder.common.entity.UserProfile;
import com.example.javaee_ecomorder.common.exception.BusinessException;
import com.example.javaee_ecomorder.common.mapper.LoginLogMapper;
import com.example.javaee_ecomorder.common.mapper.UserMapper;
import com.example.javaee_ecomorder.common.mapper.UserProfileMapper;
import com.example.javaee_ecomorder.admin.security.EcomPasswordEncoder;
import com.example.javaee_ecomorder.biz.service.UserService;
import com.example.javaee_ecomorder.common.utils.PageResult;
import com.example.javaee_ecomorder.common.vo.OrderVO;
import com.example.javaee_ecomorder.common.vo.UserListVO;
import com.example.javaee_ecomorder.common.vo.UserProfileVO;
import com.example.javaee_ecomorder.common.vo.UserWithOrdersVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProfileMapper profileMapper;
    @Autowired
    private LoginLogMapper loginLogMapper;
    @Autowired
    private EcomPasswordEncoder passwordEncoder;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${security.login-failure-lock-duration:180}")
    private long lockDurationSeconds;

    @Override
    public UserProfileVO getUserWithProfile(Long userId) {
        UserProfileVO vo = userMapper.selectUserWithProfile(userId);
        if (vo == null) {
            throw new BusinessException("用户不存�?);
        }
        return vo;
    }

    @Override
    public List<OrderVO> getUserOrders(Long userId) {
        UserWithOrdersVO vo = userMapper.selectUserWithOrders(userId);
        if (vo == null) {
            throw new BusinessException("用户不存�?);
        }
        List<OrderVO> orders = vo.getOrders();
        if (orders == null || orders.isEmpty()) {
            return Collections.emptyList();
        }
        return orders.stream()
                .filter(o -> o != null && o.getOrderId() != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUser(UserUpdateDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        UserProfileVO existing = userMapper.selectUserWithProfile(dto.getId());
        if (existing == null) {
            throw new BusinessException("用户不存�?);
        }
        if (!Objects.equals(existing.getUsername(), dto.getUsername())
                && userMapper.countByUsernameExcludeId(dto.getUsername(), dto.getId()) > 0) {
            throw new BusinessException("用户名已被占�?);
        }
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        userMapper.updateUser(user);

        UserProfile profile = profileMapper.selectByUserId(dto.getId());
        if (profile == null) {
            throw new BusinessException("用户资料不存�?);
        }
        profile.setRealName(dto.getRealName());
        profile.setAddress(dto.getAddress());
        profileMapper.update(profile);
    }

    @Override
    public PageResult<UserListVO> pageQuery(UserQueryDTO query) {
        int pageNum = query.getPageNum() != null && query.getPageNum() > 0 ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null && query.getPageSize() > 0 ? query.getPageSize() : 10;
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        long total = userMapper.countUserByQuery(query);
        if (total == 0) {
            return new PageResult<>(0L, Collections.emptyList());
        }
        int offset = (pageNum - 1) * pageSize;
        List<UserListVO> records = userMapper.selectUserPage(query, offset);
        return new PageResult<>(total, records);
    }

    @Override
    @Transactional
    public void register(UserRegisterDTO dto) {
        if (userMapper.countByUsername(dto.getUsername()) > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreateTime(new Date());
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setFailCount(0);
        userMapper.insert(user);
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setPoints(0);
        profileMapper.insert(profile);
    }

    @Override
    public void updatePassword(Long userId, String oldPwd, String newPwd) {
        User user = userMapper.selectByUsername(findUsernameById(userId));
        if (user == null) {
            throw new BusinessException("用户不存�?);
        }
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            throw new BusinessException("原密码错�?);
        }
        userMapper.updatePassword(userId, passwordEncoder.encode(newPwd));
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        if (userMapper.selectUserWithProfile(userId) == null) {
            throw new BusinessException("用户不存�?);
        }
        userMapper.updatePassword(userId, passwordEncoder.encode(newPassword));
    }

    @Override
    public void lockAccount(Long userId) {
        UserProfileVO profile = userMapper.selectUserWithProfile(userId);
        if (profile == null) {
            throw new BusinessException("用户不存�?);
        }
        userMapper.updateAccountStatus(userId, false, 5);
        stringRedisTemplate.opsForValue().set(CacheKeyPrefix.LOCK + profile.getUsername(), "1",
                lockDurationSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void unlockAccount(Long userId) {
        UserProfileVO profile = userMapper.selectUserWithProfile(userId);
        if (profile == null) {
            throw new BusinessException("用户不存�?);
        }
        userMapper.updateAccountStatus(userId, true, 0);
        stringRedisTemplate.delete(CacheKeyPrefix.LOCK + profile.getUsername());
    }

    @Override
    public List<LoginLog> getLoginLogs(Long userId) {
        return loginLogMapper.selectByUserId(userId);
    }

    private String findUsernameById(Long userId) {
        UserProfileVO vo = userMapper.selectUserWithProfile(userId);
        if (vo == null) {
            throw new BusinessException("用户不存�?);
        }
        return vo.getUsername();
    }
}
