package com.twb.wechatrobot.entity;

import java.util.Date;

import javax.persistence.*;

//使用JPA注解配置映射关系
@Entity // 告诉JPA这是一个实体类（和数据表映射的类）
@Table(name = "delmember_log") // @Table来指定和哪个数据表对应;如果省略默认表名就是user；
public class DelmemberLog
{

	public static final String MESSAGETYPE_TEXT = "1";// 1.文本
	public static final String MESSAGETYPE_VOICE = "2";// 2.语音
	public static final String MESSAGETYPE_IMAGE = "3";// 3.图片
	public static final String MESSAGETYPE_LINK = "4";// 4.链接

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
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date delMemberTime ; // 删除时间
	 
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
	public Date getDelMemberTime()
	{
		return delMemberTime;
	}
	public void setDelMemberTime(Date delMemberTime)
	{
		this.delMemberTime = delMemberTime;
	}

	

}
