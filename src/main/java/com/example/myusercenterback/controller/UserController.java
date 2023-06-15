package com.example.myusercenterback.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myusercenterback.model.User;
import com.example.myusercenterback.model.request.UserLoginRequest;
import com.example.myusercenterback.model.request.UserRegisterRequest;
import com.example.myusercenterback.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.myusercenterback.constant.UserConstant.ADMIN_ROLE;
import static com.example.myusercenterback.constant.UserConstant.USER_LOGIN_STATE;

/**
 * ClassName: UserController
 * Package: com.example.myusercenterback.controller
 * Description:
 *
 * @Author 王腾腾
 * @Create 2023/6/11 16:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {
	@Resource
	private UserService userService;

	@PostMapping("/userRegistration")
	public Long userRegistration(@RequestBody  UserRegisterRequest userRegisterRequest){
		if(userRegisterRequest == null){
			return  null;
		}
		String userAccount = userRegisterRequest.getUserAccount();
		String userPassword = userRegisterRequest.getUserPassword();
		String checkPassword = userRegisterRequest.getCheckPassword();
		if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
			return null;
		}

		return  userService.UserRegister(userAccount, userPassword, checkPassword);
	}

	@PostMapping("/userLogin")
	public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
		if(userLoginRequest == null){
			return  null;
		}
		String userAccount = userLoginRequest.getUserAccount();
		String userPassword = userLoginRequest.getUserPassword();
		if(StringUtils.isAnyBlank(userAccount,userPassword)){
			return null;
		}

		return  userService.UserLogin(userAccount, userPassword,request);
	}

	//管理员根据名字查询
	@GetMapping("/search")
	List<User> searchByName(@RequestParam("username") String username,HttpServletRequest request){
		boolean admin = isAdmin(request);
		if(!admin){
			return new ArrayList<>();
		}

		if(StringUtils.isBlank(username)){
			return new ArrayList<>();
		}

		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("username", username);
		//脱敏
		return userService.list(queryWrapper)
				.stream()
				.map(user -> userService.getSafetyUser(user))
				.collect(Collectors.toList());
	}

	//管理员删除用户
	@PostMapping("/deleteUser")
	boolean deleteUser(@RequestBody Long userId,HttpServletRequest request){
		if(isAdmin(request)){
			return false;
		}
		if(userId < 1){
			return false;
		}
		return userService.removeById(userId);
	}

	//公共方法 判断是否是管理员
	public boolean isAdmin(HttpServletRequest request){
		//判断是否是管理员
		Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
		User user = (User)userObj;
		if(user == null || user.getUserRole() != ADMIN_ROLE){
			return false;
		}
		return true;
	}
}
