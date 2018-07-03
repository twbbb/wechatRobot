package com.twb.wechatrobot.repository;

import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twb.wechatrobot.entity.WechatMessage;



//继承JpaRepository来完成对数据库的操作
public interface WechatMessageRepository extends JpaRepository<WechatMessage,Integer>,JpaSpecificationExecutor<WechatMessage>{
	
	@Query(value="select count(1) from wechat_message WHERE TO_DAYS( NOW( ) ) - TO_DAYS(timestamp) =1 and wxgroup_name=:groupName",nativeQuery = true)
	public int countMessage(@Param("groupName") String groupName ) throws Exception;
}
