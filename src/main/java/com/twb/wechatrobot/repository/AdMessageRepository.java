package com.twb.wechatrobot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twb.wechatrobot.entity.AdMessage;



//继承JpaRepository来完成对数据库的操作
public interface AdMessageRepository extends JpaRepository<AdMessage,Integer>,JpaSpecificationExecutor<AdMessage>{
	
	@Modifying
	@Query("update AdMessage am set am.deleteState = "+AdMessage.DELETE_STATE_DELETE+" where am.wxgroupId = :wxgroupId and am.fromuserId=:fromuserId and am.deleteState = "+AdMessage.DELETE_STATE_DEFUALT)
	public void updateDeleteState(@Param(value = "wxgroupId") String groupId,@Param(value = "fromuserId")String memberId);
	
	@Modifying
	@Query("update AdMessage am set am.isOverdue = "+AdMessage.ISOVERDUE+" where am.isOverdue = "+AdMessage.ISOVERDUE_NO)
	public void updateIsOverdue  ();
}
