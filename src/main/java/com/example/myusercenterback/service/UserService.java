package com.example.myusercenterback.service;

import com.example.myusercenterback.common.BaseResponse;
import com.example.myusercenterback.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author LuckyME
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-06-11 09:48:19
*/
public interface UserService extends IService<User> {

	//测试foreach 插入方法
	void testForeachInsert(List<User> users);

	//测试foreach in
	List<User> testForeachIn(List<Long> ids);

	/**
	 *用户注册
	 */
	long UserRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

	/**
	 * 用户登录
	 */
	User UserLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafetyUser(User originalUser);

    //根据tag搜索用户
//	List<User> getUsersByTags(List<String> tagsNameList);

	void testForeachUpdate(List<User> userList);

    User getLoginUser(HttpServletRequest request);

	/**
	 * 是否是管理员
	 * @param request
	 * @return
	 */
	boolean isAdmin(HttpServletRequest request);
	boolean isAdmin(User loginUser);

	/**
	 * 根据tags匹配用户
	 * @param num
	 * @param loginUser
	 * @return
	 */

	List<User> matchUsers(int num, User loginUser);
}
