package com.twb.wechatrobot.service;

import com.twb.wechatrobot.entity.UserInfo;

public interface UserInfoService {
    /**通过username查找用户信息;*/
    public UserInfo findByUsername(String username);
}