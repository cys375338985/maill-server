package com.mmail.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmail.common.ServerResponse;
import com.mmail.dao.ShippingMapper;
import com.mmail.pojo.Shipping;
import com.mmail.pojo.User;
import com.mmail.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by cys on 2018/6/29.
 */
@Service
public class ShippingService implements IShippingService {
    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(User user, Shipping shipping) {
        Integer id= user.getId();
        shipping.setUserId(user.getId());
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount> 0 ){
            Map resut = Maps.newHashMap();
            resut.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("地址创建成功",resut);
        }
        return ServerResponse.createByErrorMessage("地址创建失败");
    }

    @Override
    public ServerResponse del(User user, Integer shippingId) {
        int resulCount = shippingMapper.deleteByshippingIdAnduserId(user.getId(),shippingId);
        if(resulCount > 0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    @Override
    public ServerResponse update(User user, Shipping shipping) {
        shipping.setUserId(user.getId());
        int resulCount = shippingMapper.updateByshippingIdAnduserId(shipping);
        if(resulCount > 0){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createBySuccess("更新地址失败");
    }

    @Override
    public ServerResponse<Shipping> select(User user, Integer shippingId) {

        Shipping shipping = shippingMapper.selectByshippingIdAnduserId(user.getId(),shippingId);
        if(shipping != null){
            return ServerResponse.createBySuccess("查询地址成功",shipping);
        }
        return ServerResponse.createByErrorMessage("查询地址失败");
    }

    @Override
    public ServerResponse<PageInfo> list(User user, int pageNum, int pageSize) {
         PageHelper.startPage(pageNum,pageSize);
        List<Shipping> list  =  shippingMapper.selectByUserId(user.getId());
        PageInfo pageInfo = new PageInfo(list);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
