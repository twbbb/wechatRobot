package com.twb.wechatrobot.entity;

import java.util.Date;

import javax.persistence.*;

//使用JPA注解配置映射关系
@Entity // 告诉JPA这是一个实体类（和数据表映射的类）
@Table(name = "ad_key") // @Table来指定和哪个数据表对应;如果省略默认表名就是user；
public class AdKey
{

	public static final String MESSAGETYPE_TEXT = "1";// 1.文本

	@Id // 这是一个主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
	private Integer id;


	private String keyword =""; //关键字


	public Integer getId()
	{
		return id;
	}


	public void setId(Integer id)
	{
		this.id = id;
	}


	public String getKeyword()
	{
		return keyword;
	}


	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	

}
