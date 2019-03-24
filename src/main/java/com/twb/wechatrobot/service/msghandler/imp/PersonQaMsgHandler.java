package com.twb.wechatrobot.service.msghandler.imp;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.data.MessageGroup;
import com.twb.wechatrobot.entity.WechatMessage;
import com.twb.wechatrobot.entity.WechatQaMessage;
import com.twb.wechatrobot.entity.WechatQaMessageHis;
import com.twb.wechatrobot.entity.WechatUser;
import com.twb.wechatrobot.repository.WechatQaMessageHisRepository;
import com.twb.wechatrobot.repository.WechatQaMessageRepository;
import com.twb.wechatrobot.service.impl.WechatGroupServiceImp;
import com.twb.wechatrobot.service.msghandler.MessageHandler;
import com.twb.wechatrobot.thread.MyWeChatListener;
import com.twb.wechatrobot.utils.QAMessageQueue;

import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXText;

@Service
public class PersonQaMsgHandler implements MessageHandler {

	private Logger logger = LoggerFactory.getLogger(PersonQaMsgHandler.class);

	@Value("${ROBOT_NAME}")
	private String robot_name;

	@Value("${QAMESSAGE_FLAG}")
	private String qamessage_flag;

	@Autowired
	private WechatQaMessageRepository wechatQaMessageRepository;

	@Autowired
	private WechatQaMessageHisRepository wechatQaMessageHisRepository;

	private static WechatQaMessage tempQaMessage = null;

	private static String QAGROUPID = "";

	@Transactional(rollbackFor = Exception.class)
	public void init() throws Exception {
		wechatQaMessageRepository.moveToHis();
		wechatQaMessageRepository.deleteAll();
	}
	@Transactional(rollbackFor = Exception.class)
	public void handleMsg(WXMessage message) throws Exception {
		// 是自己发送的消息
		if (message.fromUser == null || message.fromUser.id.equals(MyWeChatListener.wechatClient.userMe().id)) {
			return;
		}

		// // 是群消息，并且是群主，并且群名称为问答群
		if (message.fromGroup != null && message.fromGroup.isOwner && message.fromGroup.name.equals(qamessage_flag)) {
			// 回复处理
			answerMsg(message);
			return;
		}

		// 是群消息
		if (message.fromGroup != null) {
			handlerGroupMsg(message);
		} else {
			handlerPersonMsg(message);
		}

	}

	private void answerMsg(WXMessage message) {
		if (!(message instanceof WXText)) {
			return;

		}
		if (tempQaMessage == null) {
			return;
		}
		WXText wxText = (WXText) message;
		String content = wxText.content.replace("<br/>", "\r\n");
		String answerFlag = "";
		if (!StringUtils.isEmpty(content.trim())&&!"0".equals(content.trim())) {
			try {
				answerWqm(content);
			} catch (Exception e) {
				e.printStackTrace();
			}
			answerFlag = "1";// 1.已回复
		} else {
			answerFlag = "2";
		}
		WechatQaMessageHis wechatQaMessageHis = new WechatQaMessageHis();
		wechatQaMessageHis.setAnswerFlag(answerFlag);
		wechatQaMessageHis.setAnswerText(content);
		wechatQaMessageHis.setAnswerTime(new Date());
		wechatQaMessageHis.setContentFile(tempQaMessage.getContentFile());
		wechatQaMessageHis.setContentLink(tempQaMessage.getContentLink());
		wechatQaMessageHis.setContentText(tempQaMessage.getContentText());
		wechatQaMessageHis.setFromuserId(tempQaMessage.getFromuserId());
		wechatQaMessageHis.setFromuserName(tempQaMessage.getFromuserName());
		wechatQaMessageHis.setId(tempQaMessage.getId());
		wechatQaMessageHis.setMessageType(tempQaMessage.getMessageType());
		wechatQaMessageHis.setMsgid(tempQaMessage.getMsgid());
		wechatQaMessageHis.setTimestamp(tempQaMessage.getTimestamp());
		wechatQaMessageHis.setWxgroupId(tempQaMessage.getWxgroupId());
		wechatQaMessageHis.setWxgroupName(tempQaMessage.getWxgroupName());
		wechatQaMessageHisRepository.save(wechatQaMessageHis);
		wechatQaMessageRepository.delete(tempQaMessage);

		tempQaMessage = null;
		WechatQaMessage wqm = null;
		try {
			wqm = wechatQaMessageRepository.getWqm();
			if (wqm != null) {
				sendWqm(wqm);
				tempQaMessage = wqm;
			} else {
				logger.info("wqm 取出为null");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	private void answerWqm(String answerContent) throws Exception {
		String id = tempQaMessage.getWxgroupId();
		String userName = tempQaMessage.getFromuserName();
		String content = tempQaMessage.getContentText();
		if (content.length() > 20) {
			content = content.substring(0, 20) + "...";
		}
		DateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		String time = sdf.format(tempQaMessage.getTimestamp());
		String message = "";
		if(StringUtils.isEmpty(id))
		{
			id = tempQaMessage.getFromuserId();
			message = time+ " 消息：" + content + "\r\n"+"回复："+ answerContent;
		}
		else{
			message = time+ " 消息：" + content + "\r\n"+"回复："+"@" + userName +","+ answerContent;
		}

		MessageGroup mg = new MessageGroup();
		mg.setContent(message);
		mg.setId(id);

		QAMessageQueue.add(mg);

	}

	private WechatQaMessage saveTextMsg(WXText wxText) {
		WechatQaMessage wm = new WechatQaMessage();
		saveCommonData(wxText, wm);
		wm.setMessageType(WechatMessage.MESSAGETYPE_TEXT);
		wm.setContentText(wxText.content);
		return wechatQaMessageRepository.save(wm);

	}

	private void saveCommonData(WXMessage wxMessage, WechatQaMessage wm) {
		wm.setTimestamp(new Date(wxMessage.timestamp));
		wm.setMsgid(wxMessage.id + "");
		if (wxMessage.fromGroup != null) {
			wm.setWxgroupId(wxMessage.fromGroup.id);
			wm.setWxgroupName(wxMessage.fromGroup.name);
			if (wxMessage.fromUser != null) {
				wm.setFromuserId(wxMessage.fromUser.id);
				if (!StringUtils.isEmpty(wxMessage.fromUser.name)) {
					wm.setFromuserName(wxMessage.fromUser.name);
				} else {
					WXGroup.Member member = wxMessage.fromGroup.members.get(wxMessage.fromUser.id);
					if (member != null) {
						wm.setFromuserId(member.id);
						if (StringUtils.isEmpty(member.display)) {
							wm.setFromuserName(member.name);
						} else {
							wm.setFromuserName(member.display);
						}
					}
				}

			} else {
				int index = wxMessage.content.indexOf(":");
				if (index > 0) {
					String userid = wxMessage.content.substring(0, index);
					String content = wxMessage.content.substring(index + 1);
					WXGroup.Member member = wxMessage.fromGroup.members.get(userid);
					if (member != null) {
						wm.setFromuserId(member.id);
						if (StringUtils.isEmpty(member.display)) {
							wm.setFromuserName(member.name);
						} else {
							wm.setFromuserName(member.display);
						}
						if (content.startsWith("<br/>")) {
							content = content.replaceFirst("<br/>", "");
						}
						wxMessage.content = content;

					} else {
						logger.error("发送用户获取失败。。" + userid);
					}

				}

			}
		} else if (wxMessage.fromUser != null) {
			wm.setFromuserId(wxMessage.fromUser.id);
			if (!StringUtils.isEmpty(wxMessage.fromUser.name)) {
				wm.setFromuserName(wxMessage.fromUser.name);
			} else {
				WechatUser wu = WechatGroupServiceImp.userMap.get(wxMessage.fromUser.id);
				if (wu != null) {
					wm.setFromuserName(wu.getUserName());
				}
			}

		}
	}

	// 是群消息
	private void handlerGroupMsg(WXMessage message) {

		if (message instanceof WXText) {

			WXText wxText = (WXText) message;
			String content = wxText.content.replace("<br/>", "\r\n");
			// 是@机器人消息
			if (!wxText.content.contains("@" + robot_name)) {
				return;
			}

			WechatQaMessage wqm = saveTextMsg(wxText);

			if (tempQaMessage == null) {

				try {
					sendWqm(wqm);
					tempQaMessage = wqm;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	private void sendWqm(WechatQaMessage wqm) throws Exception {
		String userName = wqm.getFromuserName();
		String groupName = wqm.getWxgroupName();
		String content = wqm.getContentText();
		DateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		String time = sdf.format(wqm.getTimestamp());
		String headmsg = "";
		if (StringUtils.isEmpty(groupName)) {
			headmsg = time +"-"+ userName + "\r\n";
		} else {
			headmsg = time+"-"+ groupName + "-" + userName + "\r\n";
		}
		if (StringUtils.isEmpty(QAGROUPID)) {
			HashMap<String, WXGroup> wxGroupMap = MyWeChatListener.wechatClient.userGroups();
			for (Entry<String, WXGroup> entry : wxGroupMap.entrySet()) {
				WXGroup wxGroup = entry.getValue();
				if (wxGroup.isOwner && wxGroup.name.equals(qamessage_flag)) {
					QAGROUPID = wxGroup.id;
					break;
				}
			}
		}
		if (!StringUtils.isEmpty(QAGROUPID)) {
			String message = headmsg + content;
			MessageGroup mg = new MessageGroup();
			mg.setContent(message);
			mg.setId(QAGROUPID);

			QAMessageQueue.add(mg);
		}

	}

	// 个人消息
	private void handlerPersonMsg(WXMessage message) {

		if (message instanceof WXText) {
			WXText wxText = (WXText) message;

			WechatQaMessage wqm = saveTextMsg(wxText);

			if (tempQaMessage == null) {
				try {
					sendWqm(wqm);
					tempQaMessage = wqm;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 移除掉问答数据，放入历史

}
