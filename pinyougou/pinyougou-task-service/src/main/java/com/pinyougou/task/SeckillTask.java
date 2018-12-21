package com.pinyougou.task;

import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillTask {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String SECKILL_GOODS = "SECKILL_GOODS";

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Scheduled(cron = "0/3 * * * * ?")
    public void refreshSeckillGoods() {
        //1. 查询在redis中的那些商品id集合；
        Set set = redisTemplate.boundHashOps(SECKILL_GOODS).keys();
        List<Long> idList = new ArrayList<>(set);

        //2. 查询符合条件的数据
        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("status", "1");
        criteria.andGreaterThan("stockCount", 0);
        criteria.andLessThanOrEqualTo("startTime", new Date());
        criteria.andGreaterThan("endTime", new Date());
        criteria.andNotIn("id", idList);

        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);

        //3. 逐个遍历商品；更新商品
        if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
            for (TbSeckillGoods seckillGoods : seckillGoodsList) {
                redisTemplate.boundHashOps(SECKILL_GOODS).put(seckillGoods.getId(), seckillGoods);
            }
            System.out.println("更新了 " + seckillGoodsList.size() + " 条秒杀商品到缓存中...");
        }
    }

    @Scheduled(cron = "0/2 * * * * ?")
    public void removeSeckillGoods() {
        //1、获取当前redis中的秒杀列表；
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps(SECKILL_GOODS).values();
        if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
            //2、逐个遍历每个商品，判断结束时间是否小于等于当前时间
            for (TbSeckillGoods seckillGoods : seckillGoodsList) {
                //3、小于等于当前时间删除redis该商品并将该商品更新回mysql
                if (seckillGoods.getEndTime().getTime() <= System.currentTimeMillis()) {
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);

                    redisTemplate.boundHashOps(SECKILL_GOODS).delete(seckillGoods.getId());

                    System.out.println("已经将秒杀商品id为 " + seckillGoods.getId() + " 对应的商品从redis中移除了。");
                }
            }
        }
    }
}
