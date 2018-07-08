package com.twb.wechatrobot.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//使用JPA注解配置映射关系
@Entity // 告诉JPA这是一个实体类（和数据表映射的类）
@Table(name = "groupmessage_log") // @Table来指定和哪个数据表对应;如果省略默认表名就是user；
public class GroupMessageLog
{

	@Id // 这是一个主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
	private Integer id;

//	private Integer groupmessageId;// 关联群消息

	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime; // 消息时间戳

	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "groupmessage_id")
	private GroupMessage gm;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

//	public Integer getGroupmessageId()
//	{
//		return groupmessageId;
//	}
//
//	public void setGroupmessageId(Integer groupmessageId)
//	{
//		this.groupmessageId = groupmessageId;
//	}

	public Date getCreatetime()
	{
		return createtime;
	}

	public void setCreatetime(Date createtime)
	{
		this.createtime = createtime;
	}

	public GroupMessage getGm()
	{
		return gm;
	}

	public void setGm(GroupMessage gm)
	{
		this.gm = gm;
	}


}
