package com.dyl.fruitstock.mapper;

import com.dyl.fruitstock.service.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseMapper<E, K> {
    /**
     * 根据主键查询
     */
    E selectById(K id);

    /**
     * 根据主键查询
     */
    List<E> selectByIds(@Param("list") List<K> ids);

    /**
     * 根据主键更新
     */
    int updateById(E entity);

    /**
     * 查询一条记录
     *
     * @param query
     * @param <T>
     * @return
     */
    <T> E selectOne(@Param("q") T query);

    /**
     * 查询(不分页)
     */
    <T> List<E> select(@Param("q") T query);

    /**
     * 查询(分页)
     */
    <T> List<E> select(@Param("q") T query, IPage<?> page);

    /**
     * 插入数据
     */
    int insert(E employee);

    /**
     * 批量插入数据
     */
    int insertBatch(@Param("list") List<E> list);

    /**
     * 插入或更新
     * @param employee
     */
    int insertOrUpdate(E employee);
}
