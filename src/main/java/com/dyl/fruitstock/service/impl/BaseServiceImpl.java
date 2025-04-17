package com.dyl.fruitstock.service.impl;


import com.dyl.fruitstock.entity.Entity;
import com.dyl.fruitstock.mapper.BaseMapper;
import com.dyl.fruitstock.service.IBaseService;
import com.dyl.fruitstock.service.IPage;
import lombok.Data;

import java.util.List;

@Data
public class BaseServiceImpl<M extends BaseMapper<E, K>, E extends Entity<K>, K> implements IBaseService<E, K> {
    private M baseMapper;

    public BaseServiceImpl(M baseMapper) {
        this.baseMapper = baseMapper;
    }

    /**
     * 通过主键获取数据
     *
     * @param id 主键
     * @return 实体
     */
    @Override
    public E getById(K id) {
        return (E) baseMapper.selectById(id);
    }

    /**
     * 通过主键获取数据
     *
     * @param ids 主键列表
     */
    @Override
    public List<E> listByIds(List<K> ids) {
        return baseMapper.selectByIds(ids);
    }

    /**
     * 根据主键更新
     */
    @Override
    public Integer updateById(E entity) {
        return baseMapper.updateById(entity);
    }

    /**
     * 保存
     *
     * @param entity 实体对象
     * @return 主键
     */
    @Override
    public K save(E entity) {
        baseMapper.insert(entity);
        return entity.getId();
    }


    /**
     * 批量保存
     *
     * @param entities 实体对象
     * @return 主键
     */
    @Override
    public Integer saveBatch(List<E> entities) {
        return baseMapper.insertBatch(entities);
    }

    @Override
    public K saveOrUpdate(E entity) {
        baseMapper.insertOrUpdate(entity);
        return entity.getId();
    }

    /**
     * 查询列表
     *
     * @param query 查询条件对象
     * @param page  分页条件
     * @return 实体列表
     */
    @Override
    public <T, O> IPage<O> select(T query, IPage<O> page) {
        baseMapper.select(query, page);
        return page;
    }

    /**
     * 查询列表
     *
     * @param query 查询条件对象
     * @return 实体列表
     */
    @Override
    public List<E> select(E query) {
        return baseMapper.select(query);
    }

}
