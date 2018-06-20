package com.twb.wechatrobot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.twb.wechatrobot.entity.WechatUser;



//继承JpaRepository来完成对数据库的操作
public interface WechatUserRepository extends JpaRepository<WechatUser,Integer>{
	

}
