package com.twb.wechatrobot.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.twb.wechatrobot.data.HttpClientResponseBean;


public class HttpClientUtils
{
	
	public static HttpClientResponseBean sendGet(String getUrl) throws HttpException, IOException
	{
		HttpClientResponseBean hcrb = new HttpClientResponseBean();
		HttpClient client = new HttpClient();
		GetMethod httpget = new GetMethod(getUrl);

		try
		{
//			httpget.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
			httpget.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 10000);
			client.executeMethod(httpget);
			int statusCode = httpget.getStatusCode();
			String responseBody = httpget.getResponseBodyAsString();
			hcrb.setResponseBody(responseBody);
			hcrb.setStatusCode(statusCode);
		}
		finally
		{
			httpget.releaseConnection();
		}

		return hcrb;
	}

}
