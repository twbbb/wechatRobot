package com.twb.wechatrobot.entity;

import java.util.Date;

import javax.persistence.*;

//使用JPA注解配置映射关系
@Entity // 告诉JPA这是一个实体类（和数据表映射的类）
@Table(name = "wechat_group_log") // @Table来指定和哪个数据表对应;如果省略默认表名就是user；
public class WechatGroupLog
{

	@Id // 这是一个主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
	private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime  ; // 创建时间

	private String groupId =""; //微信群组id
	private String groupName =""; // 微信群组名称

	@Temporal(TemporalType.TIMESTAMP)
	private Date groupCreatetime   ; // 群组数据创建时间

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

	public Date getGroupCreatetime()
	{
		return groupCreatetime;
	}

	public void setGroupCreatetime(Date groupCreatetime)
	{
		this.groupCreatetime = groupCreatetime;
	}
	
	
}
