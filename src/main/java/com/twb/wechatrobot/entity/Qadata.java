package com.twb.wechatrobot.entity;

import java.util.Date;

import javax.persistence.*;

//使用JPA注解配置映射关系
@Entity // 告诉JPA这是一个实体类（和数据表映射的类）
@Table(name = "qadata") // @Table来指定和哪个数据表对应;如果省略默认表名就是user；
public class Qadata
{


	@Id // 这是一个主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
	private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp ; // 消息时间戳

	private String question  =""; //问题
	private String answer  =""; // 答案
	private String questionParty  =""; // 问题分词
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
	public String getQuestion()
	{
		return question;
	}
	public void setQuestion(String question)
	{
		this.question = question;
	}
	public String getAnswer()
	{
		return answer;
	}
	public void setAnswer(String answer)
	{
		this.answer = answer;
	}
	public String getQuestionParty()
	{
		return questionParty;
	}
	public void setQuestionParty(String questionParty)
	{
		this.questionParty = questionParty;
	}
	

	

}
