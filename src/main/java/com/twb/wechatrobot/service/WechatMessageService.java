package com.twb.wechatrobot.service;

import com.twb.wechatrobot.entity.WechatMessage;

import me.xuxiaoxiao.chatapi.wechat.entity.message.WXImage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXLink;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXText;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXVoice;

public interface WechatMessageService {
	
//	List<WechatMessage> getTranFromJingtong(String address,String lastHash) throws Exception;
	
	WechatMessage handleWXText(WXText wxText)throws Exception;
	WechatMessage handleWXImage(WXImage wxImage)throws Exception;
	WechatMessage handleWXLink(WXLink wxLink)throws Exception;
	WechatMessage handleWXVoice(WXVoice wxVoice)throws Exception;
	
	WechatMessage saveMessage(WXMessage message)throws Exception;

}
