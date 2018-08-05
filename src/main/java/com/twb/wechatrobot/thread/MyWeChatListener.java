package com.twb.wechatrobot.thread;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twb.wechatrobot.service.AdMessageService;
import com.twb.wechatrobot.service.WechatGroupService;
import com.twb.wechatrobot.service.WechatMessageService;
import com.twb.wechatrobot.service.msghandler.MessageHandler;

import me.xuxiaoxiao.chatapi.wechat.WeChatClient;
import me.xuxiaoxiao.chatapi.wechat.WeChatClient.WeChatListener;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;

@Component
public class MyWeChatListener extends WeChatListener
{
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

	private static final Logger logger = LoggerFactory.getLogger(MyWeChatListener.class);

	
	@Autowired
	WechatMessageService wechatMessageServiceImp;
	@Autowired
	WechatGroupService wechatGroupServiceImp;
	@Autowired
	List<MessageHandler> messageHandlerList;

	@Autowired
	AdMessageService adMessageService;
	
	public static WeChatClient wechatClient;

	public static volatile boolean finish = false;

	@Override
	public void onModContact()
	{
		logger.info("onModContact");
		HashMap<String, WXGroup> wxGroupMap = wechatClient.userGroups();

		try
		{
			if (finish)
			{
				wechatGroupServiceImp.handleAddGroup(wxGroupMap);
			}

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
		logger.info("======================onQRCode========================");
		logger.info(qrCode);
		logger.info("=======================onQRCode=======================");
		logger.info("=======================onQRCode=======================");
		logger.info(qrCode);
		logger.info("=======================onQRCode=======================");
		
	}

	@Override
	public void onLogin()
	{
		logger.info("onLogin");
		
//		try
//		{
//			//之前广告消息失效
//			adMessageService.msgOverdue();
//		}
//		catch (Exception e1)
//		{
//			logger.error("广告消息失效处理失败",e1);
//			e1.printStackTrace();
//		}

		try
		{
			
			Thread thread = new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					try
					{
						HashMap<String, WXGroup> wxGroupMap = wechatClient.userGroups();
						Thread.sleep(30000);// 睡眠30秒，等待更新群组事件结束
						wechatGroupServiceImp.deleteAllGroup();
						wechatGroupServiceImp.handleAllGroup(wxGroupMap);
						finish = true;
						Thread.sleep(10000);
						//删除用户重复统计表数据
						wechatGroupServiceImp.wechatUserDeleteAll();
						//插入数据
						Thread.sleep(30000);
						wechatGroupServiceImp.handleAllGroupSaveDb();
					}
					catch (Exception e)
					{
						logger.error("群组处理失败2！！",e);
						e.printStackTrace();
					}
				}

			});
			thread.start();

		}
		catch (Exception e)
		{
			logger.error("群组处理失败！！",e);
			e.printStackTrace();
		}
		
	
		logger.info(String.format("onLogin：您有%d名好友、活跃微信群%d个", wechatClient.userFriends().size(),
				wechatClient.userGroups().size()));
	}

	@Override
	public void onMessage(WXMessage message)
	{

//		logger.info(message.getClass().getName() + ",获取到消息：" + GSON.toJson(message));
		try
		{
			// 保存信息
			wechatMessageServiceImp.saveMessage(message);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("数据库存入失败", e);
		}

		for (MessageHandler mh : messageHandlerList)
		{
			try
			{
				mh.handleMsg(message);
			}
			catch (Exception e)
			{
				logger.error("消息处理失败:" + mh.getClass().getSimpleName() + "," + message.id, e);
			}
		}

	}


}
