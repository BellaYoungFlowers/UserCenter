package com.example.myusercenterback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myusercenterback.model.User;
import com.example.myusercenterback.service.UserService;
import com.example.myusercenterback.mapper.UserMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

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

	@Override
	public long UserRegister(String userAccount, String userPassword, String checkPassword) {
		//校验账户 密码 校验密码
		if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
			return -1;
		}
		if (userAccount.length() < 4) {
			return -1;
		}
		if (userPassword.length() < 8) {
			return -1;
		}
		if (!userPassword.equals(checkPassword)) {
			return -1;
		}

		//账户不重复
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userAccount",userAccount);
		long count = this.count(queryWrapper);
		if(count!=0){
			return -1;
		}

		//对密码加密
		String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
		System.out.println(encryptedPassword);

		//插入数据
		User user = new User();
		user.setUserAccount(userAccount);
		user.setUserPassword(encryptedPassword);

		boolean result = this.save(user);
		if (!result) {//注意这个方法返回的long 实体类里面是Long 如果保存失败  id是null getId会报错
			return -1;
		}
		//如果查不到
		return user.getId();
	}

	@Override
	public User UserLogin(String userAccount, String userPassword, HttpServletRequest httpServlet) {
		//校验
		if (StringUtils.isAnyBlank(userAccount, userPassword)) {
			return null;
		}
		if (userAccount.length() < 4) {
			return null;
		}
		if (userPassword.length() < 8) {
			return null;
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
			return null;
		}

		//用户脱敏
		User safetyUser = getSafetyUser(user);

		//设置用户的登录状态(这是干什么)
		httpServlet.setAttribute("session",safetyUser);

		return safetyUser;
	}


	public User getSafetyUser(User user) {
		User SafeUser = new User();
		SafeUser.setId(user.getId());
		SafeUser.setUsername(user.getUsername());
		SafeUser.setUserAccount(user.getUserAccount());
		SafeUser.setAvatarUrl(user.getAvatarUrl());
		SafeUser.setGender(user.getGender());
		SafeUser.setPhone(user.getPhone());
		SafeUser.setEmail(user.getEmail());
		SafeUser.setUserStatus(user.getUserStatus());
		SafeUser.setCreateTime(user.getCreateTime());
		SafeUser.setUpdateTime(user.getUpdateTime());
		return SafeUser;

	}
	//根据tag搜索用户
	@Override
	public List<User> getUsersByTags(List<String> tagsNameList) {
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
		// for (User user : userList) {
		// 	String tags = user.getTags();
		// 	Set<String> tagsSet = gson.fromJson(tags, new TypeToken<Set<String>>(){}.getType());
		// 	for (String tag : tagsNameList) {
		// 		if(!tagsSet.contains(tag)){
		// 			return false;
		// 		}
		// 	}
		// 	return true;
		// }

		//脱敏 foreach写法
		userList.forEach(user->{
			getSafetyUser(user);
		});
		//脱敏 lambda写法
		userList.forEach(this::getSafetyUser);
		//脱敏 流操作
		userList.stream().map(user ->
			getSafetyUser(user)
		).collect(Collectors.toList());
		//脱敏 流操作 lambda写法
		userList.stream().map(this::getSafetyUser
		).collect(Collectors.toList());




		//语法糖
		return  userList.stream().filter((user) -> {
			String tags = user.getTags();
			if(StringUtils.isBlank(tags)){
				return false;
			}
			Set<String> tagsSet = gson.fromJson(tags, new TypeToken<Set<String>>(){}.getType());
			for (String tag : tagsNameList) {
				if(!tagsSet.contains(tag)){
					return false;
				}
			}
			return true;
		}).map(this::getSafetyUser).collect(Collectors.toList());
	}

}




