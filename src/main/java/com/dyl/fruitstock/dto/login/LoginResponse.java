package com.dyl.fruitstock.dto.login;

import com.alibaba.fastjson.JSON;
import com.dyl.fruitstock.entity.UserInfo;
import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private Object data;

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }


    public static void main(String[] args) {

        UserInfo dyl = UserInfo.builder().userName("dyl").userPassword("123456").build();
        LoginResponse loginResponse = new LoginResponse(true, "登录成功", dyl);

        System.out.println(JSON.toJSONString(loginResponse));
    }
}