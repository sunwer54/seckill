package com.xinlvyao.provider.service;

import com.xinlvyao.pojo.TbItemSeckill;

import java.util.List;

public interface ItemSeckillService {
    /**
     * 从redis中获取各时间分段的秒杀商品
     * @param time
     * @return
     */
    public List<TbItemSeckill> getEveryTimeGoods(String time);
    /**
     * 从redis中获取商品的详情信息
     * @param time
     * @param id
     * @return
     */
    public TbItemSeckill getGoodsItem(String time, long id);
}
