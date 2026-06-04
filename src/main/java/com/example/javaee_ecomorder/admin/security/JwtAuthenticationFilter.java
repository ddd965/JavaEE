package com.example.javaee_ecomorder.admin.security;

import com.example.javaee_ecomorder.common.basic.CacheKeyPrefix;
import com.example.javaee_ecomorder.common.config.EcomAopProperties;
import com.example.javaee_ecomorder.common.context.UserInfo;
import com.example.javaee_ecomorder.common.utils.JwtUtil;
import com.example.javaee_ecomorder.common.utils.RedisCacheUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private EcomAopProperties aopProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromToken(token);
            SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(username);
            Object cached = redisCacheUtil.get(CacheKeyPrefix.TOKEN + token);
            if (cached != null) {
                List<String> authorities = jwtUtil.getAuthoritiesFromToken(token);
                var granted = authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(securityUser, null, granted);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                UserInfo userInfo = toUserInfo(securityUser, authorities);
                request.setAttribute("currentUser", userInfo);
                request.setAttribute("userId", securityUser.getUserId());
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(aopProperties.getTokenHeader());
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    private UserInfo toUserInfo(SecurityUser user, List<String> authorities) {
        UserInfo info = new UserInfo();
        info.setUserId(user.getUserId());
        info.setUsername(user.getUsername());
        info.setPermissions(authorities.stream()
                .filter(a -> a.contains(":"))
                .toList());
        return info;
    }
}
