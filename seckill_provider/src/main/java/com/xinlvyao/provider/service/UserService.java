package com.xinlvyao.provider.service;

import com.xinlvyao.pojo.TbUser;

public interface UserService {
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    public TbUser login(String username, String password);
}
