package com.twb.wechatrobot.entity;

import java.util.Date;

import javax.persistence.*;


@Entity
@Table(name = "wechat_group") // 群组表
public class WechatGroup
{

	@Id // 这是一个主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
	private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime  ; // 创建时间

	private String groupId =""; //微信群组id
	private String groupName =""; // 微信群组名称
	private Integer members  ; // 微信群成员数量
	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}
	public Date getCreatetime()
	{
		return createtime;
	}
	public void setCreatetime(Date createtime)
	{
		this.createtime = createtime;
	}
	public String getGroupId()
	{
		return groupId;
	}
	public void setGroupId(String groupId)
	{
		this.groupId = groupId;
	}
	public String getGroupName()
	{
		return groupName;
	}
	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}
	public Integer getMembers()
	{
		return members;
	}
	public void setMembers(Integer members)
	{
		this.members = members;
	}

	
}
