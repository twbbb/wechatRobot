package com.twb.wechatrobot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.twb.wechatrobot.entity.WechatGroupMembers;



//继承JpaRepository来完成对数据库的操作
public interface WechatGroupMembersRepository extends JpaRepository<WechatGroupMembers,Integer>,JpaSpecificationExecutor<WechatGroupMembers>{
	
}
