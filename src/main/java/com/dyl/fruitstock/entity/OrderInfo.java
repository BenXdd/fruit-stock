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
public class OrderInfo implements Entity<Integer> {


    private Integer id;

    /**
     * 订单快捷码
     */
    private String orderCode;

    /**
     * 今日日期:yyyy-MM-dd
     */
    private String todayStr;

    /**
     * 商品信息
     */
    private String productInfo;

    /**
     * 销售总价格
     */
    private BigDecimal totalSalePrice;

    /**
     * 成本总价格
     */
    private BigDecimal totalCostPrice;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

}
