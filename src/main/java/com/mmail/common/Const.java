package com.mmail.common;

/**
 * Created by cys on 2018/4/27.
 */
public class Const {
    public static final String CURRENT_USER = "current_user";
    public static final String CHECKVAID_TYPE_USERNAME = "username";
    public static final String CHECKVAID_TYPE_EMAIL = "email";

    public interface Role {
        int ROLECUTOMER = 0; //普通用户；
        int ADMIN = 1; //普通用户；
    }
}
