package com.twb.wechatrobot.service.msghandler.imp;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twb.wechatrobot.data.MessageGroup;
import com.twb.wechatrobot.entity.Qadata;
import com.twb.wechatrobot.repository.QadataRepository;
import com.twb.wechatrobot.service.msghandler.MessageHandler;
import com.twb.wechatrobot.thread.MyWeChatListener;
import com.twb.wechatrobot.utils.QAMessageQueue;

import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXText;

@Service
public class QaMsgHandler implements MessageHandler
{

//	@Value("${ROBOT_NAME}")
//	private String robot_name;

	@Autowired
	QadataRepository qadataRepository;

	private Logger logger = LoggerFactory.getLogger(GroupMsgHandler.class);

	public static Map<String,String> qaMap = new ConcurrentHashMap<String,String>();
	@PostConstruct
	public void init()
	{
		logger.info("QaMsgHandler start");
		List<Qadata> list= null;
		try
		{
			list= qadataRepository.getAllQadata();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(list!=null&&list.size()>0)
		{
			for(Qadata qadata:list)
			{
				qaMap.put(qadata.getQuestion().trim(), qadata.getAnswer().trim());
			}
		}
	}
	
	@Override
	public void handleMsg(WXMessage message) throws Exception
	{

		// 是群消息
		if (message.fromGroup == null)
		{
			return;
		}
//是文本消息
		if (!(message instanceof WXText))
		{
			return;
		}
		//是自己发送的消息
		if (message.fromGroup == null||message.fromUser==null||message.fromUser.id.equals(MyWeChatListener.wechatClient.userMe().id))
		{
			return ;
		}
		
		WXText wxText = (WXText) message;
		String content = wxText.content.replace("<br/>", "");
		//是@机器人消息
//		if(!content.contains("@"+robot_name))
//		{
//			return;
//		}
//		String noticeStr = " ";
//		content = content.replace("@"+robot_name, "").replace(noticeStr, "").trim();
		if(qaMap.containsKey(content))
		{
			
			String fromuser = wxText.fromUser.name;
			MessageGroup mg = new MessageGroup();
			mg.setContent(content+"\r\n"+qaMap.get(content));
			mg.setGroupName(wxText.fromGroup.name);
			mg.setId(wxText.fromGroup.id);
			QAMessageQueue.add(mg);
		}
		
//		for (Entry<String, String> entry : qaMap.entrySet())
//		{
//			String question = entry.getKey();
//			String answer = entry.getValue();
//			if(content.contains(question))
//			{
//				String fromuser = wxText.fromUser.name;
//				MessageGroup mg = new MessageGroup();
//				mg.setContent("@"+fromuser+" "+answer);
//				mg.setGroupName(wxText.fromGroup.name);
//				mg.setId(wxText.fromGroup.id);
//				QAMessageQueue.add(mg);
//			}
//		}
	}

	

}
