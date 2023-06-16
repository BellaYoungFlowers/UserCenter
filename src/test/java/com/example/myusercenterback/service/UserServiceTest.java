package com.example.myusercenterback.service;
import java.util.*;

import com.example.myusercenterback.model.User;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@SpringBootTest
class UserServiceTest {
	@Autowired
	private UserService userService;

	/**
	 * 简单测试mybaits的sava方法
	 */
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

	/**
	 * 测试foreach的插入
	 */
	@Test
	public void testaddUserList(){
		User user = new User();
		user.setUsername("a");
		user.setUserAccount("a");
		user.setUserPassword("a");

		User user1 = new User();
		user1.setUsername("a1");
		user1.setUserAccount("a1");
		user1.setUserPassword("a1");


		User user2 = new User();
		user2.setUsername("a2");
		user2.setUserAccount("a2");
		user2.setUserPassword("a2");

		List<User> userList = new ArrayList();
		userList.add(user);
		userList.add(user1);
		userList.add(user2);

//		boolean save = userService.saveBatch(userList);

		//自己写的foreach 插入
		userService.testForeachInsert(userList);
	}


	/**
	 *测试foreach的更新
	 */
	@Test
	public void testupdateUserList(){
		User user = new User();
		user.setId(10L);
		user.setUsername("7r7");

		User user1 = new User();
		user1.setId(11L);
		user1.setUsername("r77");


		User user2 = new User();
		user2.setId(12L);
		user2.setUsername("r77");

		List<User> userList = new ArrayList();
		userList.add(user);
		userList.add(user1);
		userList.add(user2);

		//自己写的foreach 插入
		userService.testForeachUpdate(userList);
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
		userAccount = "Bella";
		userPassword = "Bella123456";
		encryptedPassword = "Bella123456";
		result = userService.UserRegister(userAccount, userPassword, encryptedPassword);
		Assert.assertTrue(result > 0);

	}

	@org.junit.Test
	public void userLogin() {


	}

	@Test
	public void getUsersByTags() {

		List<User> usersByTags = userService.getUsersByTags(Arrays.asList("唱", "跳", "篮球"));
		System.out.println(usersByTags);
		Assert.assertNotNull(usersByTags);

	}


	@Test
	public void testForeachIn() {
		List<Long> ids = Arrays.asList(10L, 12L, 13L);
		List<User> users = userService.testForeachIn(ids);
		log.info(users.toString());
	}
}