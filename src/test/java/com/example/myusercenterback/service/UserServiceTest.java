package com.example.myusercenterback.service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myusercenterback.mapper.UserMapper;
import com.example.myusercenterback.model.domain.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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
    @Resource
    private UserMapper userMapper;

    /**
     * 简单测试mybaits的sava方法
     */
    @Test
    public void testaddUser() {
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
    public void testaddUserList() {
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
     * 测试foreach的更新
     */
    @Test
    public void testupdateUserList() {
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
        String planetCode = "";
        long result;

        //账号不能小于4
        userAccount = "ma";
        userPassword = "hhakhflahtoahr";
        encryptedPassword = "hhakhflahtoahr";
        result = userService.UserRegister(userAccount, userPassword, encryptedPassword, planetCode);
        Assert.assertEquals(-1, result);

        //密码不能小于8
        userAccount = "jihhggakf";
        userPassword = "jjj";
        encryptedPassword = "jjj";
        result = userService.UserRegister(userAccount, userPassword, encryptedPassword, planetCode);
        Assert.assertEquals(-1, result);

        //密码和校验密码相同
        userAccount = "jihhggakf";
        userPassword = "ma";
        encryptedPassword = "werr";
        result = userService.UserRegister(userAccount, userPassword, encryptedPassword, planetCode);
        Assert.assertEquals(-1, result);

        //用户名为空
        userAccount = "";
        userPassword = "werrhhhhhhh";
        encryptedPassword = "werrhhhhhhh";
        result = userService.UserRegister(userAccount, userPassword, encryptedPassword, planetCode);
        Assert.assertEquals(-1, result);

        //正常注册
        userAccount = "Bella";
        userPassword = "Bella123456";
        encryptedPassword = "Bella123456";
        result = userService.UserRegister(userAccount, userPassword, encryptedPassword, planetCode);
        Assert.assertTrue(result > 0);

    }

    @Test
    public void userLogin() {
        List<Map<String, Integer>> originalList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map map = new HashMap();
            if (i % 3 == 0) {
                map.put("lc", i);
            } else {
                map.put("lc", 4444444);
            }
            originalList.add(map);
        }
        System.out.println("原数据"+originalList);

        List<Map<String, Integer>> mergedList = new ArrayList<>();

        //第一种写法
        int index = 0;
        for (int i = 1; i < originalList.size(); i++) {
            if (originalList.get(index).get("lc").equals(originalList.get(i).get("lc"))) {
                originalList.get(index).putAll(originalList.get(i));
                if (i == originalList.size()) {
                    mergedList.add(originalList.get(index));
                }
                continue;
            }
            mergedList.add(originalList.get(index));
            index = i;
            if (i == originalList.size()) {
                mergedList.add(originalList.get(index));
            }

        }
        System.out.println("第一种" + mergedList);
        mergedList.clear();

        // 第二种写法
        for (Map<String, Integer> map : originalList) {
            boolean found = false;
            for (Map<String, Integer> mergedMap : mergedList) {
                if (map.equals(mergedMap)) {
                    // 合并相同的Map
                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                        String key = entry.getKey();
                        int value = entry.getValue();
                        int mergedValue = mergedMap.getOrDefault(key, 0);
                        mergedMap.put(key, mergedValue + value);
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                // 如果不存在相同的Map，则直接添加到结果列表中
                mergedList.add(map);
            }
        }
        // 输出合并后的结果
        System.out.println("第二种写法" + mergedList);
        mergedList.clear();

        //第三种写法
        mergedList = originalList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(map -> map.get("lc"))))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Integer> mergedMap = new HashMap<>(entry.getKey());
                    mergedMap.put("value", entry.getValue());
                    return mergedMap;
                })
                .collect(Collectors.toList());
        System.out.println("第三种写法" + mergedList);
    }

//	@Test
//	public void getUsersByTags() {
//
//		List<User> usersByTags = userService.getUsersByTags(Arrays.asList("唱", "跳", "篮球"));
//		System.out.println(usersByTags);
//		Assert.assertNotNull(usersByTags);
//
//	}


    @Test
    public void testForeachIn() {
        List<Long> ids = Arrays.asList(10L, 12L, 13L);
        List<User> users = userService.testForeachIn(ids);
        log.info(users.toString());
    }

    @Test
    public void test() {
        String str = "100% of the people love programming.";
        System.out.println(str);
        str = str.replaceAll("%", "\\\\%");
        System.out.println(str);
    }

    //新增一万条数据到数据库
    @Test
    public void testInsert() {
        List<User> list = new ArrayList<>();
        for (int i = 10013; i < 20000; i++) {
            User user = new User();
            user.setId((long) i);
            user.setUserPassword(i + "password");
            user.setUserStatus(0);
            user.setIsDelete(0);
            user.setUserRole(0);
            user.setPlanetCode(i + "planetCode");
            list.add(user);
        }
        boolean b = userService.saveBatch(list);
        Assert.assertTrue(b);
    }

    //测试sql时间
    @Test
    public void sqlTime() {
        QueryWrapper<User> query = new QueryWrapper<>();
        User user = userMapper.selectById(0L);//去掉第一次连接数据库的时间

        long begin = System.currentTimeMillis();
        List<String> tagsNameList = Arrays.asList("java", "c++");
        for (String tagName : tagsNameList) {
            query = query.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(query);
        long end = System.currentTimeMillis();
        log.info(String.valueOf(userList.size()));
        log.info("sql时间" + (end - begin));
    }

    //测试内存时间
    @Test
    public void testJava() {
        List<User> userList = userMapper.selectList(null);
        Gson gson = new Gson();

        long begin = System.currentTimeMillis();
        List<String> tagsNameList = Arrays.asList("java", "c++");
        List<User> list = userList.stream().filter((user) -> {
            String userTags = user.getTags();
            if (StringUtils.isBlank(userTags)) {
                return false;
            }
            Set<String> userTagsSet = gson.fromJson(userTags, new TypeToken<Set<String>>() {
            }.getType());
            for (String tag : tagsNameList) {
                if (!userTagsSet.contains(tag)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        long end = System.currentTimeMillis();
        log.info(String.valueOf(list.size()));
        log.info("内存时间" + (end - begin));
    }


}