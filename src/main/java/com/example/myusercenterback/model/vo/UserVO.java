package com.example.myusercenterback.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author:xxxxx
 * @create: 2023-07-26 16:00
 * @Description: 展示的用户信息
 */
@Data
public class UserVO implements Serializable {
    private Long id;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;


    /**
     * tags
     */
    private String tags;


    /**
     * 用户头像

     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 管理员
     */
    private Integer userRole;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态

     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 星球编码
     */
    private String planetCode;

}
