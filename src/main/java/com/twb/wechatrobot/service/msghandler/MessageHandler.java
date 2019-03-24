package com.twb.wechatrobot.service.msghandler;

import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;

public interface MessageHandler {
	
	
	void handleMsg(WXMessage wm)throws Exception;

	void init( ) throws Exception ;
}
