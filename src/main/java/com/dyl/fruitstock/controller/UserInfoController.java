package com.dyl.fruitstock.controller;


import java.util.HashMap;
import java.util.Map;

import com.dyl.fruitstock.dto.login.ResultRsp;
import com.dyl.fruitstock.entity.UserInfo;
import com.dyl.fruitstock.service.IPage;
import com.dyl.fruitstock.service.IUserInfoService;
import com.dyl.fruitstock.utils.EncryptHelpUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (UserInfo)表控制层
 *
 * @author makejava
 * @since 2025-04-10 18:49:11
 */
@RestController
@RequestMapping("/user")
public class UserInfoController {
    /**
     * 服务对象
     */
    @Resource
    private IUserInfoService userInfoService;


    private static final String Key = "ZW6wAcLaa9LujHAS";

    /**
      * 保存
      */
    @PostMapping
    public ResultRsp<Map<String, Integer>> save(@RequestBody UserInfo userInfo){


        String userPassword = userInfo.getUserPassword();
        String encryptPassword = EncryptHelpUtils.aesEncryptStr(Key, userPassword);
        userInfo.setUserPassword(encryptPassword);
        userInfoService.save(userInfo);
        HashMap<String, Integer> map = new HashMap<>();
        map.put("id", userInfo.getId());
        return ResultRsp.success(map);
    }


    /**
     * 更新
     */
    @PutMapping("{id}" )
    public ResultRsp<Void> update(@PathVariable("id" ) Integer id, @RequestBody UserInfo userInfo){
        userInfo.setId(id);
        userInfoService.updateById(userInfo);
        return ResultRsp.success();
    }

    /**
    * 通过id查找
    */
    @GetMapping("{id}" )
    public ResultRsp<UserInfo> findById(@PathVariable("id" ) Integer id){
        UserInfo userInfo = userInfoService.getById(id);
        if(userInfo == null){
            throw new RuntimeException();
        }
        return ResultRsp.success(userInfo);
    }

    /**
    * 通过条件查询
    */
    @GetMapping
    public ResultRsp<IPage<UserInfo>>list(UserInfo query, IPage<UserInfo> page){
        return ResultRsp.success(userInfoService.select(query,page));
    }

}
