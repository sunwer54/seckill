package com.xinlvyao.provider.service;

import com.xinlvyao.commons.SeckillStatus;

public interface ItemOrderSeckillService {
    /**
     * 抢单处理
     * @param time
     * @param id
     * @param username
     * @return
     */
    public boolean addOrder(String time, long id, String username);
    /**
     * 查询订单状态，1排队等待抢单，2抢单成功，等待支付
     * @return
     */
    public SeckillStatus queryOrderStatus(String username);
}
