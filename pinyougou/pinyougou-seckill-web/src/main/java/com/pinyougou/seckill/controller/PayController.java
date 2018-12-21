package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
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
    private SeckillOrderService orderService;

    @Reference
    private WeixinPayService weixinPayService;

    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo) {
        Result result = Result.fail("支付失败");
        try {
            //1分钟若未支付成功则认为超时；返回支付超时
            int count = 0;
            while (true) {
                //1. 定时每隔3秒到微信支付系统查询支付状态；
                Map<String, String> resultMap = weixinPayService.queryPayStatus(outTradeNo);
                if (resultMap == null) {
                    break;
                }
                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    //2. 如果支付成功则更新订单信息；
                    orderService.saveOrderInRedisToDb(outTradeNo, resultMap.get("transaction_id"));
                    result = Result.ok("支付成功");
                    break;
                }

                count++;
                if (count > 20) {

                    //调用支付系统接口关闭微信订单
                    Map<String, String> map = weixinPayService.closeOrder(outTradeNo);

                    if (map != null && "ORDERPAID".equals(map.get("err_code"))) {
                        //说明订单在关闭的过程中被支付了，则也按照支付成功处理
                        orderService.saveOrderInRedisToDb(outTradeNo, resultMap.get("transaction_id"));
                        result = Result.ok("支付成功");
                        break;
                    }

                    //订单被关闭则需要将redis中的订单删除并加回库存
                    orderService.deleteSeckillOrderInRedis(outTradeNo);

                    result = Result.fail("支付超时");
                    break;
                }

                //每隔3秒
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //3. 返回结果
        return result;
    }

    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo) {
        try {
            //1、根据秒杀订单查询秒杀订单信息获取总金额
            TbSeckillOrder seckillOrder = orderService.findSeckillOrderByOutTradeNo(outTradeNo);
            //本次要支付的总金额，精确到分
            String totalFee = (long)(seckillOrder.getMoney().doubleValue()*100) + "";

            //2、调用支付业务方法获取返回信息
            return weixinPayService.createNative(outTradeNo, totalFee);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
