package com.xinlvyao.web.controller;

import com.xinlvyao.commons.DateUtil;
import com.xinlvyao.commons.Result;
import com.xinlvyao.commons.SeckillStatus;
import com.xinlvyao.pojo.TbItemSeckill;
import com.xinlvyao.pojo.TbUser;
import com.xinlvyao.web.service.ItemSeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@RestController
public class SeckillWebController {
    /**
     * 查询时间菜单，获取秒杀的时间分段：
     * 共五个时间分段，每2个小时为一个时间分段
     * @return
     */
    @RequestMapping("seckill/goods/menus")
    public List<Date> getSeckillTime(){
        List<Date> dateMenus = DateUtil.getDateMenus();
        return dateMenus;
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @PostMapping("/getuser")
    public String getUser(HttpSession session){
        TbUser user = (TbUser)session.getAttribute("loginUser");
        if (user != null){
            return user.getUsername();
        }
        return null;
    }

    @Autowired
    private ItemSeckillService itemSeckillService;
    /**
     * 获取各时间分段的秒杀商品
     * @param time
     * @return
     */
    @RequestMapping("/seckill/goods/list")
    public List<TbItemSeckill> getEveryTimeGoods(String time){
        return itemSeckillService.getEveryTimeGoods(time);
    }

    /**
     * 获取商品的详情信息
     * @param time
     * @param id
     * @return
     */
    @RequestMapping("/seckill/goods/selOne")
    public TbItemSeckill getGoodsItem(String time,long id,HttpSession session){
        return itemSeckillService.getGoodsItem(time, id);
    }
    /**
     * 抢单处理
     * @param time
     * @param id
     * @param session
     * @return
     */
    @RequestMapping("/seckill/order/add")
    public Result addOrder(String time, long id, HttpSession session){
        //首先判断用户登录状态
        TbUser user = (TbUser)session.getAttribute("loginUser");
        boolean order = false;
        if (user!=null){
            //用户已登录，进行抢单的下一步操作
            order = itemSeckillService.addOrder(time, id, user.getUsername());
        }else {
            //用户未登录
            return new Result(403,"请登录后再抢单");
        }
        if (order){
            return new Result(0,"抢单成功");
        }
        return new Result(100,"100");
    }

    /**
     * 查询订单状态，1排队等待抢单，2抢单成功，等待支付
     * @return
     */
    @RequestMapping("/seckill/order/query")
    public Result queryOrderStatus(HttpSession session){
        TbUser user = (TbUser) session.getAttribute("loginUser");
        if (user!=null){
            SeckillStatus seckillStatus = itemSeckillService.queryOrderStatus(user.getUsername());
            return new Result(seckillStatus.getStatus(),"抢单成功，即将跳转支付页面");
        }
        return new Result(403,"您未登录，请登录");
    }
}
