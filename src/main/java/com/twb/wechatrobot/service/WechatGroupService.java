package com.twb.wechatrobot.service;

import java.util.HashMap;

import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;

public interface WechatGroupService {
	
	void handleAllGroup(HashMap<String, WXGroup> wxGroupMap)throws Exception;
	void handleAddGroup(HashMap<String, WXGroup> wxGroupMap)throws Exception;

}
