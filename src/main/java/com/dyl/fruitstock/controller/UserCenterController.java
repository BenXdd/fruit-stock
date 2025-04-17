package com.dyl.fruitstock.controller;

import com.alibaba.fastjson.JSON;
import com.dyl.fruitstock.dto.login.LoginRequest;
import com.dyl.fruitstock.dto.login.LoginResponse;
import com.dyl.fruitstock.entity.UserInfo;
import com.dyl.fruitstock.service.IUserInfoService;
import com.dyl.fruitstock.utils.EncryptHelpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserCenterController {

    @Resource
    private IUserInfoService userInfoService;

    private static final String Key = "ZW6wAcLaa9LujHAS";

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("#login req:{}", JSON.toJSONString(request));
        String username = request.getUserName();
        String password = request.getPassWord();
        if (StringUtils.isBlank(username)) {
            return ResponseEntity.ok(new LoginResponse(false, "请输入用户名"));
        }
        if (StringUtils.isBlank(password)) {
            return ResponseEntity.ok(new LoginResponse(false, "请输入密码"));
        }


        String encryptPassword = EncryptHelpUtils.aesEncryptStr(Key, password);
        UserInfo userInfo = userInfoService.queryUser(username, encryptPassword);
        if (Objects.isNull(userInfo)) {
            return ResponseEntity.ok(new LoginResponse(false, "用户名或密码错误"));
        } else {
            return ResponseEntity.ok(new LoginResponse(true, "登录成功", userInfo));

        }

    }


    public static void main(String[] args) {
        String password = EncryptHelpUtils.aesEncryptStr(Key,"123456");
        System.out.println("password:"+password);
    }


}


