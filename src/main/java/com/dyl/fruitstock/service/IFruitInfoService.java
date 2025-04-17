package com.dyl.fruitstock.service;

import com.dyl.fruitstock.entity.FruitInfo;
import java.util.List;


/**
 * (FruitInfo)表服务接口
 *
 * @author makejava
 * @since 2025-04-14 16:25:34
 */
public interface IFruitInfoService {


    /**
     * 通过主键获取数据
     * @param id 主键
     * @return 实体
     */
    FruitInfo getById(Integer id);

    /**
     * 通过主键获取数据
     *
     * @param ids 主键列表
     */
    List<FruitInfo> listByIds(List<Integer> ids);

    /**
     * 修改数据
     *
     * @param fruitInfo 实例对象
     * @return 实例对象
     */
    Integer updateById(FruitInfo fruitInfo);
    /**
     * 新增数据
     *
     * @param fruitInfo 实例对象
     * @return 实例对象
     */
    Integer save(FruitInfo fruitInfo);  
    
    /**
     * 新增或更新
     *
     * @param fruitInfo 实例对象
     * @return 实例对象
     */
    Integer saveOrUpdate(FruitInfo fruitInfo);

    /**
     * 批量保存
     *
     * @param entities 实体对象
     * @return 主键
     */
    Integer saveBatch(List<FruitInfo> entities);

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
    List<FruitInfo> select(FruitInfo query);

}
