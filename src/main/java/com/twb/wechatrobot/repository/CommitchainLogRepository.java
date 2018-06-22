package com.twb.wechatrobot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.twb.wechatrobot.entity.CommitchainLog;



//继承JpaRepository来完成对数据库的操作
public interface CommitchainLogRepository extends JpaRepository<CommitchainLog,Integer>{
	

}
