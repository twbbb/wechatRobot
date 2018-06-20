package com.twb.wechatrobot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.twb.wechatrobot.entity.WechatGroupLog;



//继承JpaRepository来完成对数据库的操作
public interface WechatGroupLogRepository extends JpaRepository<WechatGroupLog,Integer>{
	
}
