package com.anjbo.utils.umeng;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anjbo.utils.umeng.android.AndroidBroadcast;
import com.anjbo.utils.umeng.android.AndroidCustomizedcast;
import com.anjbo.utils.umeng.android.AndroidFilecast;
import com.anjbo.utils.umeng.android.AndroidGroupcast;
import com.anjbo.utils.umeng.android.AndroidUnicast;
import com.anjbo.utils.umeng.ios.IOSBroadcast;
import com.anjbo.utils.umeng.ios.IOSCustomizedcast;
import com.anjbo.utils.umeng.ios.IOSFilecast;
import com.anjbo.utils.umeng.ios.IOSGroupcast;
import com.anjbo.utils.umeng.ios.IOSUnicast;

public class Demo {
	private String appkey = null;
	private String appMasterSecret = null;
	private PushClient client = new PushClient();
	
	public Demo(String key, String secret) {
		try {
			appkey = key;
			appMasterSecret = secret;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void sendAndroidBroadcast() throws Exception {
		AndroidBroadcast broadcast = new AndroidBroadcast(appkey,appMasterSecret);
		broadcast.setTicker( "Android broadcast ticker");
		broadcast.setTitle(  "中文的title");
		broadcast.setText(   "Android broadcast text");
		broadcast.goAppAfterOpen();
		broadcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		broadcast.setProductionMode();
		broadcast.setExtraField("test", "helloworld");
		client.send(broadcast);
	}
	
	public void sendAndroidUnicast() throws Exception {
		AndroidUnicast unicast = new AndroidUnicast(appkey,appMasterSecret);
		unicast.setDeviceToken( "AhmPnplDbAxPvJN_VbCfO2brVSqr2zRoGNSx_mPcLwfm");
		unicast.setTicker( "Android unicast ticker");
		unicast.setTitle(  "中文的title");
		unicast.setText(   "Android unicast text");
		unicast.goAppAfterOpen();
		unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		unicast.setProductionMode();
		unicast.setExtraField("test", "helloworld");
		client.send(unicast);
	}
	
	public void sendAndroidGroupcast() throws Exception {
		AndroidGroupcast groupcast = new AndroidGroupcast(appkey,appMasterSecret);
		JSONObject filterJson = new JSONObject();
		JSONObject whereJson = new JSONObject();
		JSONArray tagArray = new JSONArray();
		JSONObject testTag = new JSONObject();
		JSONObject TestTag = new JSONObject();
		testTag.put("tag", "test");
		TestTag.put("tag", "Test");
		tagArray.put(testTag);
		tagArray.put(TestTag);
		whereJson.put("and", tagArray);
		filterJson.put("where", whereJson);
		System.out.println(filterJson.toString());
		
		groupcast.setFilter(filterJson);
		groupcast.setTicker( "Android groupcast ticker");
		groupcast.setTitle(  "中文的title");
		groupcast.setText(   "Android groupcast text");
		groupcast.goAppAfterOpen();
		groupcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		groupcast.setProductionMode();
		client.send(groupcast);
	}
	
	public void sendAndroidCustomizedcast() throws Exception {
		AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(appkey,appMasterSecret);
		customizedcast.setAlias("alias", "alias_type");
		customizedcast.setTicker( "AhmPnplDbAxPvJN_VbCfO2brVSqr2zRoGNSx_mPcLwfm");
		customizedcast.setTitle(  "中文的title");
		customizedcast.setText(   "Android customizedcast text");
		customizedcast.goAppAfterOpen();
		customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		customizedcast.setProductionMode();
		client.send(customizedcast);
	}
	
	public void sendAndroidCustomizedcastFile() throws Exception {
		AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(appkey,appMasterSecret);
		String fileId = client.uploadContents(appkey,appMasterSecret,"aa"+"\n"+"bb"+"\n"+"alias");
		customizedcast.setFileId(fileId, "alias_type");
		customizedcast.setTicker( "Android customizedcast ticker");
		customizedcast.setTitle(  "中文的title");
		customizedcast.setText(   "Android customizedcast text");
		customizedcast.goAppAfterOpen();
		customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		customizedcast.setProductionMode();
		client.send(customizedcast);
	}
	
	public void sendAndroidFilecast() throws Exception {
		AndroidFilecast filecast = new AndroidFilecast(appkey,appMasterSecret);
		String fileId = client.uploadContents(appkey,appMasterSecret,"aa"+"\n"+"bb");
		filecast.setFileId( fileId);
		filecast.setTicker( "Android filecast ticker");
		filecast.setTitle(  "中文的title");
		filecast.setText(   "Android filecast text");
		filecast.goAppAfterOpen();
		filecast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		client.send(filecast);
	}
	
	public void sendIOSBroadcast() throws Exception {
		IOSBroadcast broadcast = new IOSBroadcast(appkey,appMasterSecret);
		broadcast.setAlert("IOS 广播测试");
		broadcast.setBadge( 0);
		broadcast.setSound( "default");
		broadcast.setTestMode();
		broadcast.setCustomizedField("test", "helloworld");
		client.send(broadcast);
	}
	
	public void sendIOSUnicast() throws Exception {
		IOSUnicast unicast = new IOSUnicast(appkey,appMasterSecret);
		unicast.setDeviceToken( "28347bf0bcf2b805f68136e4c54c4e25b0076d0a90ff97d1ba24397cb324aee3");
		unicast.setAlert("IOS 单播测试");
		unicast.setBadge( 0);
		unicast.setSound( "default");
		unicast.setTestMode();
		unicast.setCustomizedField("test", "helloworld");
		client.send(unicast);
	}
	
	public void sendIOSGroupcast() throws Exception {
		IOSGroupcast groupcast = new IOSGroupcast(appkey,appMasterSecret);
		JSONObject filterJson = new JSONObject();
		JSONObject whereJson = new JSONObject();
		JSONArray tagArray = new JSONArray();
		JSONObject testTag = new JSONObject();
		testTag.put("tag", "iostest");
		tagArray.put(testTag);
		whereJson.put("and", tagArray);
		filterJson.put("where", whereJson);
		System.out.println(filterJson.toString());
		groupcast.setFilter(filterJson);
		groupcast.setAlert("IOS 组播测试");
		groupcast.setBadge( 0);
		groupcast.setSound( "default");
		groupcast.setTestMode();
		client.send(groupcast);
	}
	
	public void sendIOSCustomizedcast() throws Exception {
		IOSCustomizedcast customizedcast = new IOSCustomizedcast(appkey,appMasterSecret);
		customizedcast.setAlias("alias", "alias_type");
		customizedcast.setAlert("IOS 个性化测试");
		customizedcast.setBadge( 0);
		customizedcast.setSound( "default");
		customizedcast.setTestMode();
		client.send(customizedcast);
	}
	
	public void sendIOSFilecast() throws Exception {
		IOSFilecast filecast = new IOSFilecast(appkey,appMasterSecret);
		// TODO upload your device tokens, and use '\n' to split them if there are multiple tokens 
		String fileId = client.uploadContents(appkey,appMasterSecret,"aa"+"\n"+"bb");
		filecast.setFileId( fileId);
		filecast.setAlert("IOS 文件播测试");
		filecast.setBadge( 0);
		filecast.setSound( "default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		filecast.setTestMode();
		client.send(filecast);
	}
	
	public static void main(String[] args) {
		// TODO set your appkey and master secret here
		Demo demo = new Demo("your appkey", "the app master secret");
		try {
			demo.sendAndroidUnicast();
			/* TODO these methods are all available, just fill in some fields and do the test
			 * demo.sendAndroidCustomizedcastFile();
			 * demo.sendAndroidBroadcast();
			 * demo.sendAndroidGroupcast();
			 * demo.sendAndroidCustomizedcast();
			 * demo.sendAndroidFilecast();
			 * 
			 * demo.sendIOSBroadcast();
			 * demo.sendIOSUnicast();
			 * demo.sendIOSGroupcast();
			 * demo.sendIOSCustomizedcast();
			 * demo.sendIOSFilecast();
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

}
