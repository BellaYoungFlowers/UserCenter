package com.example.myusercenterback.service;

import com.example.myusercenterback.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author LuckyME
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-06-11 09:48:19
*/
public interface UserService extends IService<User> {
	/**
	 *用户注册
	 */
	long UserRegister(String userAccount,String userPassword,String checkPassword);

	/**
	 * 用户登录
	 */
	User UserLogin(String userAccount, String userPassword, HttpServletRequest httpServlet);

	//根据tag搜索用户
	List<User> getUsersByTags(List<String> tagsNameList);
}
