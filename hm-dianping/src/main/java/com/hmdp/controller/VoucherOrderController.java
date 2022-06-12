package com.hmdp.controller;


import com.hmdp.dto.Response;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {
    @PostMapping("seckill/{id}")
    public Response seckillVoucher(@PathVariable("id") Long voucherId) {
        return Response.fail("功能未完成");
    }
}
