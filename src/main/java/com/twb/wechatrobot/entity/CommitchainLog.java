package com.twb.wechatrobot.entity;

import java.util.Date;

import javax.persistence.*;

//使用JPA注解配置映射关系
@Entity // 告诉JPA这是一个实体类（和数据表映射的类）
@Table(name = "commitchain_log") // @Table来指定和哪个数据表对应;如果省略默认表名就是user；
public class CommitchainLog
{
	public static final String STATE_SUCCESS ="1";
	public static final String STATE_FAIL ="2";

	@Id // 这是一个主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
	private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date commitchainDate   ; // 上链提交时间

	private String wechatMessageId   =""; //微信消息Id'
	private String commitchainMessageId   =""; // 上链消息id
	private String commitchainState   =""; // 上链状态1成功2失败
	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}
	public Date getCommitchainDate()
	{
		return commitchainDate;
	}
	public void setCommitchainDate(Date commitchainDate)
	{
		this.commitchainDate = commitchainDate;
	}
	public String getWechatMessageId()
	{
		return wechatMessageId;
	}
	public void setWechatMessageId(String wechatMessageId)
	{
		this.wechatMessageId = wechatMessageId;
	}
	public String getCommitchainMessageId()
	{
		return commitchainMessageId;
	}
	public void setCommitchainMessageId(String commitchainMessageId)
	{
		this.commitchainMessageId = commitchainMessageId;
	}
	public String getCommitchainState()
	{
		return commitchainState;
	}
	public void setCommitchainState(String commitchainState)
	{
		this.commitchainState = commitchainState;
	}
	
	
}
