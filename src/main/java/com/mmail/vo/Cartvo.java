package com.mmail.vo;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Formatter;
import java.util.List;

/**
 * Created by cys on 2018/6/18.
 */
public class Cartvo {
    private List<CartProductVo> cartProductVoList;
    private BigDecimal cartToalPrice;
    private  Boolean allChecked;
    private  String imageHost;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartToalPrice() {
        return cartToalPrice;
    }

    public void setCartToalPrice(BigDecimal cartToalPrice) {
        this.cartToalPrice = cartToalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}

