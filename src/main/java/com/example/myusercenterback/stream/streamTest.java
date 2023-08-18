package com.example.myusercenterback.stream;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author:xxxxx
 * @create: 2023-08-15 14:04
 * @Description: 测试stream流
 */
public class streamTest {

    /**
     * streamOf静态方法
     */
    public void testStream1(){
        Stream<String> today = Stream.of("today", "was", "a", "fairy", "tale");
        today.forEach(System.out::println);
    }

    /**
     * 数组或者list
     */
    public void testStream2(){


    }



}
