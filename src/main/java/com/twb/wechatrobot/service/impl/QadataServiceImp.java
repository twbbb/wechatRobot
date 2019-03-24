package com.twb.wechatrobot.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.twb.wechatrobot.data.HttpClientResponseBean;
import com.twb.wechatrobot.data.OutData;
import com.twb.wechatrobot.repository.QadataRepository;
import com.twb.wechatrobot.service.QadataService;
import com.twb.wechatrobot.service.msghandler.imp.QaMsgHandler;
import com.twb.wechatrobot.utils.HttpClientUtils;

@Service
public class QadataServiceImp implements QadataService
{

	private static final Logger logger = LoggerFactory.getLogger(QadataServiceImp.class);

	@Autowired
	QadataRepository qadataRepository;
	
	@Value("${swtcurl}")
	private String swtcurl;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public OutData updateSwtcPrice() throws Exception {
		HttpClientResponseBean hcrb = HttpClientUtils.sendGet(swtcurl);
		
		OutData od = new OutData();
		String waveStr = "";
		String answer = "";
				
		if (hcrb.getStatusCode() == 200)
		{
		
			String body = hcrb.getResponseBody();
			Map map = (Map) JSON.parse(body);
			boolean success = (boolean) map.get("success");
			List data = (List) map.get("data");
			BigDecimal price = new BigDecimal((String) data.get(1));
			BigDecimal wave = (BigDecimal) data.get(2);
			BigDecimal highPrice = new BigDecimal((String) data.get(3));
			BigDecimal lowPrice = new BigDecimal((String) data.get(4));
			String priceStr = price.setScale(5, BigDecimal.ROUND_HALF_UP).toString();
			String highPriceStr = highPrice.setScale(5, BigDecimal.ROUND_HALF_UP).toString();
			String lowPriceStr = lowPrice.setScale(5, BigDecimal.ROUND_HALF_UP).toString();
			waveStr = wave.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
			String now =  (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new Date());
			
			answer =String.format( "swtc价格:￥%s,涨幅:%s%%,当日最高价:￥%s,当日最低价:￥%s	\r\n【%s】",priceStr,waveStr,highPriceStr,lowPriceStr,now);
			qadataRepository.updatePrice(answer,"swtc报价");
			QaMsgHandler.qaMap.clear();
		}
		
		if(StringUtils.isEmpty(waveStr))
		{
			logger.error("updateSwtcPrice 失败:"+hcrb.toString());
			od.setReturncode("Fail");
			od.setReturnmsg("失败");
		}
		else
		{
			od.setReturncode("Success");
			od.setReturnmsg("成功");
			Map map = new HashMap();
			map.put("waveStr", waveStr);
			map.put("answer", answer);
			od.setOutmap(map);
		}
		return od;
		
	
		
	}

}
