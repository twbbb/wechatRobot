package com.twb.wechatrobot.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.twb.wechatrobot.dao.UserInfoDao;
import com.twb.wechatrobot.entity.UserInfo;
import com.twb.wechatrobot.service.UserInfoService;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private UserInfoDao userInfoDao;
    @Override
    public UserInfo findByUsername(String username) {
        System.out.println("UserInfoServiceImpl.findByUsername()");
        return userInfoDao.findByUsername(username);
    }
}