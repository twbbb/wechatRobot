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
import com.twb.wechatrobot.service.WechatMessageShowService;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@RestController
@RequestMapping("/wechat/message")
@CrossOrigin
public class MessageController {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private WechatMessageShowService wechatMessageShowServiceImp;

  	@RequestMapping("/getMsg")
	@ResponseBody
	public OutData getMsg(@RequestBody InData inData)
	{
		OutData outData = new OutData();
		try
		{
			if (inData == null || inData.getInmap() == null)
			{
				outData.setReturncode("false");
				outData.setReturnmsg("数据为空");
				return outData;
			}
			outData = wechatMessageShowServiceImp.getMsg(inData.getInmap());
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}

		return outData;
	}

}
