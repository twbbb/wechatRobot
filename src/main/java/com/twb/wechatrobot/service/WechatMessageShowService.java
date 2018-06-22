package com.twb.wechatrobot.service;

import java.util.Map;

import com.twb.wechatrobot.data.OutData;

public interface WechatMessageShowService {
	
	OutData getMsg(Map inMap) throws Exception;
	

}
