package com.xinlvyao.web.service;

import com.xinlvyao.api.ItemSeckillServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("provider-service")
public interface ItemSeckillService extends ItemSeckillServiceApi {
}
