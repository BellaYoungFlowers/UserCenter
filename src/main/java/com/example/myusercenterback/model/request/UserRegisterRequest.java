package com.example.myusercenterback.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: UserRegisterRequest
 * Package: com.example.myusercenterback.model.request
 * Description:用户注册请求体
 *
 * @Author 王腾腾
 * @Create 2023/6/11 16:54
 * @Version 1.0
 */
@Data
public class UserRegisterRequest implements Serializable {
	public String userAccount;
	public String userPassword;
	public String  checkPassword;

}
