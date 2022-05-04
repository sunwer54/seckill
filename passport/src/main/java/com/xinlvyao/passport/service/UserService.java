package com.xinlvyao.passport.service;

import com.xinlvyao.api.UserServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("provider-service")
public interface UserService extends UserServiceApi {
}
