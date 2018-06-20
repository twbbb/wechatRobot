package com.twb.wechatrobot.task;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.twb.wechatrobot.data.MyWeChatListener;
import com.twb.wechatrobot.service.WechatGroupService;
import com.twb.wechatrobot.service.WechatMessageService;

import me.xuxiaoxiao.chatapi.wechat.WeChatClient;

@Component
public class WechatRobotTask
{
	Logger logger = LoggerFactory.getLogger(WechatRobotTask.class);

	@Autowired
	WechatMessageService wechatMessageServiceImp;
	
	@Autowired
	WechatGroupService wechatGroupServiceImp;

	 @Value("${file_dir}")
	 private String file_dir;
	 
	// 定义在构造方法完毕后，执行这个初始化方法
	@PostConstruct
	public void init()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); 
		String day = df.format(new Date());
		File file = new File(file_dir+File.separator+day);
		if(!file.exists())
		{
			file.mkdirs();
		}
		MyWeChatListener myWeChatListener = new MyWeChatListener(wechatMessageServiceImp,wechatGroupServiceImp);
		WeChatClient wechatClient = new WeChatClient(myWeChatListener);
		wechatClient.setFolder(file);
		MyWeChatListener.wechatClient = wechatClient;
		wechatClient.startup();
	}
	
}
