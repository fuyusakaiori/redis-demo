package com.hmdp.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.dto.Response;
import com.hmdp.entity.Shop;
import com.hmdp.service.IShopService;
import com.hmdp.util.SystemConstants;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    public IShopService shopService;

    /**
     * 根据id查询商铺信息
     * @param id 商铺id
     * @return 商铺详情数据
     */
    @GetMapping("/{id}")
    public Response queryShopById(@PathVariable("id") Long id) {
        return Response.ok(shopService.getById(id));
    }

    /**
     * 新增商铺信息
     * @param shop 商铺数据
     * @return 商铺id
     */
    @PostMapping
    public Response saveShop(@RequestBody Shop shop) {
        // 写入数据库
        shopService.save(shop);
        // 返回店铺id
        return Response.ok(shop.getId());
    }

    /**
     * 更新商铺信息
     * @param shop 商铺数据
     * @return 无
     */
    @PutMapping
    public Response updateShop(@RequestBody Shop shop) {
        // 写入数据库
        shopService.updateById(shop);
        return Response.ok();
    }

    /**
     * 根据商铺类型分页查询商铺信息
     * @param typeId 商铺类型
     * @param current 页码
     * @return 商铺列表
     */
    @GetMapping("/of/type")
    public Response queryShopByType(
            @RequestParam("typeId") Integer typeId,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        // 根据类型分页查询
        Page<Shop> page = shopService.query()
                .eq("type_id", typeId)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
        // 返回数据
        return Response.ok(page.getRecords());
    }

    /**
     * 根据商铺名称关键字分页查询商铺信息
     * @param name 商铺名称关键字
     * @param current 页码
     * @return 商铺列表
     */
    @GetMapping("/of/name")
    public Response queryShopByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        // 根据类型分页查询
        Page<Shop> page = shopService.query()
                .like(StrUtil.isNotBlank(name), "name", name)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 返回数据
        return Response.ok(page.getRecords());
    }
}
