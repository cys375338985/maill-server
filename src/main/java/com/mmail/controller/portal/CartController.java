package com.mmail.controller.portal;

import com.mmail.common.Const;
import com.mmail.common.ResponseCode;
import com.mmail.common.ServerResponse;
import com.mmail.pojo.User;
import com.mmail.service.ICartService;
import com.mmail.vo.Cartvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by cys on 2018/6/18.
 */
@Controller
@RequestMapping("/cart")
public class CartController {
   @Autowired
   private  ICartService cartService ;

   @RequestMapping("/add.do")
   @ResponseBody
   public ServerResponse<Cartvo> add(HttpSession session, Integer productId, Integer count){
       if(productId==null || count == null){
           ResponseCode responseCode = ResponseCode.ILLEGAL_ARGUMENT;
           return ServerResponse.createByErrorCodeMessage(responseCode.getCode()
                   ,responseCode.getDesc());
       }
       User user= (User) session.getAttribute(Const.CURRENT_USER);
       if(user == null){
           return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                   ,ResponseCode.NEED_LOGIN.getDesc());
       }

       return cartService.addCart(user,productId,count);

   }
    @RequestMapping("/updata.do")
    @ResponseBody
    public ServerResponse<Cartvo> updata(HttpSession session, Integer productId, Integer count){
        if(productId==null || count == null){
            ResponseCode responseCode = ResponseCode.ILLEGAL_ARGUMENT;
            return ServerResponse.createByErrorCodeMessage(responseCode.getCode()
                    ,responseCode.getDesc());
        }
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }

        return cartService.updataCart(user,productId,count);

    }
    @RequestMapping("/delect_product.do")
    @ResponseBody
    public  ServerResponse<Cartvo> deleteProduct(HttpSession session,String productids){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.dalectProduct(user,productids);
    }

    @RequestMapping("/list.do")
    @ResponseBody
    public  ServerResponse<Cartvo> list(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.getList(user);
    }

    @RequestMapping("/select_all.do")
    @ResponseBody
    public  ServerResponse<Cartvo> selectAll(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.selectOrUnselect(user,Const.Cart.CHECKED,null);
    }

    @RequestMapping("/un_select_all.do")
    @ResponseBody
    public  ServerResponse<Cartvo> unSelectAll(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.selectOrUnselect(user,Const.Cart.UN_CHECKED,null);
    }

    @RequestMapping("/un_select.do")
    @ResponseBody
    public  ServerResponse<Cartvo> unSelect(HttpSession session,Integer productId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.selectOrUnselect(user,Const.Cart.UN_CHECKED,productId);
    }

    @RequestMapping("/select.do")
    @ResponseBody
    public  ServerResponse<Cartvo> Select(HttpSession session,Integer productId){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode()
                    ,ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.selectOrUnselect(user,Const.Cart.CHECKED,productId);
    }

    @RequestMapping("/get_cart_count.do")
    @ResponseBody
    public  ServerResponse<Integer> getCartCount(HttpSession session){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createBySuccess(0);
        }
        return cartService.getCartCount(user);
    }


}
