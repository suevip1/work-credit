/**
 * 
 */
package com.anjbo.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * @author Kevin Chang
 * 
 */
public class UidUtil {
	public static String generateUUID(){
		return UUID.randomUUID().toString().replaceAll("-","");
	}

	public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
		"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
		"t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
		"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
		"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
		"W", "X", "Y", "Z" };


	/**
	 * 生成指定长度的字符串(数字型)
	 * @Author KangLG<2017年11月6日>
	 * @return
	 */
	public static String generateNo(int length) {		
		StringBuilder sbu = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sbu.append(random.nextInt(9));
		}
		return sbu.toString();

	}
	
	public static String generateShortUuid() {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = generateUUID();
		for (int i = 0; i < 16; i++) {
			String str = uuid.substring(i * 2, i * 2 + 2);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0x3E]);
		}
		return shortBuffer.toString();

	}
	
	private static Date date = new Date();
	private static int seq = 0;
	private static final int ROTATION = 9999;
	/**
	 * 生成序列号
	 * @return
	 */
	public static synchronized String generateSerialId() {
		if (seq > ROTATION) {
			seq = 0;
		}
		date.setTime(System.currentTimeMillis());
		String str = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%2$04d",
				date, seq++);
		return str;
	}

	private static Date orderIdDate = new Date();
	private static int orderIdSeq = 0;
	private static final int ORDERID_ROTATION = 99999;
	/**
	 * 生成19位订单id
	 * @return
	 */
	public static synchronized String generateOrderId() {
		if (orderIdSeq > ORDERID_ROTATION) {
			orderIdSeq = 0;
		}
		orderIdDate.setTime(System.currentTimeMillis());
		String str = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%2$05d",
				orderIdDate, orderIdSeq++);
		return str;
	}
	/**
	 * 生成UID,在这里虽然同步了还是有可能会重复,可以利用数据库唯一索引避免重复
	 * @return
	 */
	public static synchronized String generateUid(){
		try {
			Thread.sleep(1);//避免重复
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String uid = new Date().getTime()+"";
		return uid;
	}

	private static int goodIdSeq = 0;
	private static final int GOODID_ROTATION = 999;
	/**
	 * 
	 * @return
	 */
	public static synchronized String generateGoodId(String type){
		if (goodIdSeq > GOODID_ROTATION) {
			goodIdSeq = 0;
		}
		String str = type+System.currentTimeMillis()+String.format("%1$03d",goodIdSeq++);
		return str;
	}
	public static void main(String[] args) {
		final Set<String> set = new HashSet<String>();
		Thread t = null;
		List<Thread> list = new ArrayList<Thread>();
		for(int i=0;i<1000;i++){
			t = new Thread(new Runnable() {
				@Override
				public void run() {
					String str = UidUtil.generateGoodId("1-");
					System.out.println(str);
					set.add(str);
				}
			});
			t.start();
			list.add(t);
		}
		for(Thread thread:list){
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(set.size());
	}
}
