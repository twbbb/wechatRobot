package com.twb.wechatrobot.service.msghandler.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.twb.wechatrobot.service.impl.MqProductServiceImp;
import com.twb.wechatrobot.service.msghandler.MessageHandler;

import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXText;

@Service
public class TextGroupMsgHandler implements MessageHandler
{
	private Logger logger = LoggerFactory.getLogger(TextGroupMsgHandler.class);
	@Override
	public void handleMsg(WXMessage message) throws Exception
	{
		if (!(message instanceof WXText))
		{
			return ;
		}
		WXText wxText = (WXText) message;
		if(wxText.fromGroup!=null)
		{
			logger.info("wxText.fromGroup.isOwner:"+wxText.fromGroup.isOwner);
			logger.info("wxText.fromGroup.name:"+wxText.fromGroup.name);
		}
//		if(wxText.fromGroup!=null&&wxText.fromGroup.isOwner)
//		{
//			//如果是群发群，并且不是机器人发的消息
//			if(wxText.fromGroup.name.equals(groupmessage_flag)&&!wxText.fromUser.id.equals(MyWeChatListener.wechatClient.userMe().id))
//			{
//				MessageGroup mg = new MessageGroup();
//				mg.setContent(wxText.content);
//				mg.setGroupName(wxText.fromGroup.name);
//				mg.setId(wxText.fromGroup.id);
//				GroupMessageQueue.add(mg);
//			}
//			//消息上链群
//			else if(wxText.fromGroup.name.startsWith(commitchain_group_flag))
//			{
//				Map memos = new HashMap();
//				memos.put("group", wm.getWxgroupName());
//				memos.put("user", wm.getFromuserName());
//				memos.put("time",toString( wm.getTimestamp()));
//				memos.put("content", wm.getContentText());
//				CommitchainMqData cmd = new CommitchainMqData();
//				cmd.setAmountcurrency("SWT");
//				cmd.setAmountvalue(0.00001);
//				cmd.setBusinessid(wm.getMsgid());
//				cmd.setCounterparty(commitchain_counterparty);
//				cmd.setMemos(memos);
//				
//				SendResult sr = mqProductServiceImp.sendCommitChainMQ(cmd);
//				CommitchainLog cl = new CommitchainLog();
//				cl.setWechatMessageId(wm.getMsgid());
//				if(sr!=null)
//				{
//					logger.info("上链成功success");
//					cl.setCommitchainDate(new Date());
//					cl.setCommitchainMessageId(sr.getMessageId());
//					cl.setCommitchainState(CommitchainLog.STATE_SUCCESS);
//				}
//				else
//				{
//					logger.info("上链失败 fail");
//					cl.setCommitchainState(CommitchainLog.STATE_FAIL);
//					cl.setCommitchainDate(new Date());
//				}
//				commitchainLogRepository.save(cl);
//			}
//			
//		}
		
	}

}
