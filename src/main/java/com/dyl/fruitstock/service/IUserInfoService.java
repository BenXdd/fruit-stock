package com.dyl.fruitstock.service;

import com.dyl.fruitstock.entity.UserInfo;
import java.util.List;


/**
 * (UserInfo)表服务接口
 *
 * @author makejava
 * @since 2025-04-10 18:49:11
 */
public interface IUserInfoService {


    /**
     * 通过主键获取数据
     * @param id 主键
     * @return 实体
     */
    UserInfo getById(Integer id);

    /**
     * 通过主键获取数据
     *
     * @param ids 主键列表
     */
    List<UserInfo> listByIds(List<Integer> ids);

    /**
     * 修改数据
     *
     * @param userInfo 实例对象
     * @return 实例对象
     */
    Integer updateById(UserInfo userInfo);
    /**
     * 新增数据
     *
     * @param userInfo 实例对象
     * @return 实例对象
     */
    Integer save(UserInfo userInfo);  
    
    /**
     * 新增或更新
     *
     * @param userInfo 实例对象
     * @return 实例对象
     */
    Integer saveOrUpdate(UserInfo userInfo);

    /**
     * 批量保存
     *
     * @param entities 实体对象
     * @return 主键
     */
    Integer saveBatch(List<UserInfo> entities);

    /**
     * 查询列表
     *
     * @param query 查询条件对象
     * @param page 分页条件
     * @return 实体列表
     */
    <T, O> IPage<O> select(T query, IPage<O> page);
    
    
    /**
     * 查询列表
     *
     * @param query 查询条件对象
     * @return 实体列表
     */
    List<UserInfo> select(UserInfo query);

    UserInfo queryUser(String username, String password);
}
