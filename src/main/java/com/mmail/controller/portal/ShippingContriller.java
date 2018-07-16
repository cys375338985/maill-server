package com.mmail.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmail.common.Const;
import com.mmail.common.ResponseCode;
import com.mmail.common.ServerResponse;
import com.mmail.pojo.Shipping;
import com.mmail.pojo.User;
import com.mmail.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by cys on 2018/6/29.
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingContriller {
    @Autowired
    IShippingService shippingService;
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return  shippingService.add(user,shipping);
    }
    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Integer shippingId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return  shippingService.del(user,shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Shipping shipping){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return  shippingService.update(user,shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpSession session, Integer shippingId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return  shippingService.select(user,shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }

        return shippingService.list(user,pageNum,pageSize);
    }

}
