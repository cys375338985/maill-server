package com.mmail.service;

import com.github.pagehelper.PageInfo;
import com.mmail.common.ServerResponse;
import com.mmail.pojo.Shipping;
import com.mmail.pojo.User;

/**
 * Created by cys on 2018/6/29.
 */
public interface IShippingService {
    ServerResponse add(User user,Shipping shipping);

    ServerResponse del(User user, Integer shippingId);

    ServerResponse update(User user, Shipping shippingId);

    ServerResponse select(User user, Integer shippingId);

    ServerResponse<PageInfo> list(User user, int pageNum, int pageSize);
}
