package com.twb.wechatrobot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.twb.wechatrobot.entity.Qadata;



//继承JpaRepository来完成对数据库的操作
public interface QadataRepository extends JpaRepository<Qadata,Integer>{
	
	@Query(value="select o from Qadata o ")
	public List<Qadata> getAllQadata() throws Exception;
	
	
	
	
}
