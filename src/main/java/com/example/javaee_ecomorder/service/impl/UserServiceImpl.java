package com.example.javaee_ecomorder.service.impl;

import com.example.javaee_ecomorder.common.CacheKeyPrefix;
import com.example.javaee_ecomorder.context.UserInfo;
import com.example.javaee_ecomorder.dto.UserQueryDTO;
import com.example.javaee_ecomorder.dto.UserRegisterDTO;
import com.example.javaee_ecomorder.dto.UserUpdateDTO;
import com.example.javaee_ecomorder.entity.User;
import com.example.javaee_ecomorder.entity.UserProfile;
import com.example.javaee_ecomorder.exception.BusinessException;
import com.example.javaee_ecomorder.mapper.UserMapper;
import com.example.javaee_ecomorder.mapper.UserProfileMapper;
import com.example.javaee_ecomorder.service.UserService;
import com.example.javaee_ecomorder.utils.EncryptUtil;
import com.example.javaee_ecomorder.utils.JwtUtil;
import com.example.javaee_ecomorder.utils.PageResult;
import com.example.javaee_ecomorder.utils.RedisCacheUtil;
import com.example.javaee_ecomorder.vo.LoginResultVO;
import com.example.javaee_ecomorder.vo.OrderVO;
import com.example.javaee_ecomorder.vo.UserListVO;
import com.example.javaee_ecomorder.vo.UserProfileVO;
import com.example.javaee_ecomorder.vo.UserWithOrdersVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
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
    private JwtUtil jwtUtil;
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Override
    public LoginResultVO login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException("用户名/密码不能为空");
        }
        User user = userMapper.selectByUsername(username);
        if (user == null || !user.getPassword().equals(EncryptUtil.md5(password))) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setPermissions(resolvePermissions(user.getUsername()));
        redisCacheUtil.set(CacheKeyPrefix.TOKEN + token, userInfo, jwtUtil.getExpireMillis(), TimeUnit.MILLISECONDS);
        long expireAt = System.currentTimeMillis() + jwtUtil.getExpireMillis();
        LoginResultVO result = new LoginResultVO();
        result.setUserId(user.getId());
        result.setUsername(user.getUsername());
        result.setToken(token);
        result.setExpireTime(expireAt);
        return result;
    }

    private List<String> resolvePermissions(String username) {
        if ("admin".equalsIgnoreCase(username)) {
            return Arrays.asList("product:delete", "product:update", "product:query", "order:manage");
        }
        return Arrays.asList("product:query", "order:query");
    }

    @Override
    public UserProfileVO getUserWithProfile(Long userId) {
        UserProfileVO vo = userMapper.selectUserWithProfile(userId);
        if (vo == null) {
            throw new BusinessException("用户不存在");
        }
        return vo;
    }

    @Override
    public List<OrderVO> getUserOrders(Long userId) {
        UserWithOrdersVO vo = userMapper.selectUserWithOrders(userId);
        if (vo == null) {
            throw new BusinessException("用户不存在");
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
            throw new BusinessException("用户不存在");
        }
        if (!Objects.equals(existing.getUsername(), dto.getUsername())
                && userMapper.countByUsernameExcludeId(dto.getUsername(), dto.getId()) > 0) {
            throw new BusinessException("用户名已被占用");
        }
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        userMapper.updateUser(user);

        UserProfile profile = profileMapper.selectByUserId(dto.getId());
        if (profile == null) {
            throw new BusinessException("用户资料不存在");
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
        user.setPassword(EncryptUtil.md5(dto.getPassword()));
        user.setCreateTime(new Date());
        userMapper.insert(user);
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setPoints(0);
        profileMapper.insert(profile);
    }
}
