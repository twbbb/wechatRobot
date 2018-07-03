package com.twb.wechatrobot.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.entity.WechatGroup;
import com.twb.wechatrobot.entity.WechatGroupLog;
import com.twb.wechatrobot.entity.WechatGroupMembers;
import com.twb.wechatrobot.entity.WechatUser;
import com.twb.wechatrobot.repository.WechatGroupLogRepository;
import com.twb.wechatrobot.repository.WechatGroupMembersRepository;
import com.twb.wechatrobot.repository.WechatGroupRepository;
import com.twb.wechatrobot.repository.WechatMessageRepository;
import com.twb.wechatrobot.repository.WechatUserRepository;
import com.twb.wechatrobot.service.WechatGroupService;
import com.twb.wechatrobot.thread.MyWeChatListener;

import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup.Member;

@Service
public class WechatGroupServiceImp implements WechatGroupService
{

	private static final Logger logger = LoggerFactory.getLogger(WechatGroupServiceImp.class);

	@Autowired
	private WechatGroupLogRepository wechatGroupLogRepository;
	@Autowired
	private WechatGroupMembersRepository wechatGroupMembersRepository;
	@Autowired
	private WechatGroupRepository wechatGroupRepository;
	@Autowired
	private WechatUserRepository wechatUserRepository;
	
	@Autowired
	private	WechatMessageRepository wechatMessageRepository;
	
	public static Set GROUPSET = Collections.synchronizedSet(new HashSet());
	
	public static Map<String, WechatUser> userMap = new ConcurrentHashMap();
	

	@Transactional(rollbackFor = Exception.class)
	public void deleteAllGroup() throws Exception
	{
		logger.info("deleteAllGroup start");
		//将数据复制到日志表
		List<WechatGroup> wgList = wechatGroupRepository.getAllWechatGroup();
		if(wgList!=null&&wgList.size()>0)
		{
			logger.info("复制历史群组数据:"+wgList.size());
			moveWgList(wgList);
		}
		GROUPSET.clear();
		userMap.clear();
		logger.info("deleteAllGroup end");
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void handleAllGroup(HashMap<String, WXGroup> wxGroupMap) throws Exception
	{
		logger.info("handleAllGroup start");
		for (Entry<String, WXGroup> entry : wxGroupMap.entrySet())
		{
			WXGroup wxGroup = entry.getValue();
			logger.info("处理群组:"+wxGroup.name);
			logger.info(MyWeChatListener.GSON.toJson(wxGroup));
			GROUPSET.add(entry.getKey());
			//将群组数据存入群组表
			WechatGroup wg = new WechatGroup();
			wg.setCreatetime(new Date());
			wg.setGroupId(wxGroup.id);
			wg.setGroupName(wxGroup.name);
			wg.setMembers(wxGroup.members.size());
			wechatGroupRepository.save(wg);
			
			//将群组用户数据存入用户表
			HashMap<String, Member> membersMap = wxGroup.members;
			for (Entry<String, Member> membersEntry : membersMap.entrySet())
			{
				String id = membersEntry.getKey();
				Member member = membersEntry.getValue();
				if(userMap.containsKey(id))
				{
					WechatUser wu = userMap.get(id);
					String fromgroupId = wu.getFromgroupId();
					String fromgroupName = wu.getFromgroupName();
					if(fromgroupId.length()<3800)
					{
						wu.setFromgroupId(fromgroupId+"|#"+wxGroup.id);
					}
					wu.setFromgroupName(fromgroupName+"|#"+wxGroup.name);
					wu.setFromgroupSize(wu.getFromgroupSize()+1);
				}
				else
				{
					WechatUser wu = new WechatUser();
					wu.setCreatetime(new Date());
					wu.setFromgroupId(wxGroup.id);
					wu.setFromgroupName(wxGroup.name);
					wu.setUserId(member.id);
					String name = member.name.trim();
//					if(!isMessyCode(name))
					{
						wu.setUserName(name);
					}
					wu.setFromgroupSize(1);
					userMap.put(id, wu);
				}
			}
		}
		
//		for (Entry<String, WechatUser> userMapEntry : userMap.entrySet())
//		{
//			
//			WechatUser wu = userMapEntry.getValue();
//			logger.info("handleAllGroup处理用户:"+wu.getUserName());
//			logger.info(MyWeChatListener.GSON.toJson(wu));
//			wechatUserRepository.save(wu);
//		}

		logger.info("handleAllGroup end");
	}
	
	/**
	 * 判断字符串是否乱码
	 * @author yang.shen
	 * @param strName
	 * @return boolean
	 */
	public static boolean isMessyCode(String strName) {
	Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
	Matcher m = p.matcher(strName);
	String after = m.replaceAll("");
	String temp = after.replaceAll("\\p{P}", "");
	char[] ch = temp.trim().toCharArray();
	float chLength = 0;
	float count = 0;
	for (int i = 0; i < ch.length; i++) {
	char c = ch[i];
	if (!Character.isLetterOrDigit(c)) {
	if (!isChinese(c)) {
	count = count + 1;
	}
	chLength++;
	}
	}
	float result = count / chLength;
	if (result > 0.4) {
	return true;
	} else {
	return false;
	}
	}
	private static boolean isChinese(char c) {
	Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
	if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
	|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
	|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
	|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
	|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
	|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
	return true;
	}
	return false;
	}

	private void moveWgList(List<WechatGroup> wgList)
	{
		for(WechatGroup wg :wgList)
		{
			WechatGroupLog wgl = new WechatGroupLog();
			wgl.setCreatetime(new Date());
			wgl.setGroupCreatetime(wg.getCreatetime());
			wgl.setGroupId(wg.getGroupId());
			wgl.setGroupName(wg.getGroupName());
			wechatGroupLogRepository.save(wgl);
			wechatGroupRepository.delete(wg);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void handleAddGroup(HashMap<String, WXGroup> wxGroupMap) throws Exception
	{
		Map<String, WechatUser> userAddMap = new HashMap();
		for (Entry<String, WXGroup> entry : wxGroupMap.entrySet())
		{
			String key = entry.getKey();
			if(GROUPSET.contains(key))
			{
				continue;
			}
			GROUPSET.add(entry.getKey());
			WXGroup wxGroup = entry.getValue();
			logger.info("处理群组:"+wxGroup.name);
			logger.info(MyWeChatListener.GSON.toJson(wxGroup));
			//将群组数据存入群组表
			WechatGroup wg = new WechatGroup();
			wg.setCreatetime(new Date());
			wg.setGroupId(wxGroup.id);
			wg.setGroupName(wxGroup.name);
			wg.setMembers(wxGroup.members.size());
			wechatGroupRepository.save(wg);
			
			//将群组用户数据存入用户表
			HashMap<String, Member> membersMap = wxGroup.members;
			for (Entry<String, Member> membersEntry : membersMap.entrySet())
			{
				String id = membersEntry.getKey();
				Member member = membersEntry.getValue();
				if(userMap.containsKey(id))
				{
					WechatUser wu = userMap.get(id);
					String fromgroupId = wu.getFromgroupId();
					String fromgroupName = wu.getFromgroupName();
					if(fromgroupId.length()<3800)
					{
						wu.setFromgroupId(fromgroupId+"|#"+wxGroup.id);
					}
					wu.setFromgroupSize(wu.getFromgroupSize()+1);
					wu.setFromgroupName(fromgroupName+"|#"+wxGroup.name);
				}
				else
				{
					WechatUser wu = new WechatUser();
					wu.setCreatetime(new Date());
					wu.setFromgroupId(wxGroup.id);
					wu.setFromgroupName(wxGroup.name);
					wu.setUserId(member.id);
					String name = member.name.trim();
//					if(!isMessyCode(name))
					{
						wu.setUserName(name);
					}
					wu.setFromgroupSize(1);
					userMap.put(id, wu);
				}
				userAddMap.put(id, userMap.get(id));
			}
		}
		
//		for (Entry<String, WechatUser> userMapEntry : userAddMap.entrySet())
//		{
//			
//			WechatUser wu = userMapEntry.getValue();
//			logger.info("handleAddGroup处理用户:"+wu.getUserName());
//			logger.info(MyWeChatListener.GSON.toJson(wu));
//			wechatUserRepository.save(wu);
//		}

	}

	@Transactional(rollbackFor = Exception.class)
	public void totalGroupMember(HashMap<String, WXGroup> wxGroupMap) throws Exception
	{
		logger.info("totalGroupMember,wxGroupMap size: "+wxGroupMap.size());
		Date date = new Date();
		for (Entry<String, WXGroup> entry : wxGroupMap.entrySet())
		{
			
			WXGroup wxGroup = entry.getValue();
			logger.info("totalGroupMember处理群组:"+wxGroup.name);
			if(!StringUtils.isEmpty(wxGroup.name))
			{
				WechatGroupMembers wgm = new WechatGroupMembers();
				wgm.setCreatetime(date);
				wgm.setGroupId(wxGroup.id);
				wgm.setGroupName(wxGroup.name);
				wgm.setMembers(wxGroup.members.size());
				int count = wechatMessageRepository.countMessage(wxGroup.name);
				wgm.setMsgs(count);
				wechatGroupMembersRepository.save(wgm);
			}
			
		}
		
	}


	

}
