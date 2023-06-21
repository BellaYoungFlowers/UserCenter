package com.example.myusercenterback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myusercenterback.common.ErrorCode;
import com.example.myusercenterback.exception.BusinessException;
import com.example.myusercenterback.model.User;
import com.example.myusercenterback.service.UserService;
import com.example.myusercenterback.mapper.UserMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.myusercenterback.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author LuckyME
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-06-11 09:48:19
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

	@Resource
	private UserMapper userMapper;

	public static final  String SALT = "abc";


	//测试foreach 插入方法
	@Override
    public void testForeachInsert(List<User> users) {
		userMapper.testForeachInsert(users);
	}

	//测试foreach 更新方法
	@Override
	public void testForeachUpdate(List<User> userList) {
		userMapper.testForeachUpdate(userList);

	}

	//测试foreach in
	@Override
    public List<User> testForeachIn(List<Long> ids) {
        return userMapper.testForeachIn(ids);
    }


	@Override
	public long UserRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
		//校验账户 密码 校验密码
		if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
			throw new BusinessException(ErrorCode.NULL_ERROR,"参数为空");
		}
		if (userAccount.length() < 4) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名过短");
		}
		if (userPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
		}
		if (!userPassword.equals(checkPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验密码不一致");
		}
		if (planetCode.length() > 5) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球账号长度过长");
		}

		//账户不重复
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userAccount",userAccount);
		long count = this.count(queryWrapper);
		if(count!=0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
		}

		//星球账号不重复
		queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("planetCode",planetCode);
		count = this.count(queryWrapper);
		if(count!=0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号已存在");
		}

		//对密码加密
		String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
		System.out.println(encryptedPassword);

		//插入数据
		User user = new User();
		user.setUserAccount(userAccount);
		user.setUserPassword(encryptedPassword);
		user.setPlanetCode(planetCode);

		boolean result = this.save(user);
		if (!result) {//注意这个方法返回的long 实体类里面是Long 如果保存失败  id是null getId会报错
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"注册失败");
		}
		//如果查不到
		return user.getId();
	}

	@Override
	public User UserLogin(String userAccount, String userPassword, HttpServletRequest request) {
		//校验
		if (StringUtils.isAnyBlank(userAccount, userPassword)) {
			throw new BusinessException(ErrorCode.NULL_ERROR,"账号或者密码为空");
		}
		if (userAccount.length() < 4) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号过短");
		}
		if (userPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
		}

		//对密码加密
		String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
		System.out.println(encryptedPassword);

		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userAccount",userAccount);
		queryWrapper.eq("userPassword",encryptedPassword);
		User user = userMapper.selectOne(queryWrapper);
		if (user == null) {
			//打印日志 可能用户真的不存在 或者是用户账号或者密码输入错误
			log.info("user login failed");
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在登录失败");
		}

		//用户脱敏
		User safetyUser = getSafetyUser(user);

		//设置用户的登录状态
		request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

		return safetyUser;
	}


	@Override
	public User getSafetyUser(User originalUser) {
		User SafeUser = new User();
		SafeUser.setId(originalUser.getId());
		SafeUser.setUsername(originalUser.getUsername());
		SafeUser.setUserAccount(originalUser.getUserAccount());
		SafeUser.setAvatarUrl(originalUser.getAvatarUrl());
		SafeUser.setGender(originalUser.getGender());
		SafeUser.setPhone(originalUser.getPhone());
		SafeUser.setEmail(originalUser.getEmail());
		SafeUser.setTags(originalUser.getTags());
		SafeUser.setUserRole(originalUser.getUserRole());
		SafeUser.setPlanetCode(originalUser.getPlanetCode());
		SafeUser.setUserStatus(originalUser.getUserStatus());
		SafeUser.setCreateTime(originalUser.getCreateTime());
		SafeUser.setUpdateTime(originalUser.getUpdateTime());
		return SafeUser;
	}


	//根据tag搜索用户
	@Override
	public List<User> getUsersByTags(List<String> tagsNameList) {
		if(ObjectUtils.isEmpty(tagsNameList)){
			throw new BusinessException(ErrorCode.NULL_ERROR,"标签为空");
		}
		//第一种方式 sql查询
		// QueryWrapper<User> query = new QueryWrapper<>();
		// for (String tagName : tagsNameList) {
		// 	query = query.like("tags",tagName);
		// }
		// List<User> userList = userMapper.selectList(query);
		// return userList;

		//第二种方式 sql全查询 内存再进行条件查询
		/**
		 * 第一步 查出全部user
		 * 第二步 遍历user 获取每个user的tags 将tags json对象转化为Set
		 * 第三步 判断user的tag是否包含tagsNameList 只要有一个不包含 返回false
		 */
		List<User> userList = userMapper.selectList(null);
		Gson gson = new Gson();


		//脱敏 foreach写法
//		userList.forEach(user->{
//			getSafetyUser(user);
//		});
		//脱敏 lambda写法
//		userList.forEach(this::getSafetyUser);
		//脱敏 流操作
//		userList.stream().map(user ->
//			getSafetyUser(user)
//		).collect(Collectors.toList());
		//脱敏 流操作 lambda写法
//		userList.stream().map(this::getSafetyUser
//		).collect(Collectors.toList());

		//语法糖
		return  userList.stream().filter((user) -> {
			String userTags = user.getTags();
			if(StringUtils.isBlank(userTags)){
				return false;
			}
			Set<String> userTagsSet = gson.fromJson(userTags, new TypeToken<Set<String>>(){}.getType());
			for (String tag : tagsNameList) {
				if(!userTagsSet.contains(tag)){
					return false;
				}
			}
			return true;
		}).map(this::getSafetyUser).collect(Collectors.toList());
	}

}




