package com.twb.wechatrobot.utils;

import java.util.concurrent.LinkedBlockingQueue;

import com.twb.wechatrobot.data.MessageGroup;


public class QAMessageQueue
{
	private static LinkedBlockingQueue<MessageGroup> GROUPMESSAGE_QUEUE = new LinkedBlockingQueue<MessageGroup>();
	private static int max = 10000;
	public static void add(MessageGroup obj) throws InterruptedException
	{
		if(GROUPMESSAGE_QUEUE.size()<max)
		{
			GROUPMESSAGE_QUEUE.put(obj);
		}
		
	}
	
	public static MessageGroup get() throws InterruptedException
	{
		return GROUPMESSAGE_QUEUE.take();
	}
}
