package com.twb.wechatrobot.repository;

import java.util.List;
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
	
	@Query(value= "select b.* from wechat_message a,wechat_message b where a.msgid=:msgid and b.id>a.id-1000 and b.id<a.id+500 and b.message_type='1' and a.wxgroup_id=b.wxgroup_id and a.fromuser_id=b.fromuser_id and b.`timestamp`>SUBDATE(a.`timestamp`,interval 3 minute)  and b.`timestamp`<SUBDATE(a.`timestamp`,interval -3 minute) limit 0,5",nativeQuery = true)
	public List<WechatMessage> getMsgExt(@Param("msgid")String msgid); 
}
