package com.dyl.fruitstock.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FruitInfo implements Entity<Integer> {


    private Integer id;

    /**
     * 水果名称 
     */
    private String name;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 0:下架, 1:上架
     */
    private String status;

    /**
     * 图片地址
     */
    private String imgUrl;


    /**
     * 销售价格
     */
    private BigDecimal salePrice;

    /**
     * 成本价格
     */
    private BigDecimal costPrice;


}
