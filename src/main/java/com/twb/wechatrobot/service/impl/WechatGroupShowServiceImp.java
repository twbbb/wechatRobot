package com.twb.wechatrobot.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.data.OutData;
import com.twb.wechatrobot.entity.WechatGroup;
import com.twb.wechatrobot.entity.WechatGroupMembers;
import com.twb.wechatrobot.entity.WechatUser;
import com.twb.wechatrobot.repository.WechatGroupMembersRepository;
import com.twb.wechatrobot.repository.WechatGroupRepository;
import com.twb.wechatrobot.service.WechatGroupShowService;
import com.twb.wechatrobot.thread.MyWeChatListener;
import com.twb.wechatrobot.utils.CommonUtils;

import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup.Member;

@Service
public class WechatGroupShowServiceImp implements WechatGroupShowService
{

	private static final Logger logger = LoggerFactory.getLogger(WechatGroupShowServiceImp.class);

	@Autowired
	private WechatGroupRepository wechatGroupRepository;

	@Autowired
	private WechatGroupMembersRepository wechatGroupMembersRepository;

	@Override
	public OutData getAllGroup(String pageStr, String pagesize) throws Exception
	{
		OutData od = new OutData();
		List outlist = new ArrayList();
		int pageInt = 1;
		int pageSize = 5;
		if (CommonUtils.validateNumber(pageStr))
		{
			pageInt = CommonUtils.string2Int(pageStr, 0);
		}
		if (CommonUtils.validateNumber(pagesize))
		{
			pageSize = CommonUtils.string2Int(pagesize, 0);
		}

		Pageable pageable = PageRequest.of(pageInt, pageSize, Direction.DESC, "members");

		Page<WechatGroup> page = wechatGroupRepository.findAll(new Specification<WechatGroup>()
		{
			@Override
			public Predicate toPredicate(Root<WechatGroup> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder)
			{
				List<Predicate> list = new ArrayList<Predicate>();
				Predicate[] p = new Predicate[list.size()];
				return criteriaBuilder.and(list.toArray(p));
			}
		}, pageable);

		List<WechatGroup> list = page.getContent();
		if (list != null && list.size() > 0)
		{
			for (WechatGroup wg : list)
			{
				Map map = new HashMap();
				map.put("groupName", wg.getGroupName());
				map.put("groupId", wg.getGroupId());
				map.put("members", wg.getMembers() + "");
				outlist.add(map);
			}
		}
		od.setOutlist(outlist);
		Map map = new HashMap();
		map.put("totalPage", page.getTotalPages() + "");
		map.put("totalNum", page.getTotalElements() + "");
		od.setOutmap(map);
		od.setReturncode("true");
		od.setReturnmsg("获取成功");
		return od;
	}

	@Override
	public OutData getGroupRecord(String groupName)
	{
		OutData od = new OutData();
		if (StringUtils.isEmpty(groupName))
		{
			od.setReturncode("false");
			od.setReturnmsg("群名称为空！");
			return od;
		}
		int pageInt = 0;
		int pageSize = 100;

		Pageable pageable = PageRequest.of(pageInt, pageSize, Direction.DESC, "createtime");
		Page<WechatGroupMembers> page = wechatGroupMembersRepository.findAll(new Specification<WechatGroupMembers>()
		{
			@Override
			public Predicate toPredicate(Root<WechatGroupMembers> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder)
			{
				List<Predicate> list = new ArrayList<Predicate>();
				if (!StringUtils.isEmpty(groupName))
				{
					list.add(criteriaBuilder.equal(root.get("groupName").as(String.class), groupName));
				}
				Predicate[] p = new Predicate[list.size()];
				return criteriaBuilder.and(list.toArray(p));
			}
		}, pageable);
		List<WechatGroupMembers> list = page.getContent();
		List outlist = new ArrayList();
		Calendar cal = Calendar.getInstance();
		for (WechatGroupMembers wm : list)
		{
			Map map = new HashMap();
			Date createTime = wm.getCreatetime();
			cal.setTime(createTime);
			cal.add(Calendar.DATE, -1);
			map.put("groupname", CommonUtils.toString(wm.getGroupName()));
			map.put("groupid", CommonUtils.toString(wm.getGroupId()));
			map.put("date", CommonUtils.toString(cal.getTime()));
			map.put("members", CommonUtils.toString(wm.getMembers()));
			map.put("msgs", CommonUtils.toString(wm.getMsgs()));
			outlist.add(map);
		}

		Map map = new HashMap();
		map.put("totalNum", outlist.size());
		od.setReturncode("true");
		od.setReturnmsg("获取成功");
		od.setOutlist(outlist);
		od.setOutmap(map);
		return od;
	}

	@Override
	public OutData getGrpUserRecord(String groupName) throws Exception
	{
		OutData od = new OutData();
		if (StringUtils.isEmpty(groupName))
		{
			od.setReturncode("false");
			od.setReturnmsg("群名称为空！");
			return od;
		}
		WechatGroup wg = wechatGroupRepository.getWechatGroupByName(groupName);

		if (wg == null)
		{
			od.setReturncode("false");
			od.setReturnmsg("群数据获取失败!");
			return od;
		}
		String wgId = wg.getGroupId();
		WXGroup wxGroup = MyWeChatListener.wechatClient.userGroup(wgId);
		if (wxGroup == null)
		{
			od.setReturncode("false");
			od.setReturnmsg("微信群数据获取失败!");
			return od;
		}
		Map<String, Member> members = wxGroup.members;
		Map<String, WechatUser> userMap = WechatGroupServiceImp.userMap;
		Map<String, List> seperateMap = new HashMap<String, List>();
		for (Entry<String, Member> entry : members.entrySet())
		{
			String memberId = entry.getKey();
			Member member = entry.getValue();
			WechatUser wu = userMap.get(memberId);
			if (wu == null)
			{
				wu = new WechatUser();
				wu.setUserName(member.name);
				wu.setFromgroupSize(1);
				wu.setFromgroupName(wxGroup.name);
			}
			String fromgroupSize = "" + wu.getFromgroupSize();
			if (seperateMap.containsKey(fromgroupSize))
			{
				List list = (List) seperateMap.get(fromgroupSize);
				list.add(wu);
			}
			else
			{
				List list = new ArrayList();
				list.add(wu);
				seperateMap.put(fromgroupSize, list);
			}
		}
		List outList = new ArrayList();

		for (Entry<String, List> entry : seperateMap.entrySet())
		{
			Map map = new HashMap();
			String fromgroupSize = entry.getKey();
			List<WechatUser> list = entry.getValue();
			map.put("fromgroupSize", fromgroupSize);
			map.put("fromgroupNum", list.size() + "");

			List detailList = new ArrayList();
			for (WechatUser wu : list)
			{
				Map detailMap = new HashMap();
				detailMap.put("fromgroupName", wu.getFromgroupName());
				detailMap.put("userName", wu.getUserName());
				detailList.add(detailMap);
			}
			map.put("detail", detailList);
			outList.add(map);
		}

		Map map = new HashMap();
		map.put("totalFromgroupNum", members.size());
		map.put("totalNum", outList.size());
		
		od.setReturncode("true");
		od.setReturnmsg("获取成功");
		od.setOutlist(outList);
		od.setOutmap(map);
		return od;
	}

}
