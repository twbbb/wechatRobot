package com.twb.wechatrobot.service;

import me.xuxiaoxiao.chatapi.wechat.entity.message.WXImage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXLink;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXText;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXVoice;

public interface WechatMessageService {
	
//	List<WechatMessage> getTranFromJingtong(String address,String lastHash) throws Exception;
	
	void handleWXText(WXText wxText)throws Exception;
	void handleWXImage(WXImage wxImage)throws Exception;
	void handleWXLink(WXLink wxLink)throws Exception;
	void handleWXVoice(WXVoice wxVoice)throws Exception;

}
