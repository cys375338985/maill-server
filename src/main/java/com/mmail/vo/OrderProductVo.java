package com.mmail.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by cys on 2018/7/15.
 */
@Data
public class OrderProductVo {
    private List<OrderItemVo> orderItemVoList;
    private BigDecimal productTotalPrice;
    private String imageHost;




}
