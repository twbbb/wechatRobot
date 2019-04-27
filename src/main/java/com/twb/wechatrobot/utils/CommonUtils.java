package com.twb.wechatrobot.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils
{

	public static String htmlReplace(String str){
        str = str.replace("&ldquo;", "“");
        str = str.replace("&rdquo;", "”");
        str = str.replace("&nbsp;", " ");
        str = str.replace("&amp;", "&");
        str = str.replace("&#39;", "'");
        str = str.replace("&rsquo;", "’");
        str = str.replace("&mdash;", "—");
        str = str.replace("&ndash;", "–");
        return str;
    }
	public static String toString(Object obj)
	{
		if (obj == null)
		{
			return "";
		}
		else if (obj instanceof Timestamp || obj instanceof Date)
		{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(obj);
		}
		else if (obj instanceof BigDecimal) {
			return obj.toString();
		}
		else
		{
			return obj.toString().trim();
		}
	

	}

	
	/**
	 * 
	 * @Title: validateNumber
	 * @Description: 检查是否全数字
	 * @param @param number
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean validateNumber(String number)
	{
		boolean flag = false;
		if (number != null)
		{
			Matcher m = null;
			Pattern p = Pattern.compile("^[0-9]+$");
			m = p.matcher(number);
			flag = m.matches();
		}

		return flag;

	}
	public static int string2Int(String str, int defaultVal)
	{
		int i;
		try
		{
			i = Integer.parseInt(str);
		}
		catch (NumberFormatException e)
		{
			i = defaultVal;
		}
		return i;
	}
}
