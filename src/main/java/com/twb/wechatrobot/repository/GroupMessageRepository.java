package com.twb.wechatrobot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.twb.wechatrobot.entity.GroupMessage;



//继承JpaRepository来完成对数据库的操作
public interface GroupMessageRepository extends JpaRepository<GroupMessage,Integer>,JpaSpecificationExecutor<GroupMessage>{
	
}
