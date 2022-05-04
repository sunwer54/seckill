package com.xinlvyao.provider.service.impl;

import com.xinlvyao.pojo.TbItemSeckill;
import com.xinlvyao.provider.service.ItemSeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemSeckillServiceImpl implements ItemSeckillService {
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 从redis中获取各时间分段的秒杀商品
     * @param time
     * @return
     */
    @Override
    public List<TbItemSeckill> getEveryTimeGoods(String time) {
        return (List<TbItemSeckill>)redisTemplate.boundHashOps("seckillGoods_" + time).values();
    }
    /**
     * 从redis中获取商品的详情信息
     * @param time
     * @param id
     * @return
     */
    @Override
    public TbItemSeckill getGoodsItem(String time, long id) {
        return (TbItemSeckill)redisTemplate.boundHashOps("seckillGoods_" + time).get(id);
    }
}
