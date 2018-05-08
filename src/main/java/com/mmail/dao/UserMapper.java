package com.mmail.dao;

import com.mmail.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);

    User selectLogin(@Param("username")String username,@Param("password")String password);

    int checkEmail(String email);

    String selectUserQuestionByUserName(String username);

    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePasswordByUserName(@Param("username") String username, @Param("password") String password);


    int checkPassword(@Param("userid") Integer id, @Param("password") String password);

    int updatePasswordByID(@Param("userid") Integer id, @Param("passwordNew") String passwordNew);

    int checkEmailById(@Param("userid") Integer id, @Param("email") String email);
}