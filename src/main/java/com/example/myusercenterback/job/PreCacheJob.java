package com.example.myusercenterback.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myusercenterback.model.domain.User;
import com.example.myusercenterback.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author:xxxxx
 * @create: 2023-07-18 16:55
 * @Description: 设置定时任务预热缓存，解决推荐页首次加载慢的问题
 */
@Slf4j
public class PreCacheJob {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate redisTemplate;

    //重点用户
    private List<Long> mainUserList = Arrays.asList(1L);
    //每天执行 预热推荐用户

    @Scheduled(cron = "0 31 0 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            //只有一个线程可以获得锁
            //第一个参数是等待时间 为0说明只有一个线程可以抢到 其余的线程抢不到不会继续等待
            //第二个参数是锁失效时间 释放时间 -1 看门狗机制 30自动加时
            if(lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                System.out.println("getLock: " + Thread.currentThread().getId());
                //分布式锁
                for(Long user : mainUserList){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
                    Page<User> page = userService.page(new Page<>(1,20),queryWrapper);
                    String redisKey = String.format("yupao:user:recommend:%s",user);
                    ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
                    //写缓存
                    try {
                        valueOperations.set(redisKey,page,300000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            //执行完一定要释放锁
            //锁一定要设置过期时间
            //只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                System.out.println("unlock: "+ Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }


}
