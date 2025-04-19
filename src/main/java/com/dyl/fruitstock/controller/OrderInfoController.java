package com.dyl.fruitstock.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dyl.fruitstock.dto.login.ResultRsp;
import com.dyl.fruitstock.entity.FruitInfo;
import com.dyl.fruitstock.entity.OrderInfo;
import com.dyl.fruitstock.exception.BusinessException;
import com.dyl.fruitstock.service.IFruitInfoService;
import com.dyl.fruitstock.service.IOrderInfoService;
import com.dyl.fruitstock.service.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (OrderInfo)表控制层
 *
 * @author makejava
 * @since 2025-04-18 19:51:11
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderInfoController {
    /**
     * 服务对象
     */
    @Resource
    private IOrderInfoService orderInfoService;
    @Resource
    private IFruitInfoService fruitInfoService;

    /**
      * 保存
      */
    @PostMapping("/submit")
    public ResultRsp<Object> submitOrder(@RequestBody OrderInfo orderInfo){
        log.info("#submitOrder req:{}", JSON.toJSONString(orderInfo));
        String orderCode = "";
        try {
            orderCode = orderInfoService.submitOrder(orderInfo);
        }catch (Exception e){
            if (e instanceof BusinessException){
                return ResultRsp.failure(400, e.getMessage());
            }else {
                return ResultRsp.failure(400, "系统正忙,请稍后重试");
            }
        }
        HashMap<String, String> resp = new HashMap<>();
        resp.put("orderCode",orderCode);
        return ResultRsp.success(resp);
    }

    /**
     * 更新
     */
    @PutMapping("{id}" )
    public ResultRsp<Void> update(@PathVariable("id" ) Integer id, @RequestBody OrderInfo orderInfo){
        orderInfo.setId(id);
        orderInfoService.updateById(orderInfo);
        return ResultRsp.success();
    }

    /**
    * 通过id查找
    */
    @GetMapping("{id}" )
    public ResultRsp<OrderInfo> findById(@PathVariable("id" ) Integer id){
        OrderInfo orderInfo = orderInfoService.getById(id);
        if(orderInfo == null){
            throw new RuntimeException();
        }return ResultRsp.success(orderInfo);
    }

    /**
    * 通过条件查询
    */
    @GetMapping
    public ResultRsp<IPage<OrderInfo>>list(OrderInfo query, IPage<OrderInfo> page){

        IPage<OrderInfo> select = orderInfoService.select(query, page);

        List<OrderInfo> data = select.getList();

        data.forEach(o ->{
            String productInfo = o.getProductInfo();
            List<OrderInfo.ProductItem> productItems = JSONArray.parseArray(productInfo, OrderInfo.ProductItem.class);
            List<OrderInfo.ProductItemVo> itemVos = new ArrayList<>();
            for (OrderInfo.ProductItem productItem : productItems) {
                OrderInfo.ProductItemVo itemVo = new OrderInfo.ProductItemVo();
                Integer productId = productItem.getProductId();
                FruitInfo byId = fruitInfoService.getById(productId);
                String name = byId.getName();
                itemVo.setQuantity(productItem.getQuantity());
                itemVo.setProductName(name);
                itemVos.add(itemVo);
            }
            o.setProductInfoList(itemVos);

        });
        select.setList(data);
        return ResultRsp.success(select);
    }

    @PutMapping("status")
    public ResultRsp<Void> status( @RequestBody OrderInfo orderInfo){
        orderInfo.setId(orderInfo.getId());
        orderInfo.setOrderStatus(orderInfo.getOrderStatus());
        orderInfoService.updateById(orderInfo);
        return ResultRsp.success();
    }

}
