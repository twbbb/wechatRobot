package com.twb.wechatrobot.data;

import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twb.wechatrobot.service.WechatGroupService;
import com.twb.wechatrobot.service.WechatMessageService;

import me.xuxiaoxiao.chatapi.wechat.WeChatClient;
import me.xuxiaoxiao.chatapi.wechat.WeChatClient.WeChatListener;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXImage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXLink;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXText;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXVoice;

public class MyWeChatListener extends WeChatListener
{
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

	private static final Logger logger = LoggerFactory.getLogger(MyWeChatListener.class);

	WechatMessageService wechatMessageService;
	WechatGroupService wechatGroupService;

	public static WeChatClient wechatClient;

	@Override
	public void onModContact()
	{
		logger.info("onModContact");
		HashMap<String, WXGroup> wxGroupMap = wechatClient.userGroups();

		try
		{
			wechatGroupService.handleAddGroup(wxGroupMap);
		}
		catch (Exception e)
		{
			logger.error("群组添加处理失败！！");
			e.printStackTrace();
		}

		logger.info(String.format("onModContact：您有%d名好友、活跃微信群%d个", wechatClient.userFriends().size(),
				wechatClient.userGroups().size()));
	}

	@Override
	public void onQRCode(String qrCode)
	{
		logger.info("onQRCode：" + qrCode);
	}

	@Override
	public void onLogin()
	{
		HashMap<String, WXGroup> wxGroupMap = wechatClient.userGroups();

		try
		{
			wechatGroupService.handleAllGroup(wxGroupMap);
		}
		catch (Exception e)
		{
			logger.error("群组处理失败！！");
			e.printStackTrace();
		}

		logger.info(String.format("onLogin：您有%d名好友、活跃微信群%d个", wechatClient.userFriends().size(),
				wechatClient.userGroups().size()));
	}

	@Override
	public void onMessage(WXMessage message)
	{
		System.out.println(message.getClass().getName() + ",获取到消息：" + GSON.toJson(message));

		logger.info(message.getClass().getName() + ",获取到消息：" + GSON.toJson(message));
		try
		{
			if (message instanceof WXText)
			{
				WXText wxText = (WXText) message;
				wechatMessageService.handleWXText(wxText);
			}
			else if (message instanceof WXImage)
			{
				WXImage wxImage = (WXImage) message;
				wechatMessageService.handleWXImage(wxImage);
			}
			else if (message instanceof WXLink)
			{
				WXLink wxLink = (WXLink) message;
				wechatMessageService.handleWXLink(wxLink);
			}
			else if (message instanceof WXVoice)
			{
				WXVoice wxVoice = (WXVoice) message;
				wechatClient.fetchVoice(wxVoice);
				wechatMessageService.handleWXVoice(wxVoice);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("数据库存入失败", e);
		}
	}

	public MyWeChatListener(WechatMessageService wechatMessageService, WechatGroupService wechatGroupService)
	{
		super();
		this.wechatMessageService = wechatMessageService;
		this.wechatGroupService = wechatGroupService;
	}

	public void onLogout()
	{
		
	}
}
