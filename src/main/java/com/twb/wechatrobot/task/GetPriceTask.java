package com.twb.wechatrobot.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.twb.wechatrobot.data.MessageGroup;
import com.twb.wechatrobot.data.OutData;
import com.twb.wechatrobot.service.QadataService;
import com.twb.wechatrobot.thread.MyWeChatListener;
import com.twb.wechatrobot.utils.GroupMessageQueue;

import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;

@Component
public class GetPriceTask {
	Logger logger = LoggerFactory.getLogger(GetPriceTask.class);

	public static double TEMP_WAVE=0;
	public static String sendId = "";
	
	
	
	@Value("${GROUPMESSAGE_FLAG}")
	private String sendName;
	
	@Value("${sendWave}")
	private Double sendWave;
	
	@Value("${sendIncreaseWave}")
	private Double sendIncreaseWave;
	
	@Autowired
	QadataService qadataServiceImp;
	
	@Scheduled(cron = "1 0 0 * * ?")
	public void clean() {

		logger.info("GetPriceTask.clean start");
		TEMP_WAVE=0;

		logger.info("GetPriceTask.clean end");

	}
	
	@Scheduled(cron = "0 */2 * * * ?")
//	@Scheduled(cron = "0 * * * * ?")
	public void getDate() {

		logger.info("GetPriceTask.getDate start");
		
		try {
			OutData od = qadataServiceImp.updateSwtcPrice();
			if(!"Success".equals(od.getReturncode()))
			{
				return;
			}
			Map map = od.getOutmap();
			String waveStr =(String) map.get("waveStr");
			String answer =(String) map.get("answer");
			if(StringUtils.isEmpty(waveStr))
			{
				return ;
			}
			double priceDou = Double.parseDouble(waveStr);
			boolean sendMsg = false;
			if(priceDou>sendWave)
			{
				if(TEMP_WAVE>=0)
				{
				  if(priceDou-TEMP_WAVE>=sendIncreaseWave)
				  {
					  TEMP_WAVE = priceDou;
					  sendMsg = true;
				  }	
				}
				
				 
			}
			
			if(sendMsg)
			{
				if(StringUtils.isEmpty(sendId))
				{
					HashMap<String, WXGroup> wxGroupMap = MyWeChatListener.wechatClient.userGroups();
					if(wxGroupMap!=null&&!wxGroupMap.isEmpty())
					{
						for(Entry<String, WXGroup> entry:wxGroupMap.entrySet())
						{
							WXGroup wxGroup = entry.getValue();
							if(sendName.equals(wxGroup.name))
							{
								sendId = wxGroup.id;
								break;
							}
						}
					}
				}
				
				if(!StringUtils.isEmpty(sendId))
				{
					MessageGroup mg = new MessageGroup();
					mg.setContent(answer);
					mg.setGroupName(sendName);
					mg.setId(sendId);
					GroupMessageQueue.add(mg);
				}
				
			}
			
		} catch (Exception e) {
			logger.error("报价更新失败",e);
			e.printStackTrace();
		}

		logger.info("GetPriceTask.getDate end");

	}
}
