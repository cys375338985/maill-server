package com.mmail.service.impl;

import com.mmail.common.Const;
import com.mmail.common.ServerResponse;
import com.mmail.common.TokenCache;
import com.mmail.dao.UserMapper;
import com.mmail.pojo.User;
import com.mmail.service.IUserService;
import com.mmail.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;

import java.util.UUID;

@Service("userService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
       // userMapper.select
        int resultCunt = userMapper.checkUserName(username);
        if(resultCunt == 0 ){

            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //密码登录MD5
        password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,password);
        if(user==null){
            return ServerResponse.createByErrorMessage("用户名或密码错误");
        }
        user.setPassword(StringUtils.EMPTY);

        return   ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse<String> regiser(User user) {
      int resultCunt =   userMapper.checkUserName(user.getUsername());
      if(resultCunt > 0 ) {
          return ServerResponse.createByErrorMessage("用户名已存在");
      }
      resultCunt = userMapper.checkEmail(user.getEmail());
        if(resultCunt > 0 ) {
            return ServerResponse.createByErrorMessage("Email已存在");
        }
        user.setRole(Const.Role.ROLECUTOMER);
        user.setPassword( MD5Util.MD5EncodeUtf8(user.getPassword()));
       resultCunt =  userMapper.insert(user);
        if(resultCunt== 0 ){
            return ServerResponse.createByErrorMessage("注册失败");

        }
        return ServerResponse.createBySuccess("注册成功");
    }

    @Override
    public ServerResponse<String> checkVaid(String str, String type) {
        if(StringUtils.isNotBlank(type)){
            if(Const.CHECKVAID_TYPE_USERNAME.equals(type)){

                int  resultCount= userMapper.checkUserName(str);
                if(resultCount> 0 ){
                    return ServerResponse.createBySuccess("用户名已存在");
                }
            }
            if(Const.CHECKVAID_TYPE_EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount> 0 ){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }

        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }

        return ServerResponse.createBySuccessMessage("校验成功");
    }


    @Override
    public ServerResponse<String> forgetGetQuestion(String username) {
       ServerResponse validResponse =  checkVaid(username,Const.CHECKVAID_TYPE_USERNAME);
        if (!validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectUserQuestionByUserName(username);

        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码不存在");
    }


    @Override
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        int resutlCount = userMapper.checkAnswer(username,question,answer);
        if(resutlCount > 0 ){
            String token = UUID.randomUUID().toString();
            TokenCache.setCache("token_"+username,token);
            return  ServerResponse.createBySuccess(token);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    @Override
    public ServerResponse<String> updatePassword(String username, String password, String token) {
        if(!StringUtils.isNotBlank(token)){
             return ServerResponse.createByErrorMessage("参数错误，没有token");
        }

        ServerResponse validResponse =   checkVaid(username,Const.CHECKVAID_TYPE_USERNAME);
        if (!validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String tokens = TokenCache.getKey("token_"+username);
        if(!StringUtils.isNotBlank(tokens)){
            return  ServerResponse.createByErrorMessage("服务以过期");
        }


        if(!tokens.equals(token)){
            return  ServerResponse.createByErrorMessage("令牌以错误");
        }else {
            password = MD5Util.MD5EncodeUtf8(password);
           int resultCount =   userMapper.updatePasswordByUserName(username,password);
            if(resultCount>0){
                return ServerResponse.createBySuccessMessage("密码已修改");
            }
       }
        return ServerResponse.createByErrorMessage("密码修改错误");
    }

    @Override
    public ServerResponse<String> restPassword(String password, String passwordNew, User user) {

        password = MD5Util.MD5EncodeUtf8(password);

        passwordNew = MD5Util.MD5EncodeUtf8(passwordNew);
        int reslt  = userMapper.checkPassword(user.getId(),password);
        if(reslt>0){
            user.setPassword(passwordNew);

            reslt =  userMapper.updateByPrimaryKey(user);
            if(reslt >0 ){
                return ServerResponse.createBySuccessMessage("密码修改成功");
            }else {
                return  ServerResponse.createByErrorMessage("密码修改失败");
            }

        }else {
            return  ServerResponse.createByErrorMessage("旧密码错误");
        }

    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
       int restCount  = userMapper.checkEmailById(user.getId(),user.getEmail());
       if(restCount>0){

           return  ServerResponse.createByErrorMessage("Email 已经使用,请更换Email");
       }

       User upuser = new User();
        upuser.setId(user.getId());
        upuser.setEmail(user.getEmail());
        upuser.setPhone(user.getPhone());
        upuser.setQuestion(user.getQuestion());
        upuser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(upuser);
        if(updateCount>0){
            return  ServerResponse.createBySuccess("更新个人成功",upuser);

        }else {
            return  ServerResponse.createByErrorMessage("更新个人失败");

        }

    }

    @Override
    public ServerResponse<User> getInformation(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if(user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        if(user!= null && user.getRole().intValue() != Const.Role.ADMIN){
            return  ServerResponse.createByError();
        }
        return ServerResponse.createBySuccess();
    }
}
