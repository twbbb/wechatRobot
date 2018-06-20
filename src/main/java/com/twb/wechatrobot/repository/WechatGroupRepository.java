package com.twb.wechatrobot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.twb.wechatrobot.entity.WechatGroup;



//继承JpaRepository来完成对数据库的操作
public interface WechatGroupRepository extends JpaRepository<WechatGroup,Integer>{
	
	@Query(value="select o from WechatGroup o ")
	public List<WechatGroup> getAllWechatGroup() throws Exception;
	
}
