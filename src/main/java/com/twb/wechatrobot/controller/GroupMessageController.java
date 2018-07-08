package com.twb.wechatrobot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.twb.wechatrobot.data.InData;
import com.twb.wechatrobot.data.OutData;
import com.twb.wechatrobot.service.WechatGroupMessageService;

@RestController
@RequestMapping("/wechat/groupMessage")
@CrossOrigin
public class GroupMessageController {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private WechatGroupMessageService wechatGroupMessageServiceImp;

  
	
	@RequestMapping("/getGrpMsg")
	@ResponseBody
	public OutData getGrpMsg(@RequestBody InData inData)
	{
		logger.info("============>getGrpMsg");
		OutData outData = new OutData();
		try
		{
			if (inData == null || inData.getInmap() == null)
			{
				outData.setReturncode("false");
				outData.setReturnmsg("数据为空");
				return outData;
			}
			outData = wechatGroupMessageServiceImp.getGroupMsgRecord(inData.getInmap());
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}

		return outData;
	}
	
	@RequestMapping("/sendGrpMsg")
	@ResponseBody
	public OutData sendGrpMsg(@RequestBody InData inData)
	{
		logger.info("============>sendGrpMsg");
		OutData outData = new OutData();
		try
		{
			if (inData == null || inData.getInmap() == null)
			{
				outData.setReturncode("false");
				outData.setReturnmsg("数据为空");
				return outData;
			}
			outData = wechatGroupMessageServiceImp.sendGroupMsg(inData.getInmap());
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}

		return outData;
	}
  	

	@RequestMapping("/getGrpMsgLog")
	@ResponseBody
	public OutData getGrpMsgLog(@RequestBody InData inData)
	{
		logger.info("============>getGrpMsgLog");
		OutData outData = new OutData();
		try
		{
			if (inData == null || inData.getInmap() == null)
			{
				outData.setReturncode("false");
				outData.setReturnmsg("数据为空");
				return outData;
			}
			outData = wechatGroupMessageServiceImp.getGroupMsgLog(inData.getInmap());
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}

		return outData;
	}
	
  	
  	
}
