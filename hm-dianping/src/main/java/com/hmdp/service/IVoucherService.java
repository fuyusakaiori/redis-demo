package com.hmdp.service;

import com.hmdp.dto.Response;
import com.hmdp.entity.Voucher;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IVoucherService extends IService<Voucher> {

    Response queryVoucherOfShop(Long shopId);

    void addSeckillVoucher(Voucher voucher);
}
