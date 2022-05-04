package com.xinlvyao.passport.controller;

import com.xinlvyao.commons.SeckillResult;
import com.xinlvyao.passport.service.UserService;
import com.xinlvyao.pojo.TbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class PassportController {
    @Autowired
    private UserService userService;
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/user/login")
    public SeckillResult login(String username, String password, HttpSession session){
        TbUser user = userService.login(username, password);
        if (user!=null){
            session.setAttribute("loginUser",user);
            return SeckillResult.ok();
        }
        return SeckillResult.error("用户名或密码失败");
    }
}
