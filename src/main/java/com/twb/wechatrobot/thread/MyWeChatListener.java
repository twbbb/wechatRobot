package com.twb.wechatrobot.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twb.wechatrobot.data.MessageGroup;
import com.twb.wechatrobot.entity.DcPublic;
import com.twb.wechatrobot.repository.DcPublicRepository;
import com.twb.wechatrobot.service.AdMessageService;
import com.twb.wechatrobot.service.WechatGroupService;
import com.twb.wechatrobot.service.WechatMessageService;
import com.twb.wechatrobot.service.msghandler.MessageHandler;
import com.twb.wechatrobot.utils.QAMessageQueue;

import me.xuxiaoxiao.chatapi.wechat.WeChatClient;
import me.xuxiaoxiao.chatapi.wechat.WeChatClient.WeChatListener;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXContact;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup.Member;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXMessage;

@Component
public class MyWeChatListener extends WeChatListener {
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

	private static final Logger logger = LoggerFactory.getLogger(MyWeChatListener.class);

	@Autowired
	WechatMessageService wechatMessageServiceImp;
	@Autowired
	WechatGroupService wechatGroupServiceImp;
	@Autowired
	List<MessageHandler> messageHandlerList;
	@Autowired
	DcPublicRepository dcPublicRepository ;

	@Autowired
	AdMessageService adMessageService;

	public static WeChatClient wechatClient;

	public static volatile boolean finish = false;

	public static Map userMap = new ConcurrentHashMap();
	public static Map groupMap = new ConcurrentHashMap();

	private String splitStr="|$|";

	@PostConstruct
	public void init() throws Exception {
		for (MessageHandler mh : messageHandlerList) {
			mh.init();
		}
	}

	@Override
	public void onContact(WXContact contact, int operate) {
		logger.info("onModContact");
		HashMap<String, WXGroup> wxGroupMap = wechatClient.userGroups();

		if (contact instanceof WXGroup) {
			WXGroup group = (WXGroup) contact;
			if (operate == WeChatClient.MOD_CONTACT) {
				String name = getChangeUser(group);
				if(!StringUtils.isEmpty(name)){
					
					String msg = "";
					try {
						DcPublic dcPublic =dcPublicRepository.getValue("welcom");
						if(dcPublic!=null){
							msg=dcPublic.getValue();
						}
						if(!StringUtils.isEmpty(msg))
						{
							String sendMsg = msg.replace("${userName}", name).replace("${groupName}", group.name);
							logger.info("发送欢迎："+name+","+group.name);
							MessageGroup mg = new MessageGroup();
							mg.setContent(sendMsg);
							mg.setId(group.id);
							QAMessageQueue.add(mg);
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
			addUser(group);
		}

		try {
			if (finish) {
				wechatGroupServiceImp.handleAddGroup(wxGroupMap);

			}

		} catch (Exception e) {
			logger.error("群组添加处理失败！！");
			e.printStackTrace();
		}

		logger.info(String.format("onModContact：您有%d名好友、活跃微信群%d个", wechatClient.userFriends().size(),
				wechatClient.userGroups().size()));
	}
	
	//新增的用户
	public String getChangeUser(WXGroup wxGroup){
		if(!groupMap.containsKey(wxGroup.id))
		{
			return "";
		}
		StringBuffer sb = new StringBuffer();
		String groupId = wxGroup.id;
		// 将群组用户数据存入用户表
		HashMap<String, Member> membersMap = wxGroup.members;
		for (Entry<String, Member> membersEntry : membersMap.entrySet()) {
			Member member = membersEntry.getValue();
			String memberId = member.id;
			String name = member.name;
			if (!userMap.containsKey(groupId +splitStr+ memberId)) {
				if(sb.length()==0){
					sb.append(name);	
				}
				else{
					sb.append(",").append(name);
				}
				
			}
		}
		
		return sb.toString();
	}

	public void addUser(WXGroup wxGroup) {
		String groupId = wxGroup.id;
		groupMap.put(groupId, "");
		// 将群组用户数据存入用户表
		HashMap<String, Member> membersMap = wxGroup.members;
		for (Entry<String, Member> membersEntry : membersMap.entrySet()) {
			Member member = membersEntry.getValue();
			String memberId = member.id;
			String name = member.name;
			if (!userMap.containsKey(groupId +splitStr+ memberId)) {
				userMap.put(groupId +splitStr+ memberId, name);
			}
		}
	}

	@Override
	public void onQRCode(String qrCode) {
		logger.info("======================onQRCode========================");
		logger.info(qrCode);
		logger.info("=======================onQRCode=======================");
		logger.info("=======================onQRCode=======================");
		logger.info(qrCode);
		logger.info("=======================onQRCode=======================");

	}

	@Override
	public void onLogin() {
		logger.info("onLogin");

		// try
		// {
		// //之前广告消息失效
		// adMessageService.msgOverdue();
		// }
		// catch (Exception e1)
		// {
		// logger.error("广告消息失效处理失败",e1);
		// e1.printStackTrace();
		// }

		try {

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						HashMap<String, WXGroup> wxGroupMap = wechatClient.userGroups();
						Thread.sleep(30000);// 睡眠30秒，等待更新群组事件结束
						wechatGroupServiceImp.deleteAllGroup();
						wechatGroupServiceImp.handleAllGroup(wxGroupMap);
						for (Entry<String, WXGroup> entry : wxGroupMap.entrySet()) {
							WXGroup wxGroup = entry.getValue();
							addUser(wxGroup);
						}
						finish = true;
						// Thread.sleep(10000);
						// //删除用户重复统计表数据
						// wechatGroupServiceImp.wechatUserDeleteAll();
						// //插入数据
						// Thread.sleep(30000);
						// wechatGroupServiceImp.handleAllGroupSaveDb();
					} catch (Exception e) {
						logger.error("群组处理失败2！！", e);
						e.printStackTrace();
					}
				}

			});
			thread.start();

		} catch (Exception e) {
			logger.error("群组处理失败！！", e);
			e.printStackTrace();
		}

		logger.info(String.format("onLogin：您有%d名好友、活跃微信群%d个", wechatClient.userFriends().size(),
				wechatClient.userGroups().size()));
	}

	@Override
	public void onMessage(WXMessage message) {

		// logger.info(message.getClass().getName() + ",获取到消息：" +
		// GSON.toJson(message));
		try {
			// 保存信息
			wechatMessageServiceImp.saveMessage(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("数据库存入失败", e);
		}

		for (MessageHandler mh : messageHandlerList) {
			try {
				mh.handleMsg(message);
			} catch (Exception e) {
				logger.error("消息处理失败:" + mh.getClass().getSimpleName() + "," + message.id, e);
			}
		}

	}

}
