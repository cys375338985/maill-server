package com.mmail.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmail.common.Const;
import com.mmail.common.ResponseCode;
import com.mmail.common.ServerResponse;
import com.mmail.pojo.User;
import com.mmail.service.ICartService;
import com.mmail.service.IOrderService;
import org.omg.IOP.IOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by cys on 2018/7/2.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {
    private  static Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService orderService;

    @RequestMapping(value = "create.do")
    @ResponseBody
    public ServerResponse<Object> create(HttpSession session, Integer shippingId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return  orderService.createOrder(user.getId(),shippingId);
    }
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse<Object> cancel(HttpSession session, Long orderNo){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return  orderService.cancelOrder(user.getId(),orderNo);
    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse<Object> getOrderCartProduct(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return  orderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse deteil(HttpSession session,Long orderNo){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return  orderService.deteil(user.getId(),orderNo);
    }


    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse deteil(HttpSession session,
                                 @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return  orderService.list(user.getId(),pageNum,pageSize);
    }










    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return  orderService.pay(user.getId(),path,orderNo);
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> map = Maps.newHashMap();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = ((i == values.length - 1) ? valueStr + values[i] : valueStr + (values[i] + ","));
                map.put(key, valueStr);
            }
        }
        log.info("支付宝回调,签名:{},交易状态:{},参数"
                , parameterMap.get("sign"), parameterMap.get("trade_status")
                , parameterMap.toString());
        parameterMap.remove("sign_type");
        boolean alipayRSACheckv2 = false;
        try {
            alipayRSACheckv2 = AlipaySignature.rsaCheckV2(map,
                    Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckv2) {
                return ServerResponse.createByErrorMessage("错误的回调");
            }
        } catch (AlipayApiException e) {

            log.error("支付宝验证回调异常");
            e.printStackTrace();
        }
        //TODO
        ServerResponse serverResponse = orderService.aliCallBack(map);

        if (serverResponse.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;

    }

    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return  orderService.queryOrderPayStatus(user.getId(),orderNo);
    }

}
