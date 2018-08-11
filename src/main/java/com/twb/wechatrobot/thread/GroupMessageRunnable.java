package com.twb.wechatrobot.thread;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.data.MessageGroup;
import com.twb.wechatrobot.utils.GroupMessageQueue;

public class GroupMessageRunnable implements Runnable
{

	private Logger logger = LoggerFactory.getLogger(GroupMessageRunnable.class);


	public static volatile boolean flag = false;//发送消息标志位
	
	public static final int timedelay = 10000;//发送消息标志位
	
	@Override
	public void run()
	{
		logger.info("线程:" + Thread.currentThread().getName() + "运行中.....");
		while (true)
		{
			try
			{
				flag = false;
				MessageGroup mg = GroupMessageQueue.get();
				flag = true;
				String content = mg.getContent();
				
				File file = mg.getFile();
				if(!StringUtils.isEmpty(content))
				{
					MyWeChatListener.wechatClient.sendText(MyWeChatListener.wechatClient.userContact(mg.getId()),
							content);
					Thread.sleep((long) (timedelay * Math.random() + 3000));
				}
				else if(file!=null&&file.exists())
				{
					MyWeChatListener.wechatClient.sendFile(MyWeChatListener.wechatClient.userContact(mg.getId()), file);
					Thread.sleep((long) (timedelay * Math.random() + 3000));
				}
				
				

			}
			catch (Exception e)
			{
				logger.error("GroupMessageRunnable ,error.." ,e);
				e.printStackTrace();
				try {
					logger.error("GroupMessageQueue.clear:"+GroupMessageQueue.size());
					GroupMessageQueue.clear();
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}

	}

}
