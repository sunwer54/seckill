package com.xinlvyao.provider.timer;

import com.xinlvyao.commons.DateUtil;
import com.xinlvyao.mapper.TbItemSeckillMapper;
import com.xinlvyao.pojo.TbItemSeckill;
import com.xinlvyao.pojo.TbItemSeckillExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 定时执行任务，定时从数据库中查询每个秒杀时间段内的商品，并存入redis
 */
@Component
public class TimerTask {
    @Autowired
    private TbItemSeckillMapper itemSeckillMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     *  @Scheduled (cron = "0 * * * * ?")定时任务
     *  定时时间格式:[秒][分][小时][日][月][周][年]
     *  通配符说明:
     *  * 表示所有值. 例如:在分的字段上设置 "*",表示每一分钟都会触发。
     *  ? 表示不指定值。使用的场景为不需要关心当前设置这个字段的值。
     *  例如:要在每月的10号触发一个操作，但不关心是周几，所以需要周位置的那个字段设置为"?" 具体设置为 0 0 0 10 * ?
     *  - 表示区间。例如 在小时上设置 "10-12",表示 10,11,12点都会触发。
     *  , 表示指定多个值，例如在周字段上设置 "MON,WED,FRI" 表示周一，周三和周五触发
     *  / 用于递增触发。如在秒上面设置"5/15" 表示从5秒开始，每增15秒触发(5,20,35,50)。 在月字段上设置'1/3'所示每
     *  月1号开始，每隔三天触发一次。
     *  L 表示最后的意思。在日字段设置上，表示当月的最后一天(依据当前月份，如果是二月还会依据是否是润年[leap]), 在周
     *  字段上表示星期六，相当于"7"或"SAT"。如果在"L"前加上数字，则表示该数据的最后一个。例如在周字段上设置"6L"这样
     *  的格式,则表示“本月最后一个星期五"
     *  W 表示离指定日期的最近那个工作日(周一至周五). 例如在日字段上设置"15W"，表示离每月15号最近的那个工作日触发。
     */
    @Scheduled(cron = "0 * * * * ?")
    public void getSeckillGoods(){
        System.out.println("执行定时任务查询商品");
        //1.首先获取各秒杀世间分段
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date startTime:dateMenus){
            /*
              封装符合秒杀的商品的条件：
              条件1：商品审核通过的，即status=1
              条件2：商品库存>0的，即stock_count>0
              条件3：商品的秒杀起始时间点大于等于秒杀活动的起始时间点的，即start_time>=startTime
              条件4：商品的秒杀结束时间点小于等于秒杀活动的结束时间点的，即end_time<=startTime+2
              条件5：该商品在redis中不存在的
            */
            TbItemSeckillExample exp = new TbItemSeckillExample();
            TbItemSeckillExample.Criteria criteria = exp.createCriteria();
            //1.商品必须是通过了审核的。即status = 1
            criteria.andStatusEqualTo("1");
            //2.库存必须>0。即stock_count>0
            criteria.andStockCountGreaterThan(0);
            //3.秒杀的起始时间必须>=当前的时间分段起始时间点。即start_time>=startTime
            criteria.andStartTimeGreaterThanOrEqualTo(startTime);
            //4.秒杀的结束时间必须<=当前的时间分段结束时间点。即：end_time<=startTime+2
            criteria.andEndTimeLessThanOrEqualTo(DateUtil.addDateHour(startTime,2));
            //5.如果redis中已经存在该时间分段内的该秒杀商品，则不用重复查询
            String key = "seckillGoods_"+DateUtil.date2Str(startTime);
            Set fields = redisTemplate.boundHashOps(key).keys();
            if (fields!=null&&fields.size()>0){
                criteria.andIdNotIn(new ArrayList<>(fields));
            }
            //执行查询
            List<TbItemSeckill> tbItemSeckills = itemSeckillMapper.selectByExample(exp);
            System.out.println("查询到当前时间分段-"+startTime+"-的秒杀商品-"+tbItemSeckills);
            //把符合条件的秒杀商品存入redis缓存
            for(TbItemSeckill tbItemSeckill:tbItemSeckills){
                redisTemplate.boundHashOps(key).put(tbItemSeckill.getId(),tbItemSeckill);

                /*为了解决商品超卖问题，以商品的id为key，以List<id>为value把每种商品中的每个
                商品存入redis中。创建订单时，从该redis中使用rightPop取商品，当该redis中的value
                为null时，即说明商品已售罄，则无法再继续创建订单，从而避免商品超卖*/
                long[] goodsIds = getGoodsIds(tbItemSeckill.getStockCount(), tbItemSeckill.getId());
                redisTemplate.boundListOps("goodsList"+tbItemSeckill.getId()).leftPushAll(goodsIds);
            }
        }
    }
    /**
     * 解决商品超卖问题
     * @param stockCount ：商品库存
     * @param goodsId ：商品id
     * @return
     */
    public long[] getGoodsIds(int stockCount,long goodsId){
        long[] ids = new long[stockCount];
        for (int i=0;i<stockCount;i++){
            ids[i] = goodsId;
        }
        return ids;
    }
}
