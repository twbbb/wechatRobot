package com.twb.wechatrobot.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.twb.wechatrobot.data.InData;
import com.twb.wechatrobot.data.OutData;

@Controller
@RequestMapping("/wechat")
@CrossOrigin
public class HomeController
{
//	@RequestMapping(
//	{ "/", "/index" })
//	public String index()
//	{
//		return "/index";
//	}
//
//	/**
//	 * 未登录，shiro应重定向到登录界面，此处返回未登录状态信息由前端控制跳转页面
//	 * @return
//	 */
//	@RequestMapping(value = "/unauth")
//	@ResponseBody
//	public OutData unauth()
//	{
////		Map<String, Object> map = new HashMap<String, Object>();
////		map.put("code", "1000000");
////		map.put("msg", "未登录");
////		return map;
//		OutData outData = new OutData();
//		outData.setReturncode("false");
//		outData.setReturnmsg("未登录");
//		return outData;
//	}

	/**
	 * 登录方法
	 * @param userInfo
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public OutData ajaxLogin(@RequestBody InData inData)
	{

		OutData outData = new OutData();
		if (inData == null || inData.getInmap() == null)
		{
			outData.setReturncode("false");
			outData.setReturnmsg("数据为空");
			return outData;
		}
		Map inMap = inData.getInmap();
		String name = (String) inMap.get("name");
		String password = (String) inMap.get("password");
		if (StringUtils.isEmpty(name))
		{
			outData.setReturncode("false");
			outData.setReturnmsg("用户名为空");
			return outData;
		}

		if (StringUtils.isEmpty(password))
		{
			outData.setReturncode("false");
			outData.setReturnmsg("密码为空");
			return outData;
		}

		Map jsonObject = new HashMap();
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(name, password);
		try
		{
			subject.login(token);
			jsonObject.put("token", subject.getSession().getId());
			// jsonObject.put("msg", "登录成功");
			outData.setOutmap(jsonObject);
			outData.setReturncode("true");
			outData.setReturnmsg("登录成功");
		}
		catch (IncorrectCredentialsException e)
		{
			// jsonObject.put("msg", "密码错误");
			outData.setReturncode("false");
			outData.setReturnmsg("密码错误");
		}
		catch (LockedAccountException e)
		{
			// jsonObject.put("msg", "登录失败，该用户已被冻结");
			outData.setReturncode("false");
			outData.setReturnmsg("登录失败，该用户已被冻结");
		}
		catch (AuthenticationException e)
		{
			// jsonObject.put("msg", "该用户不存在");
			outData.setReturncode("false");
			outData.setReturnmsg("该用户不存在");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			outData.setReturncode("false");
			outData.setReturnmsg(e.getMessage());
		}
		return outData;
	}
	
	

//	// @RequestMapping("/loginold")
//	public String login(HttpServletRequest request, Map<String, Object> map) throws Exception
//	{
//		System.out.println("HomeController.login()");
//		// 登录失败从request中获取shiro处理的异常信息。
//		// shiroLoginFailure:就是shiro异常类的全类名.
//		String exception = (String) request.getAttribute("shiroLoginFailure");
//		System.out.println("exception=" + exception);
//		String msg = "";
//		if (exception != null)
//		{
//			if (UnknownAccountException.class.getName().equals(exception))
//			{
//				System.out.println("UnknownAccountException -- > 账号不存在：");
//				msg = "UnknownAccountException -- > 账号不存在：";
//			}
//			else if (IncorrectCredentialsException.class.getName().equals(exception))
//			{
//				System.out.println("IncorrectCredentialsException -- > 密码不正确：");
//				msg = "IncorrectCredentialsException -- > 密码不正确：";
//			}
//			else if ("kaptchaValidateFailed".equals(exception))
//			{
//				System.out.println("kaptchaValidateFailed -- > 验证码错误");
//				msg = "kaptchaValidateFailed -- > 验证码错误";
//			}
//			else
//			{
//				msg = "else >> " + exception;
//				System.out.println("else -- >" + exception);
//			}
//		}
//		map.put("msg", msg);
//		// 此方法不处理登录成功,由shiro进行处理
//		return "/login";
//	}
//
//	@RequestMapping("/403")
//	@ResponseBody
//	public OutData unauthorizedRole()
//	{
//		System.out.println("------没有权限-------");
//		OutData outData = new OutData();
//		outData.setReturncode("false");
//		outData.setReturnmsg("没有权限");
//		return outData;
//
//	}

}