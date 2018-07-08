package com.twb.wechatrobot.service;

import java.util.Map;

import com.twb.wechatrobot.data.OutData;


public interface WechatGroupMessageService {
	
	//获取群发群群消息
	OutData getGroupMsgRecord(Map inMap) throws Exception;
	//发送群消息
	OutData sendGroupMsg(Map inMap) throws Exception;
	//获取发送历史记录
	OutData getGroupMsgLog(Map inMap) throws Exception;
	

}
