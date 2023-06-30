package com.example.myusercenterback;

 import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
 import com.example.myusercenterback.mapper.UserMapper;
 import com.example.myusercenterback.model.User;
 import com.example.myusercenterback.once.DemoData;
 import org.junit.Assert;
// import org.junit.jupiter.api.Test;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
 import java.util.*;
 import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SampleTest {

     @Autowired
     // @Resource
     private UserMapper userMapper;

     @Test
     public void testSelect() {
         DemoData data1 = new DemoData("wangtt", 12);
         DemoData data2 = new DemoData("wangtt", 14);
         DemoData data3 = new DemoData("yuan1", 51);
         DemoData data4 = new DemoData("yuan1", 26);

         List<DemoData> list = new ArrayList<DemoData>();
         list.add(data1);
         list.add(data2);
         list.add(data3);
         list.add(data4);

         Map<String, List<DemoData>> collect = list.stream().collect(Collectors.groupingBy(DemoData::getName));
         //       相同检测日期取最新的一期
         for (Map.Entry<String, List<DemoData>> stringListEntry : collect.entrySet()) {
             if(stringListEntry.getValue().size()>1){
                 System.out.println("有重的数据"+stringListEntry.getValue());
             }
         }



     }


}