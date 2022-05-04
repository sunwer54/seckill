package com.xinlvyao.api;

import com.xinlvyao.commons.SeckillStatus;
import com.xinlvyao.pojo.TbItemSeckill;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ItemSeckillServiceApi {
    /**
     * 获取各时间分段的秒杀商品
     * @param time
     * @return
     */
    @RequestMapping("/seckill/goods/list")
    public List<TbItemSeckill> getEveryTimeGoods(@RequestParam("time") String time);

    /**
     * 获取商品的详情信息
     * @param time
     * @param id
     * @return
     */
    @GetMapping("/seckill/goods/selOne")
    public TbItemSeckill getGoodsItem(@RequestParam("time") String time,@RequestParam("id") long id);
    /**
     * 抢单处理
     * @param time
     * @param id
     * @param username
     * @return
     */
    @GetMapping("/seckill/order/add")
    public boolean addOrder(@RequestParam("time") String time, @RequestParam("id") long id,@RequestParam("username") String username);

    /**
     * 查询订单状态，1排队等待抢单，2抢单成功，等待支付
     * @return
     */
    @RequestMapping("/seckill/order/query")
    public SeckillStatus queryOrderStatus(@RequestParam("username") String username);
}
