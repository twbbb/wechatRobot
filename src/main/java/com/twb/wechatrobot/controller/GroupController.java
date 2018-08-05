package com.twb.wechatrobot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.twb.wechatrobot.data.InData;
import com.twb.wechatrobot.data.OutData;
import com.twb.wechatrobot.service.WechatGroupShowService;

@RestController
@RequestMapping("/wechat/group")
@CrossOrigin
public class GroupController {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private WechatGroupShowService wechatGroupShowServiceImp;

  
	
  	@RequestMapping("/getAllGroup/{page}/{pagesize}")
	@ResponseBody
	public OutData getAllGroup(@PathVariable("page") String page,@PathVariable("pagesize") String pagesize)
	{
  		logger.info("============>getAllGroup");
		OutData outData = new OutData();
		try
		{
			outData = wechatGroupShowServiceImp.getAllGroup(page,pagesize);
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}

		return outData;
	}
  	
  	
  	@RequestMapping("/getGroupRecord/{groupName}")
  	@ResponseBody
	public OutData getGroupRecord(@PathVariable("groupName") String groupName)
	{
  		logger.info("============>getGroupRecord:"+groupName);
		OutData outData = new OutData();
		try
		{
			outData = wechatGroupShowServiceImp.getGroupRecord(groupName);
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}
		
		return outData;
	}

  	
  	@RequestMapping("/getGrpUserRecord/{groupName}")
  	@ResponseBody
	public OutData getGrpUserRecord(@PathVariable("groupName") String groupName)
	{
  		logger.info("============>getGrpUserRecord:"+groupName);
		OutData outData = new OutData();
		try
		{
			outData = wechatGroupShowServiceImp.getGrpUserRecord(groupName);
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}
		
		return outData;
	}
  	
 	@RequestMapping("/downloadGrpUserRecord/{groupName}")
  	@ResponseBody
	public OutData downloadGrpUserRecord(@PathVariable("groupName") String groupName)
	{
  		logger.info("============>downloadGrpUserRecord:"+groupName);
		OutData outData = new OutData();
		try
		{
			outData = wechatGroupShowServiceImp.downloadGrpUserRecord(groupName);
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}
		
		return outData;
	}
  	
}
