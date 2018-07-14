package com.twb.wechatrobot.service.msghandler.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.entity.AdMessage;
import com.twb.wechatrobot.entity.WechatUser;
import com.twb.wechatrobot.repository.AdKeyRepository;
import com.twb.wechatrobot.repository.AdMessageRepository;
import com.twb.wechatrobot.service.impl.WechatGroupServiceImp;
import com.twb.wechatrobot.service.msghandler.MessageHandler;
import com.twb.wechatrobot.thread.MyWeChatListener;

import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXText;

@Service
public class AdMsgHandler implements MessageHandler
{

	@Autowired
	AdKeyRepository adKeyRepository;
	@Autowired
	AdMessageRepository adMessageRepository;
	private Logger logger = LoggerFactory.getLogger(AdMsgHandler.class);

	public static List<String> adList = new ArrayList();
	private Pattern pattern;

	
	/**
	 * 获取指定字符串出现的次数
	 * 
	 * @param srcText 源字符串
	 * @param findText 要查找的字符串
	 * @return
	 */
	public int appearNumber(String srcText, Pattern p)
	{

		int count = 0;
		// Pattern p = Pattern.compile(findText);
		Matcher m = p.matcher(srcText);
		while (m.find())
		{
			count++;
		}
		return count;
	}

	@Override
	public void handleMsg(WXMessage message) throws Exception
	{
		// 是群消息,并且不是自己发送的消息
		if (message.fromGroup == null||message.fromUser==null||message.fromUser.id.equals(MyWeChatListener.wechatClient.userMe().id))
		{
			return;
		}
		// 是文本消息
		if (!(message instanceof WXText))
		{
			return;
		}
//		//是群主
//		if(!message.fromGroup.isOwner)
//		{
//			return;
//		}
		
		if (adList == null || adList.isEmpty())
		{
			adList = adKeyRepository.getKeyword();
			StringBuffer findText = new StringBuffer();
			if (adList != null)
			{
				for (int i = 0; i < adList.size(); i++)
				{
					String str = adList.get(i);
					if (i != 0)
					{
						findText.append("|");
					}
					findText.append(str);
				}
			}

			if (findText.length() == 0)
			{
				findText.append("羊毛");
			}
			pattern = Pattern.compile(findText.toString());
		}
		WXText wxText = (WXText) message;
		String content = wxText.content.replace("<br/>", "\r\n");
		int adNum = appearNumber(content, pattern);
		if(adNum>=2)
		{
			AdMessage am = new AdMessage();
			saveCommonData(wxText,am);
			am.setMessageType(AdMessage.MESSAGETYPE_TEXT);
			am.setContentText(wxText.content);
			adMessageRepository.save(am);
		}
		
		
	}
	
	private void saveCommonData(WXMessage wxMessage, AdMessage wm)
	{
		wm.setTimestamp(new Date(wxMessage.timestamp));
		
		wm.setMsgid(wxMessage.id + "");
		if (wxMessage.fromGroup != null)
		{
			if(wxMessage.fromGroup.isOwner)
			{
				wm.setIsowner(AdMessage.ISOWNER);
			}
			else
			{
				wm.setIsowner(AdMessage.ISOWNER_NO);
			}
			wm.setWxgroupId(wxMessage.fromGroup.id);
			wm.setWxgroupName(wxMessage.fromGroup.name);
			if (wxMessage.fromUser != null)
			{
				wm.setFromuserId(wxMessage.fromUser.id);
				if (!StringUtils.isEmpty(wxMessage.fromUser.name))
				{
					wm.setFromuserName(wxMessage.fromUser.name);
				}
				else
				{
					WXGroup.Member member = wxMessage.fromGroup.members.get(wxMessage.fromUser.id);
					if (member != null)
					{
						wm.setFromuserId(member.id);
						if (StringUtils.isEmpty(member.display))
						{
							wm.setFromuserName(member.name);
						}
						else
						{
							wm.setFromuserName(member.display);
						}
					}
				}

			}
			else
			{
				int index = wxMessage.content.indexOf(":");
				if (index > 0)
				{
					String userid = wxMessage.content.substring(0, index);
					String content = wxMessage.content.substring(index + 1);
					WXGroup.Member member = wxMessage.fromGroup.members.get(userid);
					if (member != null)
					{
						wm.setFromuserId(member.id);
						if (StringUtils.isEmpty(member.display))
						{
							wm.setFromuserName(member.name);
						}
						else
						{
							wm.setFromuserName(member.display);
						}
						if(content.startsWith("<br/>"))
						{
							content = content.replaceFirst("<br/>", "");
						}
						wxMessage.content = content;

					}
					else
					{
						logger.error("发送用户获取失败。。" + userid);
					}

				}

			}
		}
		else if (wxMessage.fromUser != null)
		{
			if (!StringUtils.isEmpty(wxMessage.fromUser.name))
			{
				wm.setFromuserName(wxMessage.fromUser.name);
			}
			else
			{
				WechatUser wu = WechatGroupServiceImp.userMap.get(wxMessage.fromUser.id);
				if (wu != null)
				{
					wm.setFromuserName(wu.getUserName());
				}
			}

		}
	}

}
