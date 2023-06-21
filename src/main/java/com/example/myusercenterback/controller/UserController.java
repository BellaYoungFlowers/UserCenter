package com.example.myusercenterback.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myusercenterback.common.BaseResponse;
import com.example.myusercenterback.common.ErrorCode;
import com.example.myusercenterback.common.ResultUtils;
import com.example.myusercenterback.exception.BusinessException;
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
	public BaseResponse<Long> userRegistration(@RequestBody  UserRegisterRequest userRegisterRequest){
		if(userRegisterRequest == null){
//			return ResultUtils.error(ErrorCode.NULL_ERROR);
			throw new BusinessException(ErrorCode.NULL_ERROR,"未传参 参数是null");
		}
		String userAccount = userRegisterRequest.getUserAccount();
		String userPassword = userRegisterRequest.getUserPassword();
		String checkPassword = userRegisterRequest.getCheckPassword();
		String planetCode = userRegisterRequest.getPlanetCode();
		if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
			throw new BusinessException(ErrorCode.NULL_ERROR,"账号或者密码或者校验密码或者星球编码参数是null");
		}
		long usered = userService.UserRegister(userAccount, userPassword, checkPassword, planetCode);
		return ResultUtils.success(usered);
	}

	@PostMapping("/userLogin")
	public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
		if(userLoginRequest == null){
			throw new BusinessException(ErrorCode.NULL_ERROR,"未传参 参数是null");
		}
		String userAccount = userLoginRequest.getUserAccount();
		String userPassword = userLoginRequest.getUserPassword();
		if(StringUtils.isAnyBlank(userAccount,userPassword)){
			throw new BusinessException(ErrorCode.NULL_ERROR,"未传用户账号或者密码");
		}
		User user = userService.UserLogin(userAccount, userPassword, request);
		return  ResultUtils.success(user);
	}


	/**
	 *用户注销账号
	 */
	@PostMapping("/userLogout")
	public BaseResponse<Integer> userLogout(HttpServletRequest request){
		request.getSession().removeAttribute(USER_LOGIN_STATE);
		return ResultUtils.success(1);
	}


	//管理员根据名字查询
	@GetMapping("/search")
	public BaseResponse<List<User>> searchByName(@RequestParam("username") String username,HttpServletRequest request){
		boolean admin = isAdmin(request);
		if(!admin){
			throw new BusinessException(ErrorCode.NOT_AUTH,"不是管理员");
		}

		if(StringUtils.isBlank(username)){
			throw new BusinessException(ErrorCode.NULL_ERROR,"用户名为空");
		}

		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("username", username);
		//脱敏
		List<User> collect = userService.list(queryWrapper)
				.stream()
				.map(user -> userService.getSafetyUser(user))
				.collect(Collectors.toList());
		return ResultUtils.success(collect);
	}

	//管理员删除用户
	@PostMapping("/deleteUser")
	public BaseResponse<Boolean> deleteUser(@RequestBody Long userId,HttpServletRequest request){
		if(!isAdmin(request)){
			throw new BusinessException(ErrorCode.NOT_AUTH,"不是管理员");
		}
		if(userId < 1){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户id不可以小于0");
		}
		boolean id = userService.removeById(userId);
		return ResultUtils.success(id);
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
