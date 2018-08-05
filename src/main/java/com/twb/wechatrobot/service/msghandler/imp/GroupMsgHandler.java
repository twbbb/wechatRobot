package com.twb.wechatrobot.service.msghandler.imp;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.data.MessageGroup;
import com.twb.wechatrobot.entity.GroupMessage;
import com.twb.wechatrobot.entity.WechatMessage;
import com.twb.wechatrobot.entity.WechatUser;
import com.twb.wechatrobot.repository.GroupMessageRepository;
import com.twb.wechatrobot.service.impl.WechatGroupServiceImp;
import com.twb.wechatrobot.service.msghandler.MessageHandler;
import com.twb.wechatrobot.thread.MyWeChatListener;
import com.twb.wechatrobot.utils.GroupMessageQueue;

import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXImage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXText;

@Service
public class GroupMsgHandler implements MessageHandler
{
	@Value("${file_dir}")
	private String file_dir;

	@Value("${GROUPMESSAGE_FLAG}")
	private String groupmessage_flag;

	@Autowired
	GroupMessageRepository groupMessageRepository;

	private Logger logger = LoggerFactory.getLogger(GroupMsgHandler.class);

	@Override
	public void handleMsg(WXMessage message) throws Exception
	{

		
		// 是群消息，并且是群主，并且群名称为指定名称
		if (message.fromGroup == null || !message.fromGroup.isOwner
				|| !message.fromGroup.name.equals(groupmessage_flag))
		{
			return;
		}

		// 是自己发送的消息
		if (message.fromUser==null||message.fromUser.id.equals(MyWeChatListener.wechatClient.userMe().id))
		{
			return ;
		}
		if (message instanceof WXText)
		{
			WXText wxText = (WXText) message;
			String content = wxText.content.replace("<br/>", "\r\n");
			handleTextMsg(wxText);
				MessageGroup mg = new MessageGroup();
				mg.setContent(content);
				mg.setGroupName(wxText.fromGroup.name);
				mg.setId(wxText.fromGroup.id);
				GroupMessageQueue.add(mg);
			
		}
		else if (message instanceof WXImage)
		{
			GroupMessage gm = handleImgMsg((WXImage) message);
			MessageGroup mg = new MessageGroup();
			mg.setFile(new File(file_dir + gm.getContentFile()));
			mg.setGroupName(message.fromGroup.name);
			mg.setId(message.fromGroup.id);
			GroupMessageQueue.add(mg);
		}
		else
		{
			return;
		}


	}

	private GroupMessage handleImgMsg(WXImage wxImage) throws Exception
	{
		GroupMessage wm = new GroupMessage();
		saveCommonData(wxImage, wm);
		wm.setMessageType(WechatMessage.MESSAGETYPE_IMAGE);
		if (wxImage.origin != null)
		{
			wm.setContentFile(wxImage.origin.getPath().replace(file_dir, ""));
		}
		else
		{
			// 取大图
			MyWeChatListener.wechatClient.fetchImage(wxImage);
			wm.setContentFile(wxImage.origin.getPath().replace(file_dir, ""));
		}

		return groupMessageRepository.save(wm);

	}

	private GroupMessage handleTextMsg(WXText wxText) throws Exception
	{
		
		String content = wxText.content;
		content = content.replace("<br/>", "\r\n");
		if (StringUtils.isEmpty(content))
		{
			return null;
		}

		GroupMessage wm = new GroupMessage();
		saveCommonData(wxText, wm);
		wm.setMessageType(GroupMessage.MESSAGETYPE_TEXT);
		wm.setContentText(wxText.content);
		return groupMessageRepository.save(wm);

		

	}

	private void saveCommonData(WXMessage wxMessage, GroupMessage wm)
	{
		wm.setTimestamp(new Date(wxMessage.timestamp));
		wm.setMsgid(wxMessage.id + "");
		if (wxMessage.fromGroup != null)
		{
			wm.setWxgroupName(wxMessage.fromGroup.name);
			if (wxMessage.fromUser != null)
			{
				if (!StringUtils.isEmpty(wxMessage.fromUser.name))
				{
					wm.setFromuserName(wxMessage.fromUser.name);
				}
				else
				{
					WXGroup.Member member = wxMessage.fromGroup.members.get(wxMessage.fromUser.id);
					if (member != null)
					{
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
						if (StringUtils.isEmpty(member.display))
						{
							wm.setFromuserName(member.name);
						}
						else
						{
							wm.setFromuserName(member.display);
						}
						if (content.startsWith("<br/>"))
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
