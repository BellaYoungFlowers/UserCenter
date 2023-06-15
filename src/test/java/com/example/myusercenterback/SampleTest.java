package com.example.myusercenterback;

 import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
 import com.example.myusercenterback.mapper.UserMapper;
 import com.example.myusercenterback.model.User;
import org.junit.Assert;
// import org.junit.jupiter.api.Test;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SampleTest {

     @Autowired
     // @Resource
     private UserMapper userMapper;

     @Test
     public void testSelect() {
         System.out.println(("----- selectAll method test ------"));
         QueryWrapper<User> queryWrapper = new QueryWrapper<>();
         List<User> userList = userMapper.selectList(queryWrapper);
         Assert.assertEquals(1, userList.size());
         userList.forEach(System.out::println);
     }

}