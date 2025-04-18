package com.dyl.fruitstock.controller;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.dyl.fruitstock.dto.login.ResultRsp;
import com.dyl.fruitstock.entity.FruitInfo;
import com.dyl.fruitstock.entity.Page;
import com.dyl.fruitstock.service.IFruitInfoService;
import com.dyl.fruitstock.service.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * (FruitInfo)表控制层
 *
 * @author makejava
 * @since 2025-04-14 16:25:34
 */

@Slf4j
@RestController
@RequestMapping("/api/fruits")
public class FruitInfoController {
    /**
     * 服务对象
     */
    @Resource
    private IFruitInfoService fruitInfoService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
      * 保存
      */
    @PostMapping
    public ResultRsp<Map<String, Integer>> addFruit(
            @RequestParam("name") String name,
            @RequestParam("stock") Integer stock,
            @RequestParam("status") String status,
            @RequestParam("salePrice") BigDecimal salePrice,
            @RequestParam("costPrice") BigDecimal costPrice,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        String imgUrl = saveImage(image);
        FruitInfo fruit = new FruitInfo();
        fruit.setName(name);
        fruit.setStock(stock);
        fruit.setStatus(status);
        fruit.setImgUrl(imgUrl);
        fruit.setSalePrice(salePrice);
        fruit.setCostPrice(costPrice);
        fruitInfoService.save(fruit);
        HashMap<String, Integer> map = new HashMap<>();
        map.put("id", fruit.getId());
        return ResultRsp.success(map);
    }

    /**
     * 更新
     */
    @PutMapping("{id}" )
    public ResultRsp<Void> update(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("stock") Integer stock,
            @RequestParam("status") String status,
            @RequestParam("salePrice") BigDecimal salePrice,
            @RequestParam("costPrice") BigDecimal costPrice,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        FruitInfo fruitDb = fruitInfoService.getById(id);
        if (Objects.isNull(fruitDb)){
            return ResultRsp.failure(400,"商品不存在");
        }

        String newImgUrl = image != null ? saveImage(image) : fruitDb.getImgUrl();
        FruitInfo fruit = new FruitInfo();
        fruit.setId(id);
        fruit.setName(name);
        fruit.setStock(stock);
        fruit.setStatus(status);
        fruit.setImgUrl(newImgUrl);
        fruit.setSalePrice(salePrice);
        fruit.setCostPrice(costPrice);

        fruitInfoService.updateById(fruit);
        return ResultRsp.success();
    }



    @PutMapping("/stock")
    public ResultRsp<Void> stock(@RequestBody FruitInfo fruitInfo)  {
        log.info("#stock:{}",JSON.toJSONString(fruitInfo));

        FruitInfo fruitDb = fruitInfoService.getById(fruitInfo.getId());
        if (Objects.isNull(fruitDb)){
            return ResultRsp.failure(400,"商品不存在");
        }

        Integer reqStock = fruitInfo.getStock();
        Integer dbStock = fruitDb.getStock();
        if (reqStock < 0 && (dbStock+reqStock<0)){
            return ResultRsp.failure(400,"库存已最低");
        }


        FruitInfo fruit = new FruitInfo();
        fruit.setId(fruitDb.getId());
        fruit.setStock(fruitDb.getStock() + fruitInfo.getStock());

        fruitInfoService.updateById(fruit);
        return ResultRsp.success();
    }

    /**
    * 通过id查找
    */
    @GetMapping("{id}" )
    public ResultRsp<FruitInfo> findById(@PathVariable("id" ) Integer id){
        FruitInfo fruitInfo = fruitInfoService.getById(id);
        if(fruitInfo == null){
            throw new RuntimeException();
        }
        return ResultRsp.success(fruitInfo);
    }

    /**
    * 通过条件查询
    */
    @GetMapping
    public ResultRsp<IPage<FruitInfo>>list(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) String status,
                                           @RequestParam(required = false) Integer minStock,
                                           @RequestParam(required = false) Integer maxStock){

        FruitInfo query = FruitInfo.builder().name(name).status(status).build();
        Page<FruitInfo> fruitInfoPage = new Page<>();
        fruitInfoPage.setPage(page);
        fruitInfoPage.setSize(size);
        return ResultRsp.success(fruitInfoService.select(query,fruitInfoPage));
    }



    private String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return "/placeholder.png";
        }

        // 验证文件类型
        String contentType = image.getContentType();
        if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType) && !"image/jpg".equals(contentType)) {
            throw new IOException("只支持 PNG/JPG 图片");
        }

        // 生成文件名
        String originalName = image.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 开发环境：保存到 src/main/resources/static/images/
        // 生产环境：可配置为 /app/images/
        String basePath = new File(uploadDir).getAbsolutePath();
        log.info("basePath:{}",basePath);
        String path = basePath + File.separator + fileName;
        File dest = new File(path);
        log.info("dest:{}",path);

        // 确保目录存在
        File parentDir = dest.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 保存文件
        image.transferTo(dest);
        log.info("fileName:{}",fileName);

        // 返回 URL 路径
        return "/" + fileName;
    }

}
