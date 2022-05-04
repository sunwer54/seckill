package com.xinlvyao.provider.service.impl;

import com.xinlvyao.commons.SeckillStatus;
import com.xinlvyao.provider.multithreadtask.AsyncCreateOrderTask;
import com.xinlvyao.provider.service.ItemOrderSeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class ItemOrderSeckillServiceImpl implements ItemOrderSeckillService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AsyncCreateOrderTask asyncCreateOrderTask;

    /**
     * 抢单处理
     * @param time
     * @param id
     * @param username
     * @return
     */
    @Override
    public boolean addOrder(String time, long id, String username) {
        /*为了避免重复抢单，所以首先判断用户是否已抢过单。
        以"UserOrderCount"为key，使用hash(username,count)在redis中记录用户的抢单的状态。:
        increment(username, 1)方法，对field是username的value值执行+1，并返回+1后的value的结果count
        count>1,则说明该用户已经抢过一次单，不可重复抢单
        count=1,则说明该用户是第一次抢单，可以继续抢单*/
        long count = redisTemplate.boundHashOps("UserOrderCount").increment(username, 1);
        if (count>1){
            return false;
        }

        /*把抢单的请求封装成SeckillStatus类对象，暂时将抢单请求存入redis中，等待创建订单
          存储抢单请求使用list类型，因为要保证用户抢单的顺序性，使用list才可以的模拟队列的先进先出。
          以key："userOrderQueue"，value：seckillStatus 把订单请求信息存入redis中*/
        //把抢单请求封装成SeckillStatus对象，status=1表示等待抢单
        SeckillStatus seckillStatus = new SeckillStatus(username,new Date(),1,id,time);
        //把抢单请求存入redis中
        redisTemplate.boundListOps("userOrderQueue").leftPush(seckillStatus);
        //把订单状态存入redis中key："userOrderStatus"，field：username，value：seckillStatus
        redisTemplate.boundHashOps("userOrderStatus").put(username,seckillStatus);

        /*使用多线程异步执行抢单操作（从redis中取出userOrderQueue的最右边的seckillStatus）并更新redis中的
        订单状态信息"userOrderStatus"key："userOrderStatus"，field：username，value：seckillStatus*/
        asyncCreateOrderTask.createOrder();
        return true;
    }
    /**
     * 查询订单状态，1排队等待抢单，2抢单成功，等待支付
     * @return
     */
    @Override
    public SeckillStatus queryOrderStatus(String username) {
        return (SeckillStatus)redisTemplate.boundHashOps("userOrderStatus").get(username);
    }
}
