package com.anjbo.utils;

import java.security.MessageDigest;
import java.util.UUID;

/***
 * 工具类
 * @author zhuzj
 *
 */
public class UidUtil {
	
	public static String getUuid(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	public static String getUuid(String code){
		return code+UUID.randomUUID().toString().replace("-", "");
	}
	
	public static String getIdentityID(String code){
		StringBuffer id = new StringBuffer(code);
		id.append("_");
		id.append(UUID.randomUUID());
		return encrypt(id.toString());
	}
	
	private final static String encrypt(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
