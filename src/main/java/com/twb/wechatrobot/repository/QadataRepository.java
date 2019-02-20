package com.twb.wechatrobot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twb.wechatrobot.entity.AdMessage;
import com.twb.wechatrobot.entity.Qadata;



//继承JpaRepository来完成对数据库的操作
public interface QadataRepository extends JpaRepository<Qadata,Integer>{
	
	@Query(value="select o from Qadata o ")
	public List<Qadata> getAllQadata() throws Exception;
	
	@Modifying
	@Query("update Qadata am set am.answer = :answer where am.question = :question")
	public void updatePrice  (@Param(value = "answer") String answer,@Param(value = "question") String question);
	
	
}
