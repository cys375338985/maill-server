package com.mmail.service;

import com.mmail.common.ServerResponse;
import com.mmail.pojo.User;

public interface IUserService {
    public ServerResponse<User> login(String username,String password);

    ServerResponse<String> regiser(User user);

    ServerResponse<String> checkVaid(String str, String type);

    ServerResponse<String> forgetGetQuestion(String username);

    ServerResponse<String> forgetCheckAnswer(String username, String question, String answer);

    ServerResponse<String> updatePassword(String username, String password, String token);

    ServerResponse<String> restPassword(String password, String passwordNew, User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer id);

    ServerResponse checkAdminRole(User user);
}
