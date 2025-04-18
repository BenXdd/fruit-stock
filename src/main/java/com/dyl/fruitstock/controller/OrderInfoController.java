package com.dyl.fruitstock.controller;

import com.alibaba.fastjson.JSON;
import com.dyl.fruitstock.dto.login.ResultRsp;
import com.dyl.fruitstock.entity.OrderInfo;
import com.dyl.fruitstock.exception.BusinessException;
import com.dyl.fruitstock.service.IOrderInfoService;
import com.dyl.fruitstock.service.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
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
        }
        return ResultRsp.success(orderInfo);
    }

    /**
    * 通过条件查询
    */
    @GetMapping
    public ResultRsp<IPage<OrderInfo>>list(OrderInfo query, IPage<OrderInfo> page){
        return ResultRsp.success(orderInfoService.select(query,page));
    }

}
