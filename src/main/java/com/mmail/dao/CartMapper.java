package com.mmail.dao;

import com.mmail.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCatByUserIdProductId(@Param("userid") Integer userid, @Param("productId") Integer productId);

    List<Cart> selectCartByUserId(@Param("userid")Integer userId);

    int selectCartProuductChenckedStatusByUserId(@Param("userid")Integer userId);

    int deleteByUserIdProductIds(@Param("userid")Integer userid,@Param("productIdList") List<String> productIdList);

    int checkedOrUmcheckedProduct(@Param("userid") Integer id, @Param("checked") Integer checked,@Param("productId") Integer productId);

    int selectCartProuductCount(@Param("userid")Integer id);

    List<Cart> selectCartByCheckdeCart(@Param("userId") Integer userId);
}