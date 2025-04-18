package com.dyl.fruitstock.service;


import com.dyl.fruitstock.entity.OrderInfo;

import java.util.List;

/**
 * (OrderInfo)表服务接口
 *
 * @author makejava
 * @since 2025-04-18 19:51:05
 */
public interface IOrderInfoService {


    /**
     * 通过主键获取数据
     * @param id 主键
     * @return 实体
     */
    OrderInfo getById(Integer id);

    /**
     * 通过主键获取数据
     *
     * @param ids 主键列表
     */
    List<OrderInfo> listByIds(List<Integer> ids);

    /**
     * 修改数据
     *
     * @param orderInfo 实例对象
     * @return 实例对象
     */
    Integer updateById(OrderInfo orderInfo);
    /**
     * 新增数据
     *
     * @param orderInfo 实例对象
     * @return 实例对象
     */
    Integer save(OrderInfo orderInfo);  
    
    /**
     * 新增或更新
     *
     * @param orderInfo 实例对象
     * @return 实例对象
     */
    Integer saveOrUpdate(OrderInfo orderInfo);

    /**
     * 批量保存
     *
     * @param entities 实体对象
     * @return 主键
     */
    Integer saveBatch(List<OrderInfo> entities);

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
    List<OrderInfo> select(OrderInfo query);

    String submitOrder(OrderInfo orderInfo);
}
