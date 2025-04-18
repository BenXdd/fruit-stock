package com.dyl.fruitstock.mapper;

import com.dyl.fruitstock.entity.FruitInfo;
import org.apache.ibatis.annotations.Param;


/**
 * (FruitInfo)表数据库访问层
 *
 * @author makejava
 * @since 2025-04-14 16:25:34
 */
public interface FruitInfoMapper extends BaseMapper<FruitInfo, Integer> {
    int updateStock(@Param("productId") Integer productId, @Param("quantity") Integer quantity);
}
