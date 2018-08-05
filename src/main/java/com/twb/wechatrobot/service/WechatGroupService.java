package com.twb.wechatrobot.service;

import java.util.HashMap;

import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;

public interface WechatGroupService {
	
	void handleAllGroup(HashMap<String, WXGroup> wxGroupMap)throws Exception;
	void deleteAllGroup()throws Exception;
	void handleAddGroup(HashMap<String, WXGroup> wxGroupMap)throws Exception;
	
	void totalGroupMember(HashMap<String, WXGroup> wxGroupMap)throws Exception;

	public void wechatUserDeleteAll() throws Exception;
	
	public void handleAllGroupSaveDb() throws Exception;
}
