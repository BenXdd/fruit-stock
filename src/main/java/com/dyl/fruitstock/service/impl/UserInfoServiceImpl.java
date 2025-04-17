package com.dyl.fruitstock.service.impl;

import com.dyl.fruitstock.entity.UserInfo;
import com.dyl.fruitstock.mapper.UserInfoMapper;
import com.dyl.fruitstock.service.IUserInfoService;
import org.springframework.stereotype.Service;

/**
 * (UserInfo)表服务实现类
 *
 * @author makejava
 * @since 2025-04-10 18:49:11
 */
@Service("userInfoService")
public class UserInfoServiceImpl extends BaseServiceImpl<UserInfoMapper, UserInfo, Integer> implements IUserInfoService {
    public UserInfoServiceImpl(UserInfoMapper userInfoMapper) {
        super(userInfoMapper);
    }


    @Override
    public UserInfo queryUser(String username, String password) {
        return this.getBaseMapper().selectOne(UserInfo.builder().userName(username).userPassword(password).build());
    }
}
