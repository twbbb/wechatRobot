package com.twb.wechatrobot.service;

import com.twb.wechatrobot.data.OutData;

public interface WechatGroupShowService {
	
	OutData getAllGroup(String page,String pagesize) throws Exception;

	OutData getGroupRecord(String groupName)throws Exception;

	OutData getGrpUserRecord(String groupName)throws Exception;

	OutData downloadGrpUserRecord(String groupName)throws Exception;
	

}
