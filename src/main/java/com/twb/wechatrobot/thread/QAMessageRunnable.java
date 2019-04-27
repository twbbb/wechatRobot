package com.twb.wechatrobot.thread;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.data.MessageGroup;
import com.twb.wechatrobot.utils.CommonUtils;
import com.twb.wechatrobot.utils.QAMessageQueue;

public class QAMessageRunnable implements Runnable
{

	private Logger logger = LoggerFactory.getLogger(QAMessageRunnable.class);


	@Override
	public void run()
	{
		logger.info("QAMessageRunnable 线程:" + Thread.currentThread().getName() + "运行中.....");
		while (true)
		{
			try
			{
				
				MessageGroup mg = QAMessageQueue.get();
				//如果群发消息在发消息，睡眠
				while(GroupMessageRunnable.flag)
				{
					Thread.sleep((long) (GroupMessageRunnable.timedelay * Math.random() + 3000));
				}
				String content = CommonUtils.htmlReplace(mg.getContent());
				File file = mg.getFile();
				if(!StringUtils.isEmpty(content))
				{
					MyWeChatListener.wechatClient.sendText(MyWeChatListener.wechatClient.userContact(mg.getId()),
							content);
					Thread.sleep((long) (GroupMessageRunnable.timedelay * Math.random() + 3000));
				}
				else if(file!=null&&file.exists())
				{
					MyWeChatListener.wechatClient.sendFile(MyWeChatListener.wechatClient.userContact(mg.getId()), file);
					Thread.sleep((long) (GroupMessageRunnable.timedelay * Math.random() + 3000));
				}
				
				

			}
			catch (Exception e)
			{
				logger.error("QAMessageRunnable ,error.." ,e);
				e.printStackTrace();
			}
		}

	}

}
