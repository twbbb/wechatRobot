package com.twb.wechatrobot.task;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.twb.wechatrobot.thread.GroupMessageRunnable;
import com.twb.wechatrobot.thread.QAMessageRunnable;

@Component
public class GroupMessageTask
{
	Logger logger = LoggerFactory.getLogger(GroupMessageTask.class);


	@Value("${GROUPMESSAGE_FLAG}")
	private String groupmessage_flag;
	 
	// 定义在构造方法完毕后，执行这个初始化方法
	@PostConstruct
	public void init()
	{
		logger.info("GroupMessageTask start");
		GroupMessageRunnable gr = new GroupMessageRunnable();
		Thread thread = new Thread(gr);
		thread.setDaemon(true);
		thread.start();
		
		QAMessageRunnable qr = new QAMessageRunnable();
		Thread thread2 = new Thread(qr);
		thread2.setDaemon(true);
		thread2.start();
		
	}
	
}
