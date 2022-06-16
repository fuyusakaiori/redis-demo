package com.hmdp.service;

import com.hmdp.dto.Response;
import com.hmdp.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author 冬坂五百里
 * @since 2022-06-15
 */
public interface IShopService extends IService<Shop> {

     Response getShopById(Long id);

     Response updateShopById(Shop shop);
}
