package com.mmail.vo;

import lombok.*;
import org.omg.PortableInterceptor.INACTIVE;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by cys on 2018/7/15.
 */
@Data
public class OrderVo {

        private Long orderNo;
        private BigDecimal payment;
        private Integer paymentType;
        private String paymentTime;
        private String paymentTypeDesc;

        private Integer postage;
        private Integer status;
        private String statusDesc;
        private String sendTime;
        private String endTime;
        private String closeTime;
        private String createTime;

        private List<OrderItemVo> orderItemVoList;
        private  String imgaeHost;
        private Integer shippingId;
        private  String receiverName;
        private ShippingVo shippingVo;

}
