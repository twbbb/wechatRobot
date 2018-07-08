package com.twb.wechatrobot.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.data.OutData;
import com.twb.wechatrobot.entity.AdMessage;
import com.twb.wechatrobot.repository.AdMessageRepository;
import com.twb.wechatrobot.service.AdMessageService;
import com.twb.wechatrobot.utils.CommonUtils;

@Service
public class AdMessageServiceImp implements AdMessageService
{

	private static final Logger logger = LoggerFactory.getLogger(AdMessageServiceImp.class);

	@Autowired
	AdMessageRepository adMessageRepository;

	@Override
	public OutData getAdMsgRecord(Map inMap) throws Exception
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

		String wxgroupName = (String) inMap.get("wxgroupName");
		String fromuserName = (String) inMap.get("fromuserName");
		String messageType = (String) inMap.get("messageType");
		String dateBeforeStr = (String) inMap.get("dateBefore");
		String dateAfterStr = (String) inMap.get("dateAfter");

		Pageable pageable = PageRequest.of(pageInt, pageSize, Direction.DESC, "timestamp");

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Page<AdMessage> page = adMessageRepository.findAll(new Specification<AdMessage>()
		{
			@Override
			public Predicate toPredicate(Root<AdMessage> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder)
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
				if (!StringUtils.isEmpty(wxgroupName))
				{
					list.add(criteriaBuilder.like(root.get("wxgroupName").as(String.class), "%" + wxgroupName + "%"));
				}
				if (!StringUtils.isEmpty(fromuserName))
				{
					list.add(criteriaBuilder.like(root.get("fromuserName").as(String.class), "%" + fromuserName + "%"));
				}
				if (!StringUtils.isEmpty(messageType))
				{
					list.add(criteriaBuilder.equal(root.get("messageType").as(String.class), messageType));
				}
				if (dateBefore != null)
				{
					list.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), dateBefore));
				}
				if (dateAfter != null)
				{
					list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), dateAfter));
				}
				// 默认待处理状态
				list.add(criteriaBuilder.equal(root.get("deleteState").as(String.class),
						AdMessage.DELETE_STATE_DEFUALT));
				// 有效
//				list.add(criteriaBuilder.equal(root.get("isOverdue").as(String.class), AdMessage.ISOVERDUE_NO));
				// 是群主
//				list.add(criteriaBuilder.equal(root.get("isowner").as(String.class), AdMessage.ISOWNER));

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

		List<AdMessage> list = page.getContent();

		List<Map<String, Object>> outlist = new ArrayList();
		for (AdMessage wm : list)
		{
			Map map = new HashMap();

			map.put("isowner", CommonUtils.toString(wm.getIsowner()));
			map.put("id", CommonUtils.toString(wm.getId()));
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

	@Transactional(rollbackFor = Exception.class)
	@Override
	public OutData delMember(Map inMap) throws Exception
	{
		OutData od = new OutData();
		String adid = (String) inMap.get("id");
		logger.info("delMember,adid:" + adid);
		String[] ids = adid.split(",");
		for(int i=0;i<ids.length;i++)
		{
			String id = ids[i];
			OutData temp = delByid( id);
			logger.info("delMember id:"+id+",msg:"+temp.getReturnmsg());
		}
		od.setReturncode("true");
		od.setReturnmsg("处理完成");
		return od;
	}

	private OutData delByid(String adid)
	{
		OutData od = new OutData();
		if (!CommonUtils.validateNumber(adid))
		{
			od.setReturncode("false");
			od.setReturnmsg("id错误");
			return od;
		}
		int idInt = Integer.parseInt(adid);
		AdMessage am = adMessageRepository.getOne(idInt);
		if (am == null)
		{
			od.setReturncode("false");
			od.setReturnmsg("广告消息未获取到");
			return od;
		}
		if(!AdMessage.DELETE_STATE_DEFUALT.equals(am.getDeleteState()))
		{
			od.setReturncode("false");
			od.setReturnmsg("消息不是待处理状态");
			return od;
		}
		String wxGrpId = am.getWxgroupId();
//		WXGroup wxGroup = MyWeChatListener.wechatClient.userGroup(wxGrpId);
//		if (wxGroup == null)
//		{
//			od.setReturncode("false");
//			od.setReturnmsg("未获取到微信群信息");
//			return od;
//		}
//		if (!wxGroup.isOwner)
//		{
//			od.setReturncode("false");
//			od.setReturnmsg("不是群主，无法踢人");
//			return od;
//		}
		String fromuserId = am.getFromuserId();
		List<String> userIds = new ArrayList();
		userIds.add(fromuserId);
//		logger.info("delMember, data:" + wxGrpId + "," + fromuserId);
//		MyWeChatListener.wechatClient.delGroupMember(wxGroup, userIds);
//		logger.info("delMember success");

		am.setDeleteState(AdMessage.DELETE_STATE_AD_DELETE);
		am.setHandlerTime(new Date());
		adMessageRepository.save(am);
		adMessageRepository.updateDeleteState(wxGrpId, fromuserId);// 更新此被踢成员所有信息状态

		od.setReturncode("true");
		od.setReturnmsg("状态已修改为已踢");
		return od;
	}

	@Override
	public OutData getDelMemberLog(Map inMap) throws Exception
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

		String wxgroupName = (String) inMap.get("wxgroupName");
		String fromuserName = (String) inMap.get("fromuserName");
		String messageType = (String) inMap.get("messageType");
		String dateBeforeStr = (String) inMap.get("dateBefore");
		String dateAfterStr = (String) inMap.get("dateAfter");

		Pageable pageable = PageRequest.of(pageInt, pageSize, Direction.DESC, "handlerTime");

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Page<AdMessage> page = adMessageRepository.findAll(new Specification<AdMessage>()
		{
			@Override
			public Predicate toPredicate(Root<AdMessage> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder)
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
				if (!StringUtils.isEmpty(wxgroupName))
				{
					list.add(criteriaBuilder.like(root.get("wxgroupName").as(String.class), "%" + wxgroupName + "%"));
				}
				if (!StringUtils.isEmpty(fromuserName))
				{
					list.add(criteriaBuilder.like(root.get("fromuserName").as(String.class), "%" + fromuserName + "%"));
				}
				if (!StringUtils.isEmpty(messageType))
				{
					list.add(criteriaBuilder.equal(root.get("messageType").as(String.class), messageType));
				}
				if (dateBefore != null)
				{
					list.add(criteriaBuilder.lessThanOrEqualTo(root.get("handlerTime"), dateBefore));
				}
				if (dateAfter != null)
				{
					list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("handlerTime"), dateAfter));
				}
				// 默认待处理状态
				// list.add(criteriaBuilder.and(root.get("deleteState").as(String.class),
				// AdMessage.DELETE_STATE_DEFUALT));
				Path<Object> path = root.get("deleteState");
				CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
				in.value(AdMessage.DELETE_STATE_AD_DELETE);
				in.value(AdMessage.DELETE_STATE_NOT_AD);
				list.add(in);

				// 是群主
//				list.add(criteriaBuilder.equal(root.get("isowner").as(String.class), AdMessage.ISOWNER));

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

		List<AdMessage> list = page.getContent();

		List<Map<String, Object>> outlist = new ArrayList();
		for (AdMessage wm : list)
		{
			Map map = new HashMap();

			map.put("handlerTime", CommonUtils.toString(wm.getHandlerTime()));
			map.put("deleteState", CommonUtils.toString(wm.getDeleteState()));
			map.put("isowner", CommonUtils.toString(wm.getIsowner()));
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

	@Override
	public OutData notDel(Map inMap) throws Exception
	{
		OutData od = new OutData();
		String adid = (String) inMap.get("id");
		logger.info("notDel,adid:" + adid);
		String[] ids = adid.split(",");
		for(int i=0;i<ids.length;i++)
		{
			String id = ids[i];
			OutData temp = notDelById( id);
			logger.info("notDel id:"+id+",msg:"+temp.getReturnmsg());
		}
		od.setReturncode("true");
		od.setReturnmsg("处理完成");
		return od;
	}
	
	public OutData notDelById(String adid) throws Exception
	{
		OutData od = new OutData();
		if (!CommonUtils.validateNumber(adid))
		{
			od.setReturncode("false");
			od.setReturnmsg("id错误");
			return od;
		}
		int idInt = Integer.parseInt(adid);
		AdMessage am = adMessageRepository.getOne(idInt);
		if (am == null)
		{
			od.setReturncode("false");
			od.setReturnmsg("广告消息未获取到");
			return od;
		}
		
		if(!AdMessage.DELETE_STATE_DEFUALT.equals(am.getDeleteState()))
		{
			od.setReturncode("false");
			od.setReturnmsg("消息不是待处理状态");
			return od;
		}
		
		am.setDeleteState(AdMessage.DELETE_STATE_NOT_AD);
		am.setHandlerTime(new Date());
		adMessageRepository.save(am);
		od.setReturncode("true");
		od.setReturnmsg("状态已修改为不踢");
		return od;
	}


	@Transactional(rollbackFor = Exception.class)
	@Override
	public void msgOverdue() throws Exception
	{
		logger.info("msgOverdue");
		adMessageRepository.updateIsOverdue();
			
	}

}
