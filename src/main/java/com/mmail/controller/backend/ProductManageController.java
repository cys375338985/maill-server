package com.mmail.controller.backend;

import com.google.common.collect.Maps;
import com.mmail.common.Const;
import com.mmail.common.ResponseCode;
import com.mmail.common.ServerResponse;
import com.mmail.pojo.Product;
import com.mmail.pojo.User;
import com.mmail.service.IFileService;
import com.mmail.service.IProductService;
import com.mmail.service.IUserService;
import com.mmail.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;

/**
 * Created by cys on 2018/5/22.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IProductService productService;
    @Autowired
    private IFileService fileService;
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
        ServerResponse serverResponse =   userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return  productService.saveOrUpdateProduct(product);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSales(HttpSession session,@RequestParam("productId") Integer productId,
                                   @RequestParam("status") Integer status){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
        ServerResponse serverResponse =   userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return  productService.setSaleStatus(productId,status);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }


    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session,@RequestParam("productId") Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
        ServerResponse serverResponse =   userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return  productService.manageProductDetail(productId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session,
                                  @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
        ServerResponse serverResponse =   userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return  productService.getProductList(pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSeach(HttpSession session,
                                   String productName,
                                   Integer productId,
                                   @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                   @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录");
        }
        ServerResponse serverResponse =   userService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return  productService.searchProduct(productName,productId,pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }
    }
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upLoad(@RequestParam(value = "upload_file",required = false) MultipartFile file
            , HttpServletRequest request){
       // User user = (User)request.getSession().getAttribute(Const.CURRENT_USER);
       // if(user==null ){
       //     return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
       //             "用户未登录，请登录");
       // }
       // ServerResponse serverResponse =   userService.checkAdminRole(user);
       // if(!serverResponse.isSuccess()){
       //     return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
       //             "你没有操作权限");
       // }
        String path = request.getSession().getServletContext().getRealPath("upload");
       String targerFileName = fileService.upLoad(file,path);
       String url =PropertiesUtil.getProperty("ftp.server.http.prefix",
               "http://img.happymmall.com/")+targerFileName;
        Map fileMap = Maps.newHashMap();
        fileMap.put("uri",targerFileName);
        fileMap.put("url",url);
        return ServerResponse.createBySuccess(fileMap);
    }

    @RequestMapping("roch_text_img_upload.do")
    @ResponseBody
    public Map rochtextImgUpload(@RequestParam(value = "upload_file",required = false) MultipartFile file
            , HttpServletRequest request, HttpServletResponse response){
       Map<String,Object> resultMap= Maps.newHashMap();
        User user = (User)request.getSession().getAttribute(Const.CURRENT_USER);
        if(user==null ){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        if(userService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targerFileName = fileService.upLoad(file,path);
            if (StringUtils.isBlank(targerFileName)){
                resultMap.put("succsess",false);
                resultMap.put("msg","上传失败");
            }
            String url =PropertiesUtil.getProperty("ftp.server.http.prefix",
                    "http://img.happymmall.com/")+targerFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
           resultMap.put("file_path",url);
           response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else {
            resultMap.put("sucess",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }

}
