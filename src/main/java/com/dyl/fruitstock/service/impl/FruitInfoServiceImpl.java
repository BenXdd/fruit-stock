package com.dyl.fruitstock.service.impl;

import com.dyl.fruitstock.entity.FruitInfo;
import com.dyl.fruitstock.mapper.FruitInfoMapper;
import com.dyl.fruitstock.service.IFruitInfoService;
import org.springframework.stereotype.Service;

/**
 * (FruitInfo)表服务实现类
 *
 * @author makejava
 * @since 2025-04-14 16:25:34
 */
@Service("fruitInfoService")
public class FruitInfoServiceImpl extends BaseServiceImpl<FruitInfoMapper, FruitInfo, Integer> implements IFruitInfoService {
    public FruitInfoServiceImpl(FruitInfoMapper fruitInfoMapper) {
        super(fruitInfoMapper);
    }
}
