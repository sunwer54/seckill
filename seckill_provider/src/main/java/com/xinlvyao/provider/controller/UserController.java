package com.xinlvyao.provider.controller;

import com.xinlvyao.api.UserServiceApi;
import com.xinlvyao.pojo.TbUser;
import com.xinlvyao.provider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserServiceApi {
    @Autowired
    private UserService userService;
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public TbUser login(String username, String password) {
        return userService.login(username, password);
    }
}
