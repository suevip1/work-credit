package com.anjbo.utils;

import com.anjbo.common.*;
import com.anjbo.lang.thread.SMSSender;
import com.anjbo.lang.thread.SMSSender2;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * ams系统工具（发送短信及邮件）
 * @author limh limh@zxsf360.com
 * @date 2016-6-2 下午07:18:42
 */
public class AmsUtil {
	private static final Logger log = Logger.getLogger(AmsUtil.class);
	private static final String EMAILSEND = "emailSend.act";
		
	/**
	 * 发送邮件
	 * @Title: emailSend 
	 * @param title 邮件标题
	 * @param email 接收邮件者
	 * @param content 邮件内容
	 * @return
	 * @throws AnjboException
	 * RespStatus
	 * @throws
	 */
	public static RespStatus emailSend(String title, String email, String content) throws AnjboException {
		if (StringUtils.isEmpty(title) || StringUtils.isEmpty(email) || StringUtils.isEmpty(content)) {
			throw new AnjboException(
					RespStatusEnum.PARAMETER_ERROR.getCode(),
					RespStatusEnum.PARAMETER_ERROR.getMsg());
		}
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("uid", URLEncoder.encode(title, "utf-8"));
			params.put("email", email);
			params.put("m", URLEncoder.encode(content, "utf-8"));
			params.put("flag", "");
			String url = Constants.LINK_ANJBO_AMS_URL+EMAILSEND;
			String result = SingleUtils.getRestTemplate().getForObject(url,String.class,getHttpUrl(params));
			if (result == null) {
				return new RespStatus(RespStatusEnum.REQUEST_TIMEOUT.getCode(),
						RespStatusEnum.REQUEST_TIMEOUT.getMsg());
			}
			Map<String, String> map = JsonUtil.parseAMSResult(result);
			int code = Integer.valueOf(map.get("code"));
			if (code == 0) {
				return new RespStatus(RespStatusEnum.SUCCESS.getCode(),
						RespStatusEnum.SUCCESS.getMsg());
			} else {
				return new RespStatus(RespStatusEnum.FAIL.getCode(),
						RespStatusEnum.FAIL.getMsg());
			}
		} catch (Exception e) {
			log.error("smsSend Exception.", e);
			throw new AnjboException(RespStatusEnum.SYSTEM_ERROR.getCode(),
					RespStatusEnum.SYSTEM_ERROR.getMsg());
		}
	}
	
	/**
	 * 通过map参数集，获取拼接url<br />
	 * 如 a=111,b=222,返回   a=111&b=222
	 * @author Jerry
	 * @version v1.0 2013-10-15 上午08:53:19 
	 * @param params map参数集
	 * @return 拼接后的连接
	 */
	public static String getHttpUrl(Map<String, String> params){
		StringBuilder builder = new StringBuilder();
		if (params == null) {
			return null;
		}
		int i = 1;
		int size = params.entrySet().size();
		for (Entry<String, String> entry : params.entrySet()) {
			builder.append(entry.getKey());
			builder.append("=");
			builder.append(StringUtil.trimToEmpty(entry.getValue()));
			if (i!=size){
				builder.append("&");
			} 
			i++;
		}
		return builder.toString();
	}
	/**
	 * 发送短信
	 * @param phone 手机号
	 * @param ip 客户端ip，用IpUtil类获取
	 * @param constantsKey Constants配置的发送短信来源
	 * @param param 如果短信内容有%s占位符需要传入相应的参数替换
	 */
	public static void smsSend(String phone, String ip, String constantsKey, Object...param){
		String smsContent = String.format(constantsKey, param);
		log.info(String.format("发送短信手机号：%s；发送内容：%s",phone,smsContent));
		if(StringUtils.isEmpty(smsContent)){return;}
		String smsComeFrom = SMSConstants.SMSCOMEFROM+constantsKey;
		try {
			Callable<RespStatus> callSMS = new SMSSender(phone,ip,smsContent,smsComeFrom);
			FutureTask<RespStatus> futuretask = new FutureTask<RespStatus>(callSMS);
			new Thread(futuretask, "Thread-SMS-Sender").start();
//			log.info("回执"+ futuretask.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 行业通/众视达
	 * 发送短信
	 * @param phone 手机号
	 * @param ip 客户端ip，用IpUtil类获取
	 * @param constantsKey Constants配置的发送短信来源
	 * @param  amsCode:hyt(行业通)/zsd（众视达）
	 * @param param 如果短信内容有%s占位符需要传入相应的参数替换
	 */
	public static void smsSend2(String phone, String ip, String constantsKey, String amsCode, Object...param){
		String smsContent = String.format(constantsKey, param);
		log.info(String.format("发送短信手机号：%s；发送内容：%s",phone,smsContent));
		if(StringUtils.isEmpty(smsContent)){return;}
		String smsComeFrom = SMSConstants.SMSCOMEFROM+constantsKey;
		try {
			Callable<RespStatus> callSMS = new SMSSender2(phone,ip,smsContent,smsComeFrom,amsCode);
			FutureTask<RespStatus> futuretask = new FutureTask<RespStatus>(callSMS);
			new Thread(futuretask, "Thread-SMS-Sender").start();
//			log.info("回执"+ futuretask.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 发送短信（无限制，用于内部发送）
	 * @param phone 手机号
	 * @param constantsKey Constants配置的发送短信来源
	 * @param param 如果短信内容有%s占位符需要传入相应的参数替换
	 */
	public static void smsSendNoLimit(String phone, String constantsKey, Object...param){
		String smsIpWhite = Constants.BASE_AMS_IPWHITE;
		smsSend(phone, smsIpWhite, constantsKey, param);
	}
}
