package com.mmail.service;

import com.github.pagehelper.PageInfo;
import com.mmail.common.ServerResponse;
import com.mmail.vo.OrderVo;

import java.util.Map;

/**
 * Created by cys on 2018/7/2.
 */
public interface IOrderService {
    ServerResponse pay(Integer userId, String path, Long orderNo);

    ServerResponse aliCallBack(Map<String, String> map);


    ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse<Object> createOrder(Integer userId, Integer shippingId);

    ServerResponse<Object> cancelOrder(Integer userId, Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse deteil(Integer userId, Long orderNo);

    ServerResponse<PageInfo> list(Integer userid, int pageNum, int pageSize);

    ServerResponse<PageInfo> managelist(int pageNum, int pageSize);

    ServerResponse<OrderVo> manageDetail(long orderNo);


    ServerResponse<PageInfo> manageSerach(long orderNo, int pageNum, int pageSize);

    ServerResponse<String> manageSendGoods(long orderNo);
}
