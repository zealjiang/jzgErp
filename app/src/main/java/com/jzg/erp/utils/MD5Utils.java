/**
 * Project Name:JZGPingGuShi
 * File Name:MD5Utils.java
 * Package Name:com.gc.jzgpinggushi.uitls
 * Date:2014-9-1上午10:38:59
 * Copyright (c) 2014, wangyd523@gmail.com All Rights Reserved.
 *
 */

package com.jzg.erp.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * ClassName:MD5Utils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2014-9-1 上午10:38:59 <br/>
 * 
 * @author 汪渝栋
 * @version
 * @since JDK 1.6
 * @see
 */
public class MD5Utils
{

	/*public final static String MD5(String s)
	{
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try
		{
			byte[] strTemp = s.getBytes("GB2312");
			// byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++)
			{
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toUpperCase();
		} catch (Exception e)
		{
			return null;
		}
	}*/

	/**
	 * MD5加密 MD5Encrypt: <br/>
	 * 
	 * @author wang
	 * @param inStr
	 * @return
	 * @since JDK 1.6
	 */
	public static String MD5Encrypt(String inStr)
	{

		MessageDigest md = null;
		String outStr = null;
		try
		{

			md = MessageDigest.getInstance("MD5"); // 可以选中其他的算法如SHA
			byte[] digest = md.digest(inStr.getBytes());
			// 返回的是byet[]，要转化为String存储比较方便
			outStr = bytetoString(digest);
		} catch (NoSuchAlgorithmException nsae)
		{
			nsae.printStackTrace();
		}
		return outStr;
	}

	public static String bytetoString(byte[] digest)
	{

		String str = "";
		String tempStr = "";
		for (int i = 1; i < digest.length; i++)
		{
			tempStr = (Integer.toHexString(digest[i] & 0xff));
			if (tempStr.length() == 1)
			{
				str = str + "0" + tempStr;
			} else
			{
				str = str + tempStr;
			}
		}
		return str.toLowerCase();

	}

	public static final String PRIVATE_KEY_XIANQIAN = "2CB3147B-D93C-964B-47AE-EEE448C84E3C"
			.toLowerCase();
	private static final String PRIVATE_KEY = "3CEA5FCD-451F-4B97-87B6-A2C6C7C8A5FD"
			.toLowerCase();

	/**
	 * 
	 * getMD5Sign
	 * 
	 * @Title: getMD5Sign
	 * @Description: 获取MD5加密后的sign字符串
	 * @param @param params 需要传递的参数Map集合，无参状态下可以为null
	 * @return String
	 * @throws
	 */
	public static String getMD5Sign(Map<String, Object> params)
	{
		//params.put("tokenid","6");//根据项目加密
		if (params == null || params.size() <= 0)
			return newMD5(PRIVATE_KEY);

		StringBuffer signValue = new StringBuffer();
		List<Map.Entry<String, Object>> infos = sortTreeMap(params);
		for (int i = 0; i < infos.size(); i++)
		{
			if(!TextUtils.isEmpty(infos.get(i).toString())){
				String value = infos.get(i).toString();
				signValue.append(value);
			}


		}
		signValue.append(PRIVATE_KEY);
		return newMD5(signValue.toString().toLowerCase());
	}


	/**
	 *
	 * getMD5Sign
	 *
	 * @Title: getMD5Sign
	 * @Description: 获取MD5加密后的sign字符串
	 * @param @param params 需要传递的参数Map集合，无参状态下可以为null
	 * @return String
	 * @throws
	 */
	public static String getMD5Sign(Map<String, Object> params,String Key)
	{
	//	Key = Key.toLowerCase();
		//params.put("tokenid","6");//根据项目加密
		if (params == null || params.size() <= 0)
			return newMD5(Key.toLowerCase());

		StringBuffer signValue = new StringBuffer();
		List<Map.Entry<String, Object>> infos = sortTreeMap(params);
		for (int i = 0; i < infos.size(); i++)
		{
			String value = infos.get(i).toString();
			signValue.append(value);
		}
		signValue.append(Key.toLowerCase());

		return newMD5(signValue.toString().toLowerCase());
	}

	/**
	 * 
	 * sortTreeMap
	 * 
	 * @Title: sortTreeMap
	 * @Description: 对参数列表进行排序（按照key的字母大小）
	 * @param @param params
	 * @param @return
	 * @return Map<String,Object>
	 * @throws
	 */
	private static List<Map.Entry<String, Object>> sortTreeMap(
			Map<String, Object> params)
	{
		List<Map.Entry<String, Object>> entrys = new ArrayList<Map.Entry<String, Object>>(
				params.entrySet());

		// 排序
		Collections.sort(entrys, new Comparator<Map.Entry<String, Object>>()
		{
			public int compare(Map.Entry<String, Object> o1,
					Map.Entry<String, Object> o2)
			{
				return (o1.getKey()).toLowerCase().compareTo(
						o2.getKey().toLowerCase());
			}
		});

		return entrys;
	}

	/**
	 * 
	 * MD5
	 * 
	 * @Title: MD5
	 * @Description: MD5加密方法
	 * @param @param s 需要加密的字符串
	 * @param @return
	 * @return String
	 * @throws
	 */
	public final static String newMD5(String s)
	{
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try
		{
			byte[] strTemp = s.getBytes("UTF-8");
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++)
			{
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toUpperCase();
		} catch (Exception e)
		{
			return null;
		}
	}

	public  static String encode(String password){
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] result = digest.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : result) {
				int number = b & 0xff; //加盐 | 0x23
				String str = Integer.toHexString(number);
				if(str.length()==1){
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}
}
