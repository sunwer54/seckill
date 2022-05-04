package com.xinlvyao.receiver;

import com.alibaba.fastjson.JSON;
import com.xinlvyao.commons.SeckillStatus;
import com.xinlvyao.mapper.TbItemSeckillMapper;
import com.xinlvyao.pojo.TbItemSeckill;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbItemSeckillMapper itemSeckillMapper;
    /**
     * 接收死信队列中的消息，对过期未支付的订单执行回滚操作
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "dlx_queue"),
    exchange = @Exchange(value = "direct_exchange"),key = "dlx.ttl.key"))
    public void RollBackOrder(String msg){
        System.out.println("接收到消息："+msg);
        /*由于该死信队列中都是过期未支付的订单的消息，所以需要对产生该订单的操作执行回滚
        1.redis中恢复商品库存，即对key:"seckillGoods_"+time,field:goodsId恢复库存，
        2.删除redis中该用户的抢单记录，即删除key："UserOrderCount",field:username
        3.删除redis中用户的抢单状态，即删除key:"userOrderStatus",field:username
        4.删除redis中的用户已创建的订单，即删除key:"orderSeckill",field:username
        库存恢复的特殊情况:由于当redis中的商品库存为0后删除了redis中该商品数据，并同步更
        新到了mysql，所以在对商品库存回滚时，需要对这个情况进行特殊处理*/
        //把接收到的消息转化为SeckillStatus对象
        SeckillStatus seckillStatus = JSON.parseObject(msg, SeckillStatus.class);
        //1.库存回滚
        //商品所在秒杀时间段
        String time = seckillStatus.getTime();
        //商品id
        Long goodsId = seckillStatus.getGoodsId();
        //用户名
        String username = seckillStatus.getUsername();
        //获取商品
        TbItemSeckill itemSeckill = (TbItemSeckill)redisTemplate.boundHashOps("seckillGoods_" +time).get(goodsId);
        //如果itemSeckill不为null，说明redis中还存在该商品，则说明商品还有库存，执行库存加1，再存入redis即可
        if (itemSeckill!=null){
            itemSeckill.setStockCount(itemSeckill.getStockCount()+1);
        }else {
            //如果itemSeckill为null，说明商品库存已为0，则需要在mysql中恢复商品库存,重新添加到redis中
            itemSeckill = itemSeckillMapper.selectByPrimaryKey(goodsId);
            itemSeckill.setStockCount(itemSeckill.getStockCount()+1);
            itemSeckillMapper.updateByPrimaryKeySelective(itemSeckill);
        }
        redisTemplate.boundHashOps("seckillGoods_" +time).put(goodsId,itemSeckill);
        //2.删除redis中该用户的抢单记录
        redisTemplate.boundHashOps("UserOrderCount").delete(username);
        //3.删除redis中用户的抢单状态
        redisTemplate.boundHashOps("userOrderStatus").delete(username);
        //4.删除redis中的用户已创建的订单
        redisTemplate.boundHashOps("orderSeckill").delete(username);
        System.out.println("超时订单处理完毕");
    }
}
