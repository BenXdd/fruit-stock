package com.dyl.fruitstock.service;
import com.dyl.fruitstock.entity.Entity;

import java.util.List;

public interface IBaseService<E extends Entity<K>, K> {
    E getById(K id);

    List<E> listByIds(List<K> ids);

    Integer updateById(E employee);

    default Integer remove(K id) {
        return 0;
    }

    K save(E entity);

    Integer saveBatch(List<E> entities);

    K saveOrUpdate(E entity);

    <T, O> IPage<O> select(T query, IPage<O> page);

    List<E> select(E query);
}

