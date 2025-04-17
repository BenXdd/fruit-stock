package com.dyl.fruitstock.service;

import com.dyl.fruitstock.entity.Page;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public interface IPage<T> extends Serializable {
    /**
     * 降序字段数组
     *
     * @return order by desc 的字段数组
     */
    default String[] orderBy() {
        return null;
    }

    /**
     * 进行 count 查询 【 默认: true 】
     *
     * @return true 是 / false 否
     */
    default boolean count() {
        return true;
    }

    /**
     * 计算当前分页偏移量
     */
    default long offset() {
        return getPage() > 0 ? (getPage() - 1) * getSize() : 0;
    }

    /**
     * 当前分页总页数
     */
    default long getTotalPages() {
        if (getSize() == 0) {
            return 0L;
        }
        long pages = getTotal() / getSize();
        if (getTotal() % getSize() != 0) {
            pages++;
        }
        return pages;
    }

    /**
     * 内部什么也不干
     * <p>只是为了 json 反序列化时不报错</p>
     */
    default IPage<T> setTotalPages(long pages) {
        // to do nothing
        return this;
    }

    /**
     * 分页记录列表
     *
     * @return 分页对象记录列表
     */
    List<T> getList();

    /**
     * 设置分页记录列表
     */
    IPage<T> setList(List<T> records);

    /**
     * 当前满足条件总行数
     *
     * @return 总条数
     */
    long getTotal();

    /**
     * 设置当前满足条件总行数
     */
    IPage<T> setTotal(long total);

    /**
     * 当前分页总页数
     *
     * @return 总页数
     */
    long getSize();

    /**
     * 设置当前分页总页数
     */
    IPage<T> setSize(long size);

    /**
     * 当前页，默认 1
     *
     * @return 当然页
     */
    long getPage();

    /**
     * 设置当前页
     */
    IPage<T> setPage(long current);

    Page<T> setOrderBy(String[] orderBy);

    Object[] getNextSortValue();

    /**
     * IPage 的泛型转换
     *
     * @param mapper 转换函数
     * @param <R>    转换后的泛型
     * @return 转换泛型后的 IPage
     */
    @SuppressWarnings("unchecked")
    default <R> IPage<R> convert(Function<? super T, ? extends R> mapper) {
        List<R> collect = this.getList().stream().map(mapper).collect(toList());
        return ((IPage<R>) this).setList(collect);
    }
}