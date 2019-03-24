package com.twb.wechatrobot.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.entity.WechatMessage;
import com.twb.wechatrobot.entity.WechatUser;
import com.twb.wechatrobot.repository.WechatMessageRepository;
import com.twb.wechatrobot.service.MqProductService;
import com.twb.wechatrobot.service.WechatMessageService;
import com.twb.wechatrobot.thread.MyWeChatListener;

import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXImage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXLink;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXText;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXVoice;

@Service
public class WechatMessageServiceImp implements WechatMessageService
{

	private static final Logger logger = LoggerFactory.getLogger(WechatMessageServiceImp.class);

	@Autowired
	private WechatMessageRepository wechatMessageRepository;
	
	
	@Autowired
	MqProductService mqProductServiceImp;

	@Value("${file_dir}")
	private String file_dir;
	
	

	
	
	private void saveCommonData(WXMessage wxMessage, WechatMessage wm)
	{
		wm.setTimestamp(new Date(wxMessage.timestamp));
		wm.setMsgid(wxMessage.id + "");
		if (wxMessage.fromGroup != null)
		{
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
			wm.setFromuserId(wxMessage.fromUser.id);
			if(!StringUtils.isEmpty(wxMessage.fromUser.name))
			{
				wm.setFromuserName(wxMessage.fromUser.name);
			}
			else
			{
				WechatUser wu = WechatGroupServiceImp.userMap.get(wxMessage.fromUser.id);
				if(wu!=null)
				{
					wm.setFromuserName(wu.getUserName());
				}
			}
			
		}
	}

	@Override
	public WechatMessage handleWXText(WXText wxText) throws Exception
	{
		WechatMessage wm = new WechatMessage();
		saveCommonData(wxText, wm);
		wm.setMessageType(WechatMessage.MESSAGETYPE_TEXT);
		wm.setContentText(wxText.content);
		return wechatMessageRepository.save(wm);

	}

	@Override
	public WechatMessage handleWXImage(WXImage wxImage) throws Exception
	{
		WechatMessage wm = new WechatMessage();
		saveCommonData(wxImage, wm);
		wm.setMessageType(WechatMessage.MESSAGETYPE_IMAGE);
		if(wxImage.image!=null)
		{
			wm.setContentFile(wxImage.image.getPath().replace(file_dir, ""));	
		}
		
		return wechatMessageRepository.save(wm);
	}

	@Override
	public WechatMessage handleWXLink(WXLink wxLink) throws Exception
	{
		WechatMessage wm = new WechatMessage();
		saveCommonData(wxLink, wm);
		wm.setMessageType(WechatMessage.MESSAGETYPE_LINK);
		wm.setContentText(wxLink.linkName);
		wm.setContentLink(wxLink.linkUrl);
		return  wechatMessageRepository.save(wm);
	}

	@Override
	public WechatMessage handleWXVoice(WXVoice wxVoice) throws Exception
	{
		WechatMessage wm = new WechatMessage();
		saveCommonData(wxVoice, wm);
		wm.setMessageType(WechatMessage.MESSAGETYPE_VOICE);
		// wm.setContentText(wxVoice.);
		 wm.setContentFile(wxVoice.voice.getPath().replace(file_dir, ""));
//		 wechatClient.fetchVoice((WXVoice) message);
		 return wechatMessageRepository.save(wm);
	}
	public String toString(Object obj)
	{
		if (obj == null)
		{
			return "";
		}
		else if (obj instanceof Timestamp || obj instanceof Date)
		{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(obj);
		}
		else
		{
			return obj.toString();
		}

	}

	@Override
	public WechatMessage saveMessage(WXMessage message) throws Exception
	{
		WechatMessage wm = null;
		if (message instanceof WXText)
		{
			WXText wxText = (WXText) message;
			wm =this.handleWXText(wxText);
		}
		else if (message instanceof WXImage)
		{
			WXImage wxImage = (WXImage) message;
			wm =this.handleWXImage(wxImage);
		}
		else if (message instanceof WXLink)
		{
			WXLink wxLink = (WXLink) message;
			wm =this.handleWXLink(wxLink);
		}
		else if (message instanceof WXVoice)
		{
			WXVoice wxVoice = (WXVoice) message;
			MyWeChatListener.wechatClient.fetchVoice(wxVoice);
			wm =this.handleWXVoice(wxVoice);
		}
		return wm;
	}

}
