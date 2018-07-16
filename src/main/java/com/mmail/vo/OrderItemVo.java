package com.mmail.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by cys on 2018/7/15.
 */
@Data
public class OrderItemVo {
    private Long orderNo;
    private Integer productId;
    private String productName;
    private BigDecimal currentUnitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Date createTime;
}
