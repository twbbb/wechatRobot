package com.twb.wechatrobot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.twb.wechatrobot.entity.AdKey;
import com.twb.wechatrobot.entity.WechatGroup;



//继承JpaRepository来完成对数据库的操作
public interface AdKeyRepository extends JpaRepository<AdKey,Integer>{

	@Query(value="select keyword from AdKey o ")
	public List<String> getKeyword() throws Exception;
	
}
