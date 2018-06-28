package com.mmail.service;

import com.mmail.common.ServerResponse;
import com.mmail.pojo.User;
import com.mmail.vo.Cartvo;

/**
 * Created by cys on 2018/6/18.
 */
public interface ICartService {
    ServerResponse addCart(User user, Integer productId, Integer count);

    ServerResponse<Cartvo> updataCart(User user, Integer productId, Integer count);

    ServerResponse<Cartvo> dalectProduct(User user, String productids);

    ServerResponse<Cartvo> getList(User user);

    ServerResponse<Cartvo> selectOrUnselect(User user,Integer checked,Integer productId);

    ServerResponse<Integer> getCartCount(User user);
}
