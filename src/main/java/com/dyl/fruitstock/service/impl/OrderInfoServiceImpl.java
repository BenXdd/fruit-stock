package com.dyl.fruitstock.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dyl.fruitstock.entity.FruitInfo;
import com.dyl.fruitstock.entity.OrderInfo;
import com.dyl.fruitstock.exception.BusinessException;
import com.dyl.fruitstock.mapper.FruitInfoMapper;
import com.dyl.fruitstock.mapper.OrderInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.dyl.fruitstock.service.IOrderInfoService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * (OrderInfo)表服务实现类
 *
 * @author makejava
 * @since 2025-04-18 19:51:08
 */
@Service("orderInfoService")
public class OrderInfoServiceImpl extends BaseServiceImpl<OrderInfoMapper, OrderInfo, Integer> implements IOrderInfoService {
    public OrderInfoServiceImpl(OrderInfoMapper orderInfoMapper) {
        super(orderInfoMapper);
    }


    @Resource
    private FruitInfoMapper fruitInfoMapper;


    @Transactional
    @Override
    public void submitOrder(OrderInfo orderInfo) {


        String productInfo = orderInfo.getProductInfo();
        if (StringUtils.isBlank(productInfo)){
            throw new BusinessException(400,"请选择有效的商品");
        }


        BigDecimal totalSalePrice = BigDecimal.ZERO;
        BigDecimal totalCostPrice = BigDecimal.ZERO;

        JSONArray jsonArray = JSONArray.parseArray(productInfo);
        for (Object o : jsonArray) {
            JSONObject ele = JSONObject.parseObject(o.toString());

            Integer productId = ele.getInteger("productId");
            Integer quantity = ele.getInteger("quantity");


            FruitInfo fruitInfo = fruitInfoMapper.selectById(productId);


            //库存扣减
            if (quantity > fruitInfo.getStock()){
                throw new BusinessException(400, "商品:"+fruitInfo.getName()+" 已被抢空请重新选择");
            }else {
                int i = fruitInfoMapper.updateStock(productId, quantity);
                if (i < 1){
                    throw new BusinessException(400, "商品:"+fruitInfo.getName()+" 已被抢空请重新选择");
                }
            }

            BigDecimal saleEle = fruitInfo.getSalePrice().multiply(new BigDecimal(quantity));
            totalCostPrice = totalSalePrice.add(saleEle);


            BigDecimal costEle = fruitInfo.getCostPrice().multiply(new BigDecimal(quantity));
            totalCostPrice = totalCostPrice.add(costEle);

        }

        OrderInfo save = new OrderInfo();

        String orderCode = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
        save.setOrderCode(orderCode);
        save.setTodayStr(LocalDate.now().toString());
        save.setProductInfo(productInfo);
        save.setTotalSalePrice(totalSalePrice);
        save.setTotalCostPrice(totalCostPrice);
        long l = System.currentTimeMillis() / 1000;
        save.setCreateTime(l);
        this.getBaseMapper().insert(save);

    }



}
