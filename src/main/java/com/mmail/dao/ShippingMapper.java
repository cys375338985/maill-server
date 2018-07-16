package com.mmail.dao;

import com.mmail.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByshippingIdAnduserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    int updateByshippingIdAnduserId(Shipping shipping);

    Shipping selectByshippingIdAnduserId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    List<Shipping> selectByUserId(Integer userid);
}