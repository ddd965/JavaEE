package com.example.javaee_ecomorder.context;

public final class UserContext {

    private static final ThreadLocal<UserInfo> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setUser(UserInfo user) {
        CURRENT_USER.set(user);
    }

    public static UserInfo getUser() {
        return CURRENT_USER.get();
    }

    public static Long getCurrentUserId() {
        UserInfo user = CURRENT_USER.get();
        return user != null ? user.getUserId() : null;
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
