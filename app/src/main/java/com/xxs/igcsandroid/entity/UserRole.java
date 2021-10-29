package com.xxs.igcsandroid.entity;

public class UserRole {
    public static final String ROLE_SYS_ADMIN = "系统管理员";
    public static final String ROLE_PARK_ADMIN = "园区管理员";
    public static final String ROLE_PARK_USER = "园区工作人员";

    public static String getParent(String curRole) {
        if (curRole.equals(ROLE_PARK_ADMIN)) {
            return ROLE_SYS_ADMIN;
        } else if (curRole.equals(ROLE_PARK_USER)) {
            return ROLE_PARK_ADMIN;
        }
        return "";
    }

    public static String getChild(String curRole) {
        if (curRole.equals(ROLE_SYS_ADMIN)) {
            return ROLE_PARK_ADMIN;
        } else if (curRole.equals(ROLE_PARK_ADMIN)) {
            return ROLE_PARK_USER;
        }
        return "";
    }
}
