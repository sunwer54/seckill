package com.xinlvyao.provider.controller;

import com.xinlvyao.api.ItemSeckillServiceApi;
import com.xinlvyao.commons.SeckillStatus;
import com.xinlvyao.pojo.TbItemSeckill;
import com.xinlvyao.provider.service.ItemOrderSeckillService;
import com.xinlvyao.provider.service.ItemSeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeckillController implements ItemSeckillServiceApi {
    @Autowired
    private ItemSeckillService itemSeckillService;

    @Autowired
    private ItemOrderSeckillService itemOrderSeckillService;
    /**
     * 获取各时间分段的秒杀商品
     * @param time
     * @return
     */
    @Override
    public List<TbItemSeckill> getEveryTimeGoods(String time) {
        return itemSeckillService.getEveryTimeGoods(time);
    }

    /**
     * 从redis中获取商品的详情信息
     * @param time
     * @param id
     * @return
     */
    @Override
    public TbItemSeckill getGoodsItem(String time, long id) {
        return itemSeckillService.getGoodsItem(time, id);
    }

    /**
     * 抢单处理
     * @param time
     * @param id
     * @param username
     * @return
     */
    @Override
    public boolean addOrder(String time, long id, String username) {
        return itemOrderSeckillService.addOrder(time, id, username);
    }
    /**
     * 查询订单状态，1排队等待抢单，2抢单成功，等待支付
     * @return
     */
    @Override
    public SeckillStatus queryOrderStatus(String username){
        return itemOrderSeckillService.queryOrderStatus(username);
    }
}
