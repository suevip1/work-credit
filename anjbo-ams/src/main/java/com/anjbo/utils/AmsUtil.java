package com.anjbo.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.anjbo.common.AnjboException;
import com.anjbo.common.Constants;
import com.anjbo.common.Enums;
import com.anjbo.common.RespStatus;
import com.anjbo.common.RespStatusEnum;
/**
 * ams系统工具（发送短信及邮件）
 * @author limh limh@zxsf360.com
 * @date 2016-6-2 下午07:18:42
 */
public class AmsUtil {
	private static final Logger log = Logger.getLogger(AmsUtil.class);
	public static final Map<String, String> smsMap = new HashMap<String, String>();

	static {
		Properties p = new Properties();
		try {
			p.load(new InputStreamReader(ConfigUtil.class.getClassLoader().getResourceAsStream(
					"sms.properties"),"utf-8"));

			for (Entry<Object, Object> tmp : p.entrySet()) {
				smsMap.put((String) tmp.getKey(), (String) tmp.getValue());
			}
		} catch (IOException e) {
			log.error("加载配置文件:sms.properties出错!", e);
		}
	}
	private static final String SMSSOURCE = "【四海恒通】";
	private static final String SMSURL2 = "smsSend2.act";
	private static final String EMAILSEND = "emailSend.act";
	public static void main(String[] args) throws Exception {
		smsSendNoLimit("15112347841", Constants.SMSCOMEFROM_TEST,"Mark");
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("phone", "15112347841");
//		String smsIpWhite = "http://192.168.1.74:8806";//ConfigUtil.getStringValue(Enums.GLOBAL_CONFIG_KEY.AMS_SMS_IPWHITE.toString());
//		params.put("ip", smsIpWhite);
//		params.put("smsContent",URLEncoder.encode(SMSSOURCE + "尊敬的xx先生/女士：您在我司办理的抵押贷业务已成功放款，放款金额xx元，请注意查收。本次业务借款期限为xx期。每期的还款日为每月xx日","UTF-8"));
//		String smsComeFrom = Constants.SMSCOMEFROM+Constants.SMSCOMEFROM_TEST;
//		params.put("smsComeFrom",smsComeFrom);
//		params.put("amsCode","hyt");
//		ConfigUtil.getStringValue(Enums.GLOBAL_CONFIG_KEY.AMS_URL.toString());
//		String url = "http://192.168.1.74:8806/uuams/smsSend4.act"+"?"+getHttpUrl(params);
//		String result = StringUtils.EMPTY;
//		result = HttpUtil.get(url);
//		log.info("result=="+result);
//		System.out.println(url);
	}
	/**
	 * 发送短信
	 * @param phone 手机号
	 * @param ip 客户端ip地址
	 * @param smsContent 短信内容
	 * @param smsComeFrom 来源标记（自定义）
	 * @return
	 * @throws AnjboException
	 */
	private static RespStatus smsSend(String phone, String ip, String smsContent,
			String smsComeFrom) throws AnjboException {
		if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(smsContent)) {
			throw new AnjboException(
					RespStatusEnum.PARAMETER_ERROR.getCode(),
					RespStatusEnum.PARAMETER_ERROR.getMsg());
		}
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("phone", phone);
			params.put("ip", ip);
			params.put("smsContent",URLEncoder.encode(SMSSOURCE + smsContent,"UTF-8"));
			params.put("smsComeFrom",smsComeFrom);
			String result = StringUtils.EMPTY;
			String url = ConfigUtil.getStringValue(Enums.GLOBAL_CONFIG_KEY.AMS_URL.toString())+SMSURL2+"?"+getHttpUrl(params);
			result = HttpUtil.get(url);
			log.info("result=="+result);
			if (result == null) {
				return new RespStatus(RespStatusEnum.REQUEST_TIMEOUT.getCode(),
						RespStatusEnum.REQUEST_TIMEOUT.getMsg());
			}
			Map<String, String> map = JsonUtil.parseAMSResult(result);
			int code = Integer.valueOf(map.get("code"));
			if (code == 0) {
				return new RespStatus(RespStatusEnum.SUCCESS.getCode(),
						RespStatusEnum.SUCCESS.getMsg());
			} else if (code == -9) {
				return new RespStatus(RespStatusEnum.SMS_DAY_THREE.getCode(),
						RespStatusEnum.SMS_DAY_THREE.getMsg());
			} else if (code == -30) {
				return new RespStatus(RespStatusEnum.SMS_MONTH_FIVE.getCode(),
						RespStatusEnum.SMS_MONTH_FIVE.getMsg());
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
	public static RespStatus emailSend(String title, String email, String content) throws AnjboException{
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
			String result = StringUtils.EMPTY;
			String url = ConfigUtil.getStringValue(Enums.GLOBAL_CONFIG_KEY.AMS_URL.toString())+EMAILSEND+"?"+getHttpUrl(params);
			result = HttpUtil.get(url);
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
	private static String getHttpUrl(Map<String, String> params){
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
	public static void smsSend(String phone,String ip,String constantsKey,Object ...param){
		String smsContent = String.format(smsMap.get(constantsKey),param);
		log.info(String.format("发送短信手机号：%s；发送内容：%s",phone,smsContent));
		if(StringUtils.isEmpty(smsContent)){return;}
		String smsComeFrom = Constants.SMSCOMEFROM+constantsKey;
		try {
			smsSend(phone,ip,smsContent,smsComeFrom);
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
	public static void smsSendNoLimit(String phone,String constantsKey,Object ...param){
		String smsIpWhite = ConfigUtil.getStringValue(Enums.GLOBAL_CONFIG_KEY.AMS_SMS_IPWHITE.toString());
		smsSend(phone, smsIpWhite, constantsKey, param);
	}
}
