package com.mmail.controller.portal.backend;

import com.mmail.common.Const;
import com.mmail.common.ServerResponse;
import com.mmail.pojo.User;
import com.mmail.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by cys on 2018/5/3.
 */
@Controller
@RequestMapping("/manager/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String name, String pass, HttpSession session){
        ServerResponse<User> response =  iUserService.login(name,pass);
        if(response.isSuccess()){
            User user  = response.getDate();
            if(user.getRole() == Const.Role.ADMIN){
                session.setAttribute(Const.CURRENT_USER,response.getDate());
                return response;
            }else {

                return ServerResponse.createByErrorMessage("你不是管理员");

            }
        }
        return response;

    }
}
