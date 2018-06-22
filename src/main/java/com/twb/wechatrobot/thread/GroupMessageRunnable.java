package com.twb.wechatrobot.thread;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.data.MessageGroup;
import com.twb.wechatrobot.data.MyWeChatListener;
import com.twb.wechatrobot.utils.GroupMessageQueue;

import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;



public class GroupMessageRunnable implements Runnable
{

	private static final Logger logger = LoggerFactory.getLogger(GroupMessageRunnable.class);

	private String groupmessage_flag;

	@Override
	public void run()
	{
		logger.info("线程:" + Thread.currentThread().getName() + "运行中.....");
		while (true)
		{
			try
			{
				MessageGroup mg = GroupMessageQueue.get();
				String content = mg.getContent();
				if(StringUtils.isEmpty(content))
				{
					continue;
				}
				String flag = "测试";
				//如果不是群发开头，则直接回复相同消息
				if(!content.startsWith(flag))
				{
					content = content.replaceFirst(flag, "").replace("<br/>", "\r\n");
					String id = mg.getId();
					String name = mg.getGroupName();
					if(!StringUtils.isEmpty(name)&&name.equals(groupmessage_flag)&&!StringUtils.isEmpty(id))
					{
						logger.info("返回消息:"+name+"，"+id);
						MyWeChatListener.wechatClient.sendText(MyWeChatListener.wechatClient.userContact(id), content);
						Thread.sleep((long)(15000*Math.random()+3000));
					}
						
					continue;
				}
				
				flag = "群发";
				content = content.replaceFirst(flag, "").replace("<br/>", "\r\n");
				if(StringUtils.isEmpty(content))
				{
					continue;
				}
				
				logger.info("发送消息内容："+content);
				HashMap<String, WXGroup>  wxgroupMap = MyWeChatListener.wechatClient.userGroups();
				for (Entry<String, WXGroup> wxgroupEntry : wxgroupMap.entrySet())
				{
					WXGroup wxgroup = wxgroupEntry.getValue();
					String name = wxgroup.name;
					String id = wxgroup.id;
					if(!StringUtils.isEmpty(name)&&!name.equals(groupmessage_flag)&&!StringUtils.isEmpty(id))
					{
						logger.info("发送消息:"+name+"，"+id);
						MyWeChatListener.wechatClient.sendText(MyWeChatListener.wechatClient.userContact(id), content);
						Thread.sleep((long)(15000*Math.random()+3000));
					}
					
				}
				
			}
			catch (Exception e)
			{
				logger.error("error.." + e.toString() + "," + Arrays.toString(e.getStackTrace()));
				e.printStackTrace();
			}
		}

	}

	public GroupMessageRunnable(String groupmessage_flag)
	{
		super();
		this.groupmessage_flag = groupmessage_flag;
	}

	
	

}
