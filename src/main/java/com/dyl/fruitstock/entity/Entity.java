package com.dyl.fruitstock.entity;

import java.io.Serializable;
import java.util.Date;

public interface Entity<T> extends Serializable {
    T getId();

    default void setCreateTime(Date date) {
    }

    default void setCreateBy(String createBy) {

    }

    default void setUpdateTime(Date date) {

    }

    default void setUpdateBy(String updateBy) {

    }
}
