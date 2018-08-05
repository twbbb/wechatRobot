package com.twb.wechatrobot.service.impl;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.data.MessageGroup;
import com.twb.wechatrobot.data.OutData;
import com.twb.wechatrobot.entity.GroupMessage;
import com.twb.wechatrobot.entity.GroupMessageLog;
import com.twb.wechatrobot.repository.GroupMessageLogRepository;
import com.twb.wechatrobot.repository.GroupMessageRepository;
import com.twb.wechatrobot.service.WechatGroupMessageService;
import com.twb.wechatrobot.thread.MyWeChatListener;
import com.twb.wechatrobot.utils.CommonUtils;
import com.twb.wechatrobot.utils.GroupMessageQueue;

import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;

@Service
public class WechatGroupMessageServiceImp implements WechatGroupMessageService
{

	private static final Logger logger = LoggerFactory.getLogger(WechatGroupMessageServiceImp.class);

	@Autowired
	GroupMessageLogRepository groupMessageLogRepository;

	@Autowired
	GroupMessageRepository groupMessageRepository;
	
	@Value("${file_dir}")
	private String file_dir;

	@Override
	public OutData getGroupMsgRecord(Map inMap) throws Exception
	{
		OutData od = new OutData();

		String pageStr = (String) inMap.get("page");
		String pagesize = (String) inMap.get("pagesize");

		int pageInt = 0;
		int pageSize = 50;
		if (CommonUtils.validateNumber(pageStr))
		{
			pageInt = CommonUtils.string2Int(pageStr, 0);
		}
		if (CommonUtils.validateNumber(pagesize))
		{
			pageSize = CommonUtils.string2Int(pagesize, 50);
		}

		String fromuserName = (String) inMap.get("fromuserName");
		String dateBeforeStr = (String) inMap.get("dateBefore");
		String dateAfterStr = (String) inMap.get("dateAfter");

		Pageable pageable = PageRequest.of(pageInt, pageSize, Direction.DESC, "timestamp");

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Page<GroupMessage> page = groupMessageRepository.findAll(new Specification<GroupMessage>()
		{
			@Override
			public Predicate toPredicate(Root<GroupMessage> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder)
			{
				Date dateBefore = null;
				Date dateAfter = null;
				if (!StringUtils.isEmpty(dateBeforeStr))
				{
					try
					{
						dateBefore = sdf.parse(dateBeforeStr);
					}
					catch (ParseException e)
					{
						logger.error("dateBeforeStr日期解析错误" + dateBeforeStr, e);
						e.printStackTrace();
					}
				}

				if (!StringUtils.isEmpty(dateAfterStr))
				{
					try
					{
						dateAfter = sdf.parse(dateAfterStr);
					}
					catch (ParseException e)
					{
						logger.error("dateAfter日期解析错误" + dateAfter, e);
						e.printStackTrace();
					}
				}

				List<Predicate> list = new ArrayList<Predicate>();
				if (!StringUtils.isEmpty(fromuserName))
				{
					list.add(criteriaBuilder.like(root.get("fromuserName").as(String.class), "%" + fromuserName + "%"));
				}
				if (dateBefore != null)
				{
					list.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), dateBefore));
				}
				if (dateAfter != null)
				{
					list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), dateAfter));
				}
				Predicate[] p = new Predicate[list.size()];
				return criteriaBuilder.and(list.toArray(p));
			}
		}, pageable);
		Map outMap = new HashMap();
		if (page.hasNext())
		{
			outMap.put("nextPage", ++pageInt + "");
		}
		else
		{
			outMap.put("nextPage", "-1");
		}
		outMap.put("totalNum", page.getTotalElements() + "");
		outMap.put("allPageSize", page.getTotalPages() + "");

		List<GroupMessage> list = page.getContent();

		List<Map<String, Object>> outlist = new ArrayList();
		for (GroupMessage wm : list)
		{
			Map map = new HashMap();
			map.put("wxgroupName", CommonUtils.toString(wm.getWxgroupName()));
			map.put("fromuserName", CommonUtils.toString(wm.getFromuserName()));
			map.put("timestamp", CommonUtils.toString(wm.getTimestamp()));
			map.put("messageType", CommonUtils.toString(wm.getMessageType()));
			map.put("contentText", CommonUtils.toString(wm.getContentText()));
			map.put("contentLink", CommonUtils.toString(wm.getContentLink()));
			map.put("contentFile", CommonUtils.toString(wm.getContentFile()));
			map.put("id", CommonUtils.toString(wm.getId()));
			outlist.add(map);
		}
		od.setOutmap(outMap);
		od.setOutlist(outlist);
		od.setReturncode("true");
		od.setReturnmsg("获取成功");
		return od;
	}

	@Override
	public OutData sendGroupMsg(Map inMap) throws Exception
	{	
		OutData od = new OutData();
		String gmid = (String) inMap.get("id");
		if (!CommonUtils.validateNumber(gmid))
		{
			od.setReturncode("false");
			od.setReturnmsg("id错误");
			return od;
		}
		int idInt = Integer.parseInt(gmid);
		GroupMessage gm = groupMessageRepository.getOne(idInt);
		if (gm == null)
		{
			od.setReturncode("false");
			od.setReturnmsg("获取数据失败");
			return od;
		}
		
		GroupMessageLog gml = new GroupMessageLog();
		gml.setCreatetime(new Date());
		gml.setGm(gm);
		groupMessageLogRepository.save(gml);
		String content ="";
		File file =null;
		if(GroupMessage.MESSAGETYPE_TEXT.equals(gm.getMessageType()))
		{
			content = gm.getContentText();
			content = content.replace("<br/>", "\r\n");
			logger.info("发送消息内容：" + content);
		}
		else if(GroupMessage.MESSAGETYPE_IMAGE.equals(gm.getMessageType()))
		{
			file = new File(file_dir+gm.getContentFile());
			logger.info("发送图片内容：" + file_dir+gm.getContentFile());
		}
		
		
		
		HashMap<String, WXGroup> wxgroupMap = MyWeChatListener.wechatClient.userGroups();
		for (Entry<String, WXGroup> wxgroupEntry : wxgroupMap.entrySet())
		{
			WXGroup wxgroup = wxgroupEntry.getValue();
			String name = wxgroup.name;
			String id = wxgroup.id;
			if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(id))
			{
//				if("啦啦啦".equals(name))
//				{
					MessageGroup mg = new MessageGroup();
					mg.setContent(content);
					mg.setGroupName(name);
					mg.setId(id);
					mg.setFile(file);
					GroupMessageQueue.add(mg);
//				}
				
			}
		}
		od.setReturncode("true");
		od.setReturnmsg("发送成功");
		return od;
	}

	@Override
	public OutData getGroupMsgLog(Map inMap) throws Exception
	{
		OutData od = new OutData();

		String pageStr = (String) inMap.get("page");
		String pagesize = (String) inMap.get("pagesize");

		int pageInt = 0;
		int pageSize = 50;
		if (CommonUtils.validateNumber(pageStr))
		{
			pageInt = CommonUtils.string2Int(pageStr, 0);
		}
		if (CommonUtils.validateNumber(pagesize))
		{
			pageSize = CommonUtils.string2Int(pagesize, 50);
		}

		String dateBeforeStr = (String) inMap.get("dateBefore");
		String dateAfterStr = (String) inMap.get("dateAfter");

		Pageable pageable = PageRequest.of(pageInt, pageSize, Direction.DESC, "createtime");

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Page<GroupMessageLog> page = groupMessageLogRepository.findAll(new Specification<GroupMessageLog>()
		{
			@Override
			public Predicate toPredicate(Root<GroupMessageLog> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder)
			{
				Date dateBefore = null;
				Date dateAfter = null;
				if (!StringUtils.isEmpty(dateBeforeStr))
				{
					try
					{
						dateBefore = sdf.parse(dateBeforeStr);
					}
					catch (ParseException e)
					{
						logger.error("dateBeforeStr日期解析错误" + dateBeforeStr, e);
						e.printStackTrace();
					}
				}

				if (!StringUtils.isEmpty(dateAfterStr))
				{
					try
					{
						dateAfter = sdf.parse(dateAfterStr);
					}
					catch (ParseException e)
					{
						logger.error("dateAfter日期解析错误" + dateAfter, e);
						e.printStackTrace();
					}
				}

				List<Predicate> list = new ArrayList<Predicate>();
				if (dateBefore != null)
				{
					list.add(criteriaBuilder.lessThanOrEqualTo(root.get("createtime"), dateBefore));
				}
				if (dateAfter != null)
				{
					list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createtime"), dateAfter));
				}
				Predicate[] p = new Predicate[list.size()];
				return criteriaBuilder.and(list.toArray(p));
			}
		}, pageable);
		
		Map outMap = new HashMap();
		if (page.hasNext())
		{
			outMap.put("nextPage", ++pageInt + "");
		}
		else
		{
			outMap.put("nextPage", "-1");
		}
		outMap.put("totalNum", page.getTotalElements() + "");
		outMap.put("allPageSize", page.getTotalPages() + "");

		List<GroupMessageLog> list = page.getContent();

		List<Map<String, Object>> outlist = new ArrayList();
		for (GroupMessageLog gml : list)
		{
			GroupMessage wm = gml.getGm();
			Map map = new HashMap();
			map.put("sendGrpTime", CommonUtils.toString(gml.getCreatetime()));
			
			map.put("wxgroupName", CommonUtils.toString(wm.getWxgroupName()));
			map.put("fromuserName", CommonUtils.toString(wm.getFromuserName()));
			map.put("timestamp", CommonUtils.toString(wm.getTimestamp()));
			map.put("messageType", CommonUtils.toString(wm.getMessageType()));
			map.put("contentText", CommonUtils.toString(wm.getContentText()));
			map.put("contentLink", CommonUtils.toString(wm.getContentLink()));
			map.put("contentFile", CommonUtils.toString(wm.getContentFile()));
			outlist.add(map);
		}
		od.setOutmap(outMap);
		od.setOutlist(outlist);
		od.setReturncode("true");
		od.setReturnmsg("获取成功");
		return od;
	}

}
