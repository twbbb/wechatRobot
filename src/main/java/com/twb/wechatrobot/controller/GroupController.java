package com.twb.wechatrobot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.twb.wechatrobot.data.OutData;
import com.twb.wechatrobot.service.WechatGroupShowService;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@RestController
@RequestMapping("/wechat/group")
@CrossOrigin
public class GroupController {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private WechatGroupShowService wechatGroupShowServiceImp;

  	@RequestMapping("/getAllGroup")
	@ResponseBody
	public OutData getAllGroup()
	{
		OutData outData = new OutData();
		try
		{
			outData = wechatGroupShowServiceImp.getAllGroup();
		}
		catch (Exception e)
		{
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}

		return outData;
	}

}
