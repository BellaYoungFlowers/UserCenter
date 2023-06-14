package com.example.myusercenterback.controller;

import com.example.myusercenterback.model.User;
import com.example.myusercenterback.model.request.UserLoginRequest;
import com.example.myusercenterback.model.request.UserRegisterRequest;
import com.example.myusercenterback.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
}
