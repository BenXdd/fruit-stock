package com.dyl.fruitstock.controller;

import com.dyl.fruitstock.dto.login.ResultRsp;
import com.dyl.fruitstock.entity.FruitInfo;
import com.dyl.fruitstock.entity.Page;
import com.dyl.fruitstock.service.IFruitInfoService;
import com.dyl.fruitstock.service.IPage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("font")
public class FontIndexController {


    @Resource
    private IFruitInfoService fruitInfoService;


    @GetMapping("/product/list")
    public ResultRsp<IPage<FruitInfo>> list(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String name){

        FruitInfo query = FruitInfo.builder().name(name).status("1").build();
        Page<FruitInfo> fruitInfoPage = new Page<>();
        fruitInfoPage.setPage(page);
        fruitInfoPage.setSize(size);
        return ResultRsp.success(fruitInfoService.select(query,fruitInfoPage));
    }



}
