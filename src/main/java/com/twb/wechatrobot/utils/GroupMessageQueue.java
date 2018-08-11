package com.twb.wechatrobot.utils;

import java.util.concurrent.LinkedBlockingQueue;

import com.twb.wechatrobot.data.MessageGroup;


public class GroupMessageQueue
{
	private static LinkedBlockingQueue<MessageGroup> GROUPMESSAGE_QUEUE = new LinkedBlockingQueue<MessageGroup>();
	
	public static void add(MessageGroup obj) throws InterruptedException
	{
		GROUPMESSAGE_QUEUE.put(obj);
	}
	
	public static MessageGroup get() throws InterruptedException
	{
		return GROUPMESSAGE_QUEUE.take();
	}
	
	public static void clear() 
	{
		GROUPMESSAGE_QUEUE.clear();
	}
	
	public static int size() 
	{
		return GROUPMESSAGE_QUEUE.size();
	}
	
	public static boolean isEmpty() 
	{
		return GROUPMESSAGE_QUEUE.isEmpty();
	}
}
