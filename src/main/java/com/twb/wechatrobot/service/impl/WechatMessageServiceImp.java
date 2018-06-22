package com.twb.wechatrobot.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aliyun.openservices.ons.api.SendResult;
import com.twb.commondata.data.CommitchainMqData;
import com.twb.wechatrobot.data.MessageGroup;
import com.twb.wechatrobot.data.MyWeChatListener;
import com.twb.wechatrobot.entity.CommitchainLog;
import com.twb.wechatrobot.entity.WechatMessage;
import com.twb.wechatrobot.entity.WechatUser;
import com.twb.wechatrobot.repository.CommitchainLogRepository;
import com.twb.wechatrobot.repository.WechatMessageRepository;
import com.twb.wechatrobot.service.MqProductService;
import com.twb.wechatrobot.service.WechatMessageService;
import com.twb.wechatrobot.utils.GroupMessageQueue;

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
	CommitchainLogRepository commitchainLogRepository;
	
	@Autowired
	MqProductService mqProductServiceImp;

	@Value("${file_dir}")
	private String file_dir;
	
	@Value("${COMMITCHAIN_GROUP_FLAG}")
	private String commitchain_group_flag;
	
	@Value("${COMMITCHAIN_COUNTERPARTY}")
	private String commitchain_counterparty;
	

	@Value("${GROUPMESSAGE_FLAG}")
	private String groupmessage_flag;
	
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
	public void handleWXText(WXText wxText) throws Exception
	{
		WechatMessage wm = new WechatMessage();
		saveCommonData(wxText, wm);
		wm.setMessageType(WechatMessage.MESSAGETYPE_TEXT);
		wm.setContentText(wxText.content);
		wechatMessageRepository.save(wm);
		if(wxText.fromGroup!=null)
		{
			logger.info("wxText.fromGroup.isOwner:"+wxText.fromGroup.isOwner);
			logger.info("wxText.fromGroup.name:"+wxText.fromGroup.name);
		}
		if(wxText.fromGroup!=null&&wxText.fromGroup.isOwner)
		{
			//如果是群发群，并且不是机器人发的消息
			if(wxText.fromGroup.name.equals(groupmessage_flag)&&!wxText.fromUser.id.equals(MyWeChatListener.wechatClient.userMe().id))
			{
				MessageGroup mg = new MessageGroup();
				mg.setContent(wxText.content);
				mg.setGroupName(wxText.fromGroup.name);
				mg.setId(wxText.fromGroup.id);
				GroupMessageQueue.add(mg);
			}
			//消息上链群
			else if(wxText.fromGroup.name.startsWith(commitchain_group_flag))
			{
				Map memos = new HashMap();
				memos.put("group", wm.getWxgroupName());
				memos.put("user", wm.getFromuserName());
				memos.put("time",toString( wm.getTimestamp()));
				memos.put("content", wm.getContentText());
				CommitchainMqData cmd = new CommitchainMqData();
				cmd.setAmountcurrency("SWT");
				cmd.setAmountvalue(0.00001);
				cmd.setBusinessid(wm.getMsgid());
				cmd.setCounterparty(commitchain_counterparty);
				cmd.setMemos(memos);
				
				SendResult sr = mqProductServiceImp.sendCommitChainMQ(cmd);
				CommitchainLog cl = new CommitchainLog();
				cl.setWechatMessageId(wm.getMsgid());
				if(sr!=null)
				{
					logger.info("上链成功success");
					cl.setCommitchainDate(new Date());
					cl.setCommitchainMessageId(sr.getMessageId());
					cl.setCommitchainState(CommitchainLog.STATE_SUCCESS);
				}
				else
				{
					logger.info("上链失败 fail");
					cl.setCommitchainState(CommitchainLog.STATE_FAIL);
					cl.setCommitchainDate(new Date());
				}
				commitchainLogRepository.save(cl);
			}
			
		}
	}

	@Override
	public void handleWXImage(WXImage wxImage) throws Exception
	{
		WechatMessage wm = new WechatMessage();
		saveCommonData(wxImage, wm);
		wm.setMessageType(WechatMessage.MESSAGETYPE_IMAGE);
		if(wxImage.image!=null)
		{
			wm.setContentFile(wxImage.image.getPath().replace(file_dir, ""));	
		}
		
		wechatMessageRepository.save(wm);
	}

	@Override
	public void handleWXLink(WXLink wxLink) throws Exception
	{
		WechatMessage wm = new WechatMessage();
		saveCommonData(wxLink, wm);
		wm.setMessageType(WechatMessage.MESSAGETYPE_LINK);
		wm.setContentText(wxLink.linkName);
		wm.setContentLink(wxLink.linkUrl);
		wechatMessageRepository.save(wm);
	}

	@Override
	public void handleWXVoice(WXVoice wxVoice) throws Exception
	{
		WechatMessage wm = new WechatMessage();
		saveCommonData(wxVoice, wm);
		wm.setMessageType(WechatMessage.MESSAGETYPE_VOICE);
		// wm.setContentText(wxVoice.);
		 wm.setContentFile(wxVoice.voice.getPath().replace(file_dir, ""));
//		 wechatClient.fetchVoice((WXVoice) message);
		wechatMessageRepository.save(wm);
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

}
