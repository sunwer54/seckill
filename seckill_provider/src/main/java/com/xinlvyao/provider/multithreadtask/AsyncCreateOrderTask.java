package com.xinlvyao.provider.multithreadtask;

import com.alibaba.fastjson.JSON;
import com.xinlvyao.commons.IDUtils;
import com.xinlvyao.commons.SeckillStatus;
import com.xinlvyao.mapper.TbItemSeckillMapper;
import com.xinlvyao.pojo.TbItemSeckill;
import com.xinlvyao.pojo.TbOrderSeckill;
import com.xinlvyao.send.sender.Sender;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 多线程异步任务类，创建订单
 */
@Component
public class AsyncCreateOrderTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbItemSeckillMapper itemSeckillMapper;
    @Autowired
    private Sender sender;
    /**
     * @Async这是spring中用来定义异步任务的注解
     * @Async修饰类: 该类下所有的方法都是异步调用的
     * @Async修改方法: 这个方法是异步调用(在一个新的线程中执行)
     */
    @Async
    public void createOrder(){
        /*把redis(key为"userOrderQueue",value：List)中的抢单请求从List右边取出依次执行订单的
          创建，并把订单存入redis(key:"orderSeckill",field:username,value:orderSeckill)
          并更新redis中"userOrderStatus"的抢单的状态信息*/
        //1.从redis中取出抢单请求(从右边取出),拿到SeckillStatus抢单请求对象
        SeckillStatus seckillStatus = (SeckillStatus)redisTemplate.boundListOps("userOrderQueue").rightPop();
        //2.订单创建
        //2.1先从seckillStatus中获取到订单参数
        String startTime = seckillStatus.getTime();
        long goodsId = seckillStatus.getGoodsId();
        String username = seckillStatus.getUsername();
        //2.2从redis中获取到下单的商品信息
        Object o = redisTemplate.boundListOps("goodsList" + goodsId).rightPop();
        if (o == null){
            //商品已卖完，清除redis中关于该商品的信息
            //清除该商品信息
            redisTemplate.boundHashOps("seckillGoods_"+startTime).delete(goodsId);
            //清除该商品列表信息
            redisTemplate.delete("goodsList" + goodsId);
            //清除当前用户的订单记录信息
            redisTemplate.boundHashOps("UserOrderCount").delete(username);
            //清除当前用户的排队状态信息
            redisTemplate.boundHashOps("userOrderStatus").delete(username);
            return;
        }
        TbItemSeckill itemSeckill = (TbItemSeckill)redisTemplate.boundHashOps("seckillGoods_" + startTime).get(goodsId);
        System.out.println("itemSeckill:"+itemSeckill);
        //2.3判断商品是否还有库存，还有库存才能下单
        if (itemSeckill!=null&&itemSeckill.getStockCount()>0){
            //2.4执行订单创建
            TbOrderSeckill orderSeckill = new TbOrderSeckill();
            orderSeckill.setId(IDUtils.getItemId());
            orderSeckill.setSeckillId(goodsId);
            orderSeckill.setMoney(itemSeckill.getCostPrice());
            orderSeckill.setUserId(username);
            orderSeckill.setCreateTime(new Date());
            orderSeckill.setStatus("0");//订单状态：0未支付，1已支付
            //2.5把订单存入redis中
            redisTemplate.boundHashOps("orderSeckill").put(username,orderSeckill);
            //2.6用户抢单成功后，执行redis中商品的库存扣减
            itemSeckill.setStockCount(itemSeckill.getStockCount()-1);
            //2.6.1库存>0,把商品信息更新到redis中
            if (itemSeckill.getStockCount() > 0){
                redisTemplate.boundHashOps("seckillGoods_" + startTime).put(goodsId,itemSeckill);
            }else {
                //2.6.2库存=0,则删除redis中该商品的信息，并同步更新到mysql中
                //a.删除redis中该商品信息
                redisTemplate.boundHashOps("seckillGoods_" + startTime).delete(goodsId);
                //b.同步更新到mysql中
                itemSeckillMapper.updateByPrimaryKeySelective(itemSeckill);
            }
            //2.7更新redis中抢单状态
            seckillStatus.setMoney(itemSeckill.getCostPrice().floatValue());//商品秒杀价
            seckillStatus.setOrderId(IDUtils.getItemId());//订单id
            seckillStatus.setStatus(2);//更新抢单状态，2是已抢单等待支付
            redisTemplate.boundHashOps("userOrderStatus").put(seckillStatus.getUsername(),seckillStatus);
            System.out.println("抢单成功，等待支付");
            //2.8发送抢单成功，等待支付的消息到延时消息队列
            String orderMsg = JSON.toJSONString(seckillStatus);
            //通过expiration设置消息的过期时间（即设置了单条消息过期时间，又设置了队列过期时间，则二者取其短）
            MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setExpiration("10000");//设置这条消息的过期时间
                    message.getMessageProperties().setContentEncoding("utf-8");//设置编码
                    return message;
                }
            };
            sender.sendMsg(orderMsg,messagePostProcessor);
            System.out.println("消息发送到队列成功");
        }
    }
}
