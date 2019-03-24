package com.twb.wechatrobot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twb.wechatrobot.entity.DcPublic;



//继承JpaRepository来完成对数据库的操作
public interface DcPublicRepository extends JpaRepository<DcPublic,Integer>{

	@Query(value="select o from DcPublic o where  key = :key")
	public DcPublic getValue(@Param("key") String key) throws Exception;
	
	@Query(value="select value from DcPublic o where  key = :key")
	public String getStrValue(@Param("key") String key) throws Exception;
	
}
