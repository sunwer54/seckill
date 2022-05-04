package com.xinlvyao.provider.service.impl;

import com.xinlvyao.mapper.TbUserMapper;
import com.xinlvyao.pojo.TbUser;
import com.xinlvyao.pojo.TbUserExample;
import com.xinlvyao.provider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TbUserMapper userMapper;
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public TbUser login(String username, String password) {
        TbUserExample exp = new TbUserExample();
        exp.createCriteria().andUsernameEqualTo(username).andPasswordEqualTo(password);
        List<TbUser> users = userMapper.selectByExample(exp);
        if (users!=null&&users.size()>0){
            return users.get(0);
        }
        return null;
    }
}
