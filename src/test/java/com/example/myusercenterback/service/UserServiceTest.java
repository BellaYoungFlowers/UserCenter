package com.example.myusercenterback.service;
import java.util.*;

import com.example.myusercenterback.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: UserServiceTest
 * Package: com.example.myusercenterback.service
 * Description: 用户服务测试
 *
 * @Author 王腾腾
 * @Create 2023/6/11 9:52
 * @Version 1.0
 */
@SpringBootTest
class UserServiceTest {
	@Autowired
	private UserService userService;

	@Test
	public void testaddUser(){
		User user = new User();
		user.setUsername("Bella");
		user.setUserAccount("BellaAccount");
		user.setAvatarUrl("https://www.google.com/webhp?hl=zh-CN&ictx=2&sa=X&ved=0ahUKEwj45rGylbr_AhWil4kEHRRODL4QPQgJ");
		user.setGender(0);
		user.setUserPassword("123");
		user.setPhone("178653");
		user.setEmail("123@emla.com");
		boolean save = userService.save(user);
		System.out.println(user.getId());
		assertEquals(true, save);
	}

	@Test
	void userRegister() {
		String userAccount = "";
		String userPassword = "";
		String encryptedPassword = "";
		long result;

		//账号不能小于4
		userAccount = "ma";
		userPassword = "hhakhflahtoahr";
		encryptedPassword = "hhakhflahtoahr";
		result = userService.UserRegister(userAccount, userPassword, encryptedPassword);
		Assert.assertEquals(-1,result);

		//密码不能小于8
		userAccount = "jihhggakf";
		userPassword = "jjj";
		encryptedPassword = "jjj";
		result = userService.UserRegister(userAccount, userPassword, encryptedPassword);
		Assert.assertEquals(-1,result);

		//密码和校验密码相同
		userAccount = "jihhggakf";
		userPassword = "ma";
		encryptedPassword = "werr";
		result = userService.UserRegister(userAccount, userPassword, encryptedPassword);
		Assert.assertEquals(-1,result);

		//用户名为空
		userAccount = "";
		userPassword = "werrhhhhhhh";
		encryptedPassword = "werrhhhhhhh";
		result = userService.UserRegister(userAccount, userPassword, encryptedPassword);
		Assert.assertEquals(-1,result);

		//正常注册
		userAccount = "werrhhhhhhh";
		userPassword = "werrhhhhhhh";
		encryptedPassword = "werrhhhhhhh";
		result = userService.UserRegister(userAccount, userPassword, encryptedPassword);
		// Assert.assertTrue(result > 0);
		Assert.assertEquals(-1,result);

	}

	@Test
	public void getUsersByTags() {

		List<User> usersByTags = userService.getUsersByTags(Arrays.asList("唱", "跳", "篮球"));
		System.out.println(usersByTags);
		Assert.assertNotNull(usersByTags);

	}
}