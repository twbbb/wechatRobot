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
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.data.OutData;
import com.twb.wechatrobot.entity.WechatMessage;
import com.twb.wechatrobot.repository.WechatMessageRepository;
import com.twb.wechatrobot.service.WechatMessageShowService;
import com.twb.wechatrobot.utils.CommonUtils;

@Service
public class WechatMessageShowServiceImp implements WechatMessageShowService
{

	private static final Logger logger = LoggerFactory.getLogger(WechatMessageServiceImp.class);

	@Autowired
	private WechatMessageRepository wechatMessageRepository;

	@Override
	public OutData getMsg(Map inMap) throws Exception
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

		Page<WechatMessage> page = wechatMessageRepository.findAll(new Specification<WechatMessage>()
		{
			@Override
			public Predicate toPredicate(Root<WechatMessage> root, CriteriaQuery<?> query,
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

		List<WechatMessage> list = page.getContent();

		List<Map<String, Object>> outlist = new ArrayList();
		for (WechatMessage wm : list)
		{
			Map map = new HashMap();
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
