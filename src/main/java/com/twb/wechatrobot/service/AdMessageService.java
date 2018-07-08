package com.twb.wechatrobot.service;

import java.util.Map;

import com.twb.wechatrobot.data.OutData;


public interface AdMessageService {
	
	//获取广告消息
	OutData getAdMsgRecord(Map inMap) throws Exception;
	//删除群成员
	OutData delMember(Map inMap) throws Exception;
	
	//删除群成员
	OutData notDel(Map inMap) throws Exception;
	
	//获取删除群成员记录
	OutData getDelMemberLog(Map inMap) throws Exception;
	

	//消息失效
	void msgOverdue() throws Exception;
	
}
