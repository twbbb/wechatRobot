package com.twb.wechatrobot.service;

import com.aliyun.openservices.ons.api.SendResult;
import com.twb.commondata.data.CommitchainMqData;

public interface MqProductService
{
	SendResult sendCommitChainMQ(CommitchainMqData data);
}
