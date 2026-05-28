package com.example.javaee_ecomorder.security;

import com.example.javaee_ecomorder.entity.Role;
import com.example.javaee_ecomorder.entity.User;
import com.example.javaee_ecomorder.mapper.PermissionMapper;
import com.example.javaee_ecomorder.mapper.RoleMapper;
import com.example.javaee_ecomorder.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.javaee_ecomorder.common.CacheKeyPrefix;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(CacheKeyPrefix.LOCK + username))) {
            throw new UsernameNotFoundException("账户已锁定");
        }
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        boolean enabled = user.getEnabled() == null || user.getEnabled();
        boolean accountNonLocked = user.getAccountNonLocked() == null || user.getAccountNonLocked();
        List<String> authorities = buildAuthorities(user);
        return new SecurityUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                enabled,
                accountNonLocked,
                authorities
        );
    }

    private List<String> buildAuthorities(User user) {
        Set<String> set = new LinkedHashSet<>();
        List<Role> roles = roleMapper.selectByUserId(user.getId());
        if (roles != null) {
            roles.forEach(r -> set.add(r.getName()));
        }
        List<String> permissions = permissionMapper.selectPermissionNamesByUserId(user.getId());
        if (permissions != null) {
            set.addAll(permissions);
        }
        if (set.isEmpty()) {
            set.addAll(fallbackPermissions(user.getUsername()));
        }
        return new ArrayList<>(set);
    }

    private List<String> fallbackPermissions(String username) {
        if ("admin".equalsIgnoreCase(username)) {
            return List.of("ROLE_ADMIN", "product:delete", "product:update", "product:query", "order:manage");
        }
        return List.of("ROLE_USER", "product:query", "order:query");
    }
}
