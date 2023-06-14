package com.example.myusercenterback.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: UserLoginRequest
 * Package: com.example.myusercenterback.model.request
 * Description:用户登录请求体
 *
 * @Author 王腾腾
 * @Create 2023/6/11 17:09
 * @Version 1.0
 */
@Data
public class UserLoginRequest implements Serializable {
	public String userAccount;
	public String userPassword;
}
