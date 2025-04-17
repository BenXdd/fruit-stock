package com.dyl.fruitstock.entity;

import com.dyl.fruitstock.service.IPage;

import java.util.Collections;
import java.util.List;

public class Page<T> implements IPage<T> {
    /**
     * 查询数据列表
     */
    private List<T> list  = Collections.emptyList();
    /**
     * 总数
     */
    private long     total = 0;
    /**
     * 每页显示条数，默认 10
     */
    private long     size  = 10;
    /**
     * 当前页
     */
    private long     page  = 1;
    /**
     * SQL 排序 ASC 数组
     */
    private String[] orderBy;
    /**
     * 是否进行 count 查询
     */
    private boolean  count = true;

    private Object[] nextSortValue;

    public Page() {
        // to do nothing
    }

    /**
     * 分页构造函数
     *
     * @param page 当前页
     * @param size 每页显示条数
     */
    public Page(long page, long size) {
        this(page, size, 0);
    }

    public Page(long page, long size, long total) {
        this(page, size, total, true);
    }

    public Page(long page, long size, boolean isSearchCount) {
        this(page, size, 0, isSearchCount);
    }

    public Page(long page, long size, long total, boolean isSearchCount) {
        if (page > 1) {
            this.page = page;
        }
        this.size = size;
        this.total = total;
        this.count = isSearchCount;
    }

    /**
     * 是否存在上一页
     *
     * @return true / false
     */
    public boolean hasPrevious() {
        return this.page > 1;
    }

    /**
     * 是否存在下一页
     *
     * @return true / false
     */
    public boolean hasNext() {
        return this.page < this.getTotalPages();
    }

    @Override
    public List<T> getList() {
        return this.list;
    }

    @Override
    public Page<T> setList(List<T> records) {
        this.list = records;
        return this;
    }

    @Override
    public long getTotal() {
        return this.total;
    }

    @Override
    public Page<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public Page<T> setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public long getPage() {
        return this.page;
    }

    @Override
    public Page<T> setPage(long page) {
        this.page = page;
        return this;
    }

    @Override
    public String[] orderBy() {
        return orderBy;
    }

    @Override
    public Page<T> setOrderBy(String[] orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public void setCount(boolean count) {
        this.count = count;
    }

    @Override
    public Object[] getNextSortValue() {
        return nextSortValue;
    }

    public void setNextSortValue(Object[] nextSortValue) {
        this.nextSortValue = nextSortValue;
    }
}