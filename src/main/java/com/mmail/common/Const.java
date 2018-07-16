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

        public interface  AlipayCallback{
            String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
            String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

            String RESPONSE_SUCCESS = "success";
            String RESPONSE_FAILED = "failed";
        }

    public interface Role {
        int ROLECUTOMER = 0; //普通用户；
        int ADMIN = 1; //普通用户；
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc","price_desc");

    }

    public interface  Cart{
        int CHECKED = 1;
        int UN_CHECKED= 0;
        String LIMIT_NUM_FAIL = "LIMT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";

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


    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");


        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code){
            for(OrderStatusEnum orderStatusEnum : values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");

        PayPlatformEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
    public  enum  PaymentTypeEnum{
          ONLINE_PAY(1,"在线支付");
        private  int code;
        private  String value;
        PaymentTypeEnum(int code,String value){
            this.code = code;
            this.value = value;

        }
        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeof(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()){
                if(paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;

                }

            }
            throw new RuntimeException("没有这样的状态");
        }
    }

}
