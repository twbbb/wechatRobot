package com.twb.wechatrobot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twb.wechatrobot.entity.WechatMessage;
import com.twb.wechatrobot.entity.WechatQaMessage;



//继承JpaRepository来完成对数据库的操作
public interface WechatQaMessageRepository extends JpaRepository<WechatQaMessage,Integer>{
	
	@Query(value="select * from wechat_qa_message a order by a.id limit 1",nativeQuery = true)
	public WechatQaMessage getWqm() throws Exception;
	
	@Modifying
	@Query(value=
	"insert into wechat_qa_message_his ( " +
	"wechat_qa_message_his.id, " + 
	"wechat_qa_message_his.msgid, " + 
	"wechat_qa_message_his.`timestamp`, " + 
	"wechat_qa_message_his.wxgroup_id, " + 
	"wechat_qa_message_his.wxgroup_name, " + 
	"wechat_qa_message_his.fromuser_id, " + 
	"wechat_qa_message_his.fromuser_name, " + 
	"wechat_qa_message_his.content_text, " + 
	"wechat_qa_message_his.answer_flag, " + 
	"wechat_qa_message_his.answer_time, " + 
	"wechat_qa_message_his.content_file, " + 
	"wechat_qa_message_his.content_link, " + 
	"wechat_qa_message_his.message_type," +
	"wechat_qa_message_his.ext_message"
	+ ") " + 
	"SELECT " + 
	"wechat_qa_message.id, " + 
	"wechat_qa_message.msgid, " + 
	"wechat_qa_message.`timestamp`, " + 
	"wechat_qa_message.wxgroup_id, " + 
	"wechat_qa_message.wxgroup_name, " + 
	"wechat_qa_message.fromuser_id, " + 
	"wechat_qa_message.fromuser_name, " + 
	"wechat_qa_message.content_text, " + 
	"'3', " + 
	"SYSDATE(), " + 
	"wechat_qa_message.content_file, " + 
	"wechat_qa_message.content_link, " + 
	"wechat_qa_message.message_type, " +
	"wechat_qa_message.ext_message " + 
	"FROM " + 
	"wechat_qa_message",nativeQuery = true)
	public void moveToHis() throws Exception;
	
	

}
