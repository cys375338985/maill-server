package com.mmail.common;

import com.google.common.collect.Sets;

import java.util.Set;

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

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc","price_desc");
    }

    public enum  ProductStatus{
        ON_SALE(1,"在线");
        private int code ;
         private String value;

        ProductStatus(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }
}
