package com.twb.wechatrobot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.twb.wechatrobot.entity.GroupMessageLog;



//继承JpaRepository来完成对数据库的操作
public interface GroupMessageLogRepository extends JpaRepository<GroupMessageLog,Integer>,JpaSpecificationExecutor<GroupMessageLog>{
	
}
