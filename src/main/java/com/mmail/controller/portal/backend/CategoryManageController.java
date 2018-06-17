package com.mmail.controller.portal.backend;

import com.mmail.common.Const;
import com.mmail.common.ResponseCode;
import com.mmail.common.ServerResponse;
import com.mmail.pojo.User;
import com.mmail.service.ICategoryService;
import com.mmail.service.IUserService;
import com.mmail.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by cys on 2018/5/21.
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService userService;
    @Autowired
    private ICategoryService categoryService;
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName,
                                      @RequestParam(value ="parenId", defaultValue = "0") int parenId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
       ServerResponse serverResponse =   userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            categoryService.addCategory(categoryName,parenId);
        }else {

            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
        return null;
    }
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCatgoryName(HttpSession session,@RequestParam(value = "categoryId") Integer catid ,
                                         @RequestParam(value = "categoryName") String catname){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
        ServerResponse serverResponse =   userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return   categoryService.updateCategoryName(catid,catname);
        }else {

            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }

    }
    @RequestMapping("get_category.do")
    @ResponseBody
    public  ServerResponse getChildernParalleCategory(HttpSession session ,
                                                      @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
        ServerResponse serverResponse =   userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return  categoryService.getChildernParllelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }

    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public  ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session ,
                                                      @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
        ServerResponse serverResponse =   userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return  categoryService.getCategoryAndDeepChildrenCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }

    }


}
