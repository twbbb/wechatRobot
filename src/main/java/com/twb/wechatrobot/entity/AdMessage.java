package com.twb.wechatrobot.entity;

import java.util.Date;

import javax.persistence.*;

//使用JPA注解配置映射关系
@Entity // 告诉JPA这是一个实体类（和数据表映射的类）
@Table(name = "admessage") // @Table来指定和哪个数据表对应;如果省略默认表名就是user；
public class AdMessage
{

	public static final String MESSAGETYPE_TEXT = "1";// 1.文本
	public static final String MESSAGETYPE_VOICE = "2";// 2.语音
	public static final String MESSAGETYPE_IMAGE = "3";// 3.图片
	public static final String MESSAGETYPE_LINK = "4";// 4.链接
	
	public static final String DELETE_STATE_DEFUALT = "0";// 0.默认状态
	public static final String DELETE_STATE_AD_DELETE = "1";// 1.已踢掉
	public static final String DELETE_STATE_DELETE = "2";// 2.附带被踢
	public static final String DELETE_STATE_NOT_AD = "3";// 3.不踢

	public static final String ISOVERDUE_NO = "0";// 0.未过期
	public static final String ISOVERDUE = "1";// 1.已过期
	
	
	public static final String ISOWNER = "1";// 1.是群主
	public static final String ISOWNER_NO = "2";// 2.不是群主
	
	@Id // 这是一个主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
	private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp ; // 消息时间戳

	private String msgid =""; //消息id
	private String wxgroupName =""; // 微信群组名称
	private String fromuserName =""; // '微信群组发送者名称'
	private String messageType  =""; // 消息类型表，1文本，2语音，3图片4链接
	private String contentText  =""; // 消息内容,文本，语音转换的文本,链接标题',
	private String contentFile  =""; //消息内容，语音路径,图片路径',
	private String contentLink  =""; //  '消息内容，链接地址'
	private String isowner   = "";//是否是群主
	private String wxgroupId =""; // 微信群组id
	private String fromuserId =""; // 微信群组发送者id
	
	private String deleteState=DELETE_STATE_DEFUALT; //处理状态
	private String isOverdue=ISOVERDUE_NO; //数据是否过期0.未过期1.已过期
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date handlerTime; // 处理时间
	
	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}
	public Date getTimestamp()
	{
		return timestamp;
	}
	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}
	public String getMsgid()
	{
		return msgid;
	}
	public void setMsgid(String msgid)
	{
		this.msgid = msgid;
	}
	public String getWxgroupName()
	{
		return wxgroupName;
	}
	public void setWxgroupName(String wxgroupName)
	{
		this.wxgroupName = wxgroupName;
	}
	public String getFromuserName()
	{
		return fromuserName;
	}
	public void setFromuserName(String fromuserName)
	{
		this.fromuserName = fromuserName;
	}
	public String getMessageType()
	{
		return messageType;
	}
	public void setMessageType(String messageType)
	{
		this.messageType = messageType;
	}
	public String getContentText()
	{
		return contentText;
	}
	public void setContentText(String contentText)
	{
		this.contentText = contentText;
	}
	public String getContentFile()
	{
		return contentFile;
	}
	public void setContentFile(String contentFile)
	{
		this.contentFile = contentFile;
	}
	public String getContentLink()
	{
		return contentLink;
	}
	public void setContentLink(String contentLink)
	{
		this.contentLink = contentLink;
	}
	public String getIsowner()
	{
		return isowner;
	}
	public void setIsowner(String isowner)
	{
		this.isowner = isowner;
	}
	public String getWxgroupId()
	{
		return wxgroupId;
	}
	public void setWxgroupId(String wxgroupId)
	{
		this.wxgroupId = wxgroupId;
	}
	public String getFromuserId()
	{
		return fromuserId;
	}
	public void setFromuserId(String fromuserId)
	{
		this.fromuserId = fromuserId;
	}
	public Date getHandlerTime()
	{
		return handlerTime;
	}
	public void setHandlerTime(Date handlerTime)
	{
		this.handlerTime = handlerTime;
	}
	public String getIsOverdue()
	{
		return isOverdue;
	}
	public void setIsOverdue(String isOverdue)
	{
		this.isOverdue = isOverdue;
	}
	public String getDeleteState()
	{
		return deleteState;
	}
	public void setDeleteState(String deleteState)
	{
		this.deleteState = deleteState;
	}

	

}
