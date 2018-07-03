package com.twb.wechatrobot.task;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.twb.wechatrobot.service.WechatGroupService;
import com.twb.wechatrobot.thread.MyWeChatListener;

import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;

@Component
public class GroupTask
{
	Logger logger = LoggerFactory.getLogger(GroupTask.class);

	 
	@Autowired
	WechatGroupService wechatGroupServiceImp;
	@Scheduled(cron = "1 0 0 * * ?")
	public void task()
	{

		logger.info("GroupTask.task start");
		try
		{
			wechatGroupServiceImp.totalGroupMember(MyWeChatListener.wechatClient.userGroups());
		}
		catch (Exception e)
		{

			logger.error("GroupTask ,error.." ,e);
			e.printStackTrace();
		}

		logger.info("GroupTask.task end");

		//同步所有群数据到数据库
		MyWeChatListener.finish=false;
		
		try
		{
			HashMap<String, WXGroup> wxGroupMap = MyWeChatListener.wechatClient.userGroups();
			wechatGroupServiceImp.deleteAllGroup();
			wechatGroupServiceImp.handleAllGroup(wxGroupMap);
		}
		catch (Exception e)
		{
			logger.error("handleAllGroup ,error.." ,e);
		}
		MyWeChatListener.finish = true;
	}
	
}
