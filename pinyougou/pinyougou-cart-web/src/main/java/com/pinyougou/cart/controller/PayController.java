package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference
    private OrderService orderService;

    @Reference
    private WeixinPayService weixinPayService;

    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo) {
        Result result = Result.fail("支付失败");

        try {
            int count = 0;
            while (true) {
                Map<String, String> resultMap = weixinPayService.queryPayStatus(outTradeNo);

                if (resultMap == null) {
                    break;
                }
                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    orderService.updateOrderStatus(outTradeNo, resultMap.get("transaction_id"));
                    result = Result.ok("支付成功");
                    break;
                }

                Thread.sleep(3000);

                count++;
                if (count > 60) {
                    result = Result.fail("支付超时");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo) {
        try {
            TbPayLog payLog = orderService.findPayLogByOutTradeNo(outTradeNo);
            String totalFee = payLog.getTotalFee().toString();

            return weixinPayService.createNative(outTradeNo, totalFee);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
