package com.twb.wechatrobot.entity;

import java.util.Date;

import javax.persistence.*;

//使用JPA注解配置映射关系
@Entity // 告诉JPA这是一个实体类（和数据表映射的类）
@Table(name = "wechat_user") // @Table来指定和哪个数据表对应;如果省略默认表名就是user；
public class WechatUser
{

	@Id // 这是一个主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
	private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime  ; // 创建时间

	private String userId  =""; //用户id
	private String userName  =""; // 用户姓名
	private String fromgroupId  =""; // 用户所属微信群组id，用逗号分隔
	private String fromgroupName  =""; // 用户所属微信群组名称，用逗号分隔
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
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public String getUserName()
	{
		return userName;
	}
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	public String getFromgroupId()
	{
		return fromgroupId;
	}
	public void setFromgroupId(String fromgroupId)
	{
		this.fromgroupId = fromgroupId;
	}
	public String getFromgroupName()
	{
		return fromgroupName;
	}
	public void setFromgroupName(String fromgroupName)
	{
		this.fromgroupName = fromgroupName;
	}

	
}
