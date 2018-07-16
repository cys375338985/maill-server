package com.mmail.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmail.common.Const;
import com.mmail.common.ResponseCode;
import com.mmail.common.ServerResponse;
import com.mmail.pojo.User;
import com.mmail.service.IOrderService;
import com.mmail.service.IUserService;
import com.mmail.service.impl.OrderServiceImpl;
import com.mmail.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by cys on 2018/7/16.
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {
    @Autowired
    private   IOrderService orderService;
    @Autowired
    private IUserService userService;

    @RequestMapping("/list")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session,
                                              @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
        ServerResponse serverResponse = userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return   orderService.managelist(pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }


    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session,long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
        ServerResponse serverResponse = userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return   orderService.manageDetail(orderNo);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("/search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpSession session,long orderNo,
                                                @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
                                                ){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
        ServerResponse serverResponse = userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return   orderService.manageSerach(orderNo,pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("/send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session,long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
        ServerResponse serverResponse = userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return   orderService.manageSendGoods(orderNo);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }

}
