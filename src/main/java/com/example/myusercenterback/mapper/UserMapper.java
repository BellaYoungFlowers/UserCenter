package com.example.myusercenterback.mapper;

import com.example.myusercenterback.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author LuckyME
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2023-06-11 09:48:19
* @Entity com.example.myusercenterback.model.User
*/
public interface UserMapper extends BaseMapper<User> {

    void testForeachInsert(List<User> users);

    void testForeachUpdate(List<User> userList);

    List<User> testForeachIn(List<Long> ids);
}




