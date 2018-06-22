package com.twb.wechatrobot.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.twb.wechatrobot.data.OutData;
import com.twb.wechatrobot.entity.WechatGroup;
import com.twb.wechatrobot.repository.WechatGroupRepository;
import com.twb.wechatrobot.service.WechatGroupShowService;

@Service
public class WechatGroupShowServiceImp implements WechatGroupShowService
{

	private static final Logger logger = LoggerFactory.getLogger(WechatGroupShowServiceImp.class);

	@Autowired
	private WechatGroupRepository wechatGroupRepository;

	@Override
	public OutData getAllGroup() throws Exception
	{
		OutData od = new OutData();
		List outlist = new ArrayList();
		List<WechatGroup> list = wechatGroupRepository.getAllWechatGroup();
		if(list!=null&&list.size()>0)
		{
			for(WechatGroup wg :list)
			{
				Map map = new HashMap();
				map.put("groupName", wg.getGroupName());
				map.put("groupId", wg.getGroupId());
				outlist.add(map);
			}
		}
		od.setOutlist(outlist);
		od.setReturncode("true");
		od.setReturnmsg("获取成功");
		return od;
	}

	
}
