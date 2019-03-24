package com.twb.wechatrobot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.twb.wechatrobot.entity.WechatQaMessageHis;



//继承JpaRepository来完成对数据库的操作
public interface WechatQaMessageHisRepository extends JpaRepository<WechatQaMessageHis,Integer>{
	

}
