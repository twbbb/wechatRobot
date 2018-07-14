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
import com.twb.wechatrobot.service.AdMessageService;
import com.twb.wechatrobot.service.msghandler.imp.AdMsgHandler;
import com.twb.wechatrobot.service.msghandler.imp.QaMsgHandler;

@RestController
@RequestMapping("/wechat/adMessage")
@CrossOrigin
public class AdMessageController
{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AdMessageService adMessageServiceImp;

	@RequestMapping("/clearCache")
	@ResponseBody
	public String clearCache()
	{
		logger.info("============>clearCache");
		AdMsgHandler.adList.clear();
		QaMsgHandler.qaMap.clear();
		return "success";
	}

	@RequestMapping("/getAdMsg")
	@ResponseBody
	public OutData getAdMsg(@RequestBody InData inData)
	{
		logger.info("============>getAdMsg");
		OutData outData = new OutData();
		try
		{
			if (inData == null || inData.getInmap() == null)
			{
				outData.setReturncode("false");
				outData.setReturnmsg("数据为空");
				return outData;
			}
			outData = adMessageServiceImp.getAdMsgRecord(inData.getInmap());
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}

		return outData;
	}

	@RequestMapping("/delMember")
	@ResponseBody
	public OutData delMember(@RequestBody InData inData)
	{
		logger.info("============>delMember");
		OutData outData = new OutData();
		try
		{
			if (inData == null || inData.getInmap() == null)
			{
				outData.setReturncode("false");
				outData.setReturnmsg("数据为空");
				return outData;
			}
			outData = adMessageServiceImp.delMember(inData.getInmap());
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}

		return outData;
	}

	@RequestMapping("/notDel")
	@ResponseBody
	public OutData notDel(@RequestBody InData inData)
	{
		logger.info("============>notDel");
		OutData outData = new OutData();
		try
		{
			if (inData == null || inData.getInmap() == null)
			{
				outData.setReturncode("false");
				outData.setReturnmsg("数据为空");
				return outData;
			}
			outData = adMessageServiceImp.notDel(inData.getInmap());
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}

		return outData;
	}

	@RequestMapping("/getDelMemberLog")
	@ResponseBody
	public OutData getDelMemberLog(@RequestBody InData inData)
	{
		logger.info("============>getDelMemberLog");
		OutData outData = new OutData();
		try
		{
			if (inData == null || inData.getInmap() == null)
			{
				outData.setReturncode("false");
				outData.setReturnmsg("数据为空");
				return outData;
			}
			outData = adMessageServiceImp.getDelMemberLog(inData.getInmap());
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}

		return outData;
	}

}
