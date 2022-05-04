package com.xinlvyao.api;

import com.xinlvyao.pojo.TbUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserServiceApi {
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/user/login")
    public TbUser login(@RequestParam("username") String username, @RequestParam("password") String password);
}
