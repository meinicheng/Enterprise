package com.sdbnet.hywy.enterprise.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.sdbnet.hywy.enterprise.R;

import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class UtilsCommon {
	public static boolean checkAccount() {
		String company = PreferencesUtil
				.getValue(PreferencesUtil.KEY_COMPANY_ID);
		String itemId = PreferencesUtil.getValue(PreferencesUtil.KEY_ITEM_ID);
		String userTel = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL);
		String pid = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_ID);
		if (TextUtils.isEmpty(company) || TextUtils.isEmpty(itemId)
				|| TextUtils.isEmpty(userTel) || TextUtils.isEmpty(pid)) {
			PreferencesUtil.initStoreData();
			return false;
		} else {
			return true;
		}
	}

	// public static String busiScanActions() {
	// List<Map<?, ?>> list = new ArrayList<Map<?, ?>>();
	// Map<String, String> map = new HashMap<String, String>();
	// map.put("actidx", "1");
	// map.put("action", "A0");
	// map.put("actname", "A0");
	// map.put("btnname", "A0");
	// map.put("actmemo", "A0");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "1");
	// map.put("action", "A001");
	// map.put("actname", "A001");
	// map.put("btnname", "A001");
	// map.put("actmemo", "A001");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "1");
	// map.put("action", "A002");
	// map.put("actname", "A002");
	// map.put("btnname", "A002");
	// map.put("actmemo", "A002");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "1");
	// map.put("action", "A003");
	// map.put("actname", "A003");
	// map.put("btnname", "A003");
	// map.put("actmemo", "A003");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "2");
	// map.put("action", "B0");
	// map.put("actname", "B0");
	// map.put("btnname", "B0");
	// map.put("actmemo", "B0");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "2");
	// map.put("action", "E0");
	// map.put("actname", "B001");
	// map.put("btnname", "B001");
	// map.put("actmemo", "B001");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "2");
	// map.put("action", "F0");
	// map.put("actname", "B002");
	// map.put("btnname", "B002");
	// map.put("actmemo", "B002");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "3");
	// map.put("action", "C0");
	// map.put("actname", "C0");
	// map.put("btnname", "C0");
	// map.put("actmemo", "C0");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "4");
	// map.put("action", "G0");
	// map.put("actname", "C001");
	// map.put("btnname", "C001");
	// map.put("actmemo", "C001");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "5");
	// map.put("action", "H0");
	// map.put("actname", "C010");
	// map.put("btnname", "C010");
	// map.put("actmemo", "C010");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "7");
	// map.put("action", "I0");
	// map.put("actname", "C030");
	// map.put("btnname", "C030");
	// map.put("actmemo", "C030");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "7");
	// map.put("action", "C03001");
	// map.put("actname", "C03001");
	// map.put("btnname", "C03001");
	// map.put("actmemo", "C03001");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "7");
	// map.put("action", "C03002");
	// map.put("actname", "C03002");
	// map.put("btnname", "C03002");
	// map.put("actmemo", "C03002");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "7");
	// map.put("action", "C03003");
	// map.put("actname", "C03003");
	// map.put("btnname", "C03003");
	// map.put("actmemo", "C03003");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "8");
	// map.put("action", "D0");
	// map.put("actname", "D0");
	// map.put("btnname", "D0");
	// map.put("actmemo", "D0");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "9");
	// map.put("action", "D001");
	// map.put("actname", "D001");
	// map.put("btnname", "D001");
	// map.put("actmemo", "D001");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "10");
	// map.put("action", "D010");
	// map.put("actname", "D010");
	// map.put("btnname", "D010");
	// map.put("actmemo", "D010");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "10");
	// map.put("action", "D01001");
	// map.put("actname", "D01001");
	// map.put("btnname", "D01001");
	// map.put("actmemo", "D01001");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("actidx", "10");
	// map.put("action", "D0100101");
	// map.put("actname", "D0100101");
	// map.put("btnname", "D0100101");
	// map.put("actmemo", "D0100101");
	// map.put("lineno", "1");
	// map.put("iscall", "1");
	// map.put("islocate", "1");
	// map.put("isscan", "1");
	// map.put("linename", "车辆追踪");
	// map.put("sign", "0");
	// map.put("startnode", "0");
	// map.put("workflow", "1,3");
	// map.put("acttype", "0");
	// list.add(map);
	//
	// try {
	// return simpleListToJsonStr(list);
	// } catch (Exception e) {
	// e.printStackTrace();
	// return "";
	// }
	// }
	//
	// public static String busiOrderItems() throws Exception {
	// List<Map<?, ?>> list = new ArrayList<Map<?, ?>>();
	// Map<String, String> map = new HashMap<String, String>();
	// map.put("hboId", "179939539643");
	// map.put("hboReceName", "王小虎");
	// map.put("hboReceTel", "15825495456");
	// map.put("hboStatus", "签收");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hboId", "151032194303");
	// map.put("hboReceName", "李华然");
	// map.put("hboReceTel", "15762084234");
	// map.put("hboStatus", "签收");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hboId", "146721675703");
	// map.put("hboReceName", "王学斌");
	// map.put("hboReceTel", "15762083235");
	// map.put("hboStatus", "拒签");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hboId", "145704191523");
	// map.put("hboReceName", "张永强");
	// map.put("hboReceTel", "15763208323");
	// map.put("hboStatus", "签收");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hboId", "143056016513");
	// map.put("hboReceName", "王飞");
	// map.put("hboReceTel", "15662960236");
	// map.put("hboStatus", "二次配送");
	// list.add(map);
	//
	// return simpleListToJsonStr(list);
	// }
	//
	// public static String busiTraces() throws Exception {
	// List<Map<?, ?>> list = new ArrayList<Map<?, ?>>();
	// Map<String, String> map = new HashMap<String, String>();
	// map.put("hoiOrderId", "179939539643917");
	// map.put("hdiName", "王小虎");
	// map.put("hboRecePlace", "快件由官渡一区 已发往 昆明中转");
	// map.put("hboReceTime", "2014-11-30 01:11");
	// map.put("hdiPlate", "滇C52678");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hoiOrderId", "151032194303910");
	// map.put("hdiName", "李华然");
	// map.put("hboRecePlace", "快件由昆明中转 已发往 东莞中心");
	// map.put("hboReceTime", "2014-12-01 09:22");
	// map.put("hdiPlate", "滇B288851");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hoiOrderId", "146721675703917");
	// map.put("hdiName", "王学斌");
	// map.put("hboRecePlace", "快件由东莞中心 已发往 深圳中心");
	// map.put("hboReceTime", "2014-12-02 02:12");
	// map.put("hdiPlate", "粤B62345");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hoiOrderId", "145704191523911");
	// map.put("hdiName", "张永强");
	// map.put("hboRecePlace", "快件由深圳中心 已发往 深圳宝安");
	// map.put("hboReceTime", "2014-12-02 05:31");
	// map.put("hdiPlate", "粤E12345");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hoiOrderId", "143056016513909");
	// map.put("hdiName", "王飞");
	// map.put("hboRecePlace", "深圳宝安区创业花园淘景大厦");
	// map.put("hboReceTime", "2014-12-03 11:51");
	// map.put("hdiPlate", "粤C77865");
	// list.add(map);
	//
	// return simpleListToJsonStr(list);
	// }
	//
	// public static String busiOrders() throws Exception {
	// List<Map<?, ?>> list = new ArrayList<Map<?, ?>>();
	// Map<String, String> map = new HashMap<String, String>();
	// map.put("hboId", "879939539643917");
	// map.put("hboReceName", "王小虎");
	// map.put("hboReceTel", "18325495456");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hboId", "876568240093911");
	// map.put("hboReceName", "张有才");
	// map.put("hboReceTel", "15762084234");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hboId", "851032194303910");
	// map.put("hboReceName", "李华然");
	// map.put("hboReceTel", "15762083235");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hboId", "846728675703917");
	// map.put("hboReceName", "王学斌");
	// map.put("hboReceTel", "15763208323");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hboId", "845704191523918");
	// map.put("hboReceName", "张永强");
	// map.put("hboReceTel", "15662960236");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hboId", "843056086583909");
	// map.put("hboReceName", "王飞");
	// map.put("hboReceTel", "13853963379");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hboId", "845704191523918");
	// map.put("hboReceName", "梁建");
	// map.put("hboReceTel", "13853990238");
	// list.add(map);
	//
	// map = new HashMap<String, String>();
	// map.put("hboId", "839745373783911");
	// map.put("hboReceName", "刘彻");
	// map.put("hboReceTel", "13853919575");
	// list.add(map);
	//
	// return simpleListToJsonStr(list);
	// }

	/**
	 * 将JAVA的MAP转换成JSON字符串， 只转换第一层数据
	 * 
	 * @param map
	 * @return
	 */
	public static String simpleMapToJsonStr(Map<?, ?> map) {
		if (map == null || map.isEmpty()) {
			return "null";
		}
		String jsonStr = "{";
		Set<?> keySet = map.keySet();
		for (Object key : keySet) {
			jsonStr += "\"" + key + "\":\"" + map.get(key) + "\",";
		}
		jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
		jsonStr += "}";
		return jsonStr;
	}

	/**
	 * 将JAVA的LIST转换成JSON字符串
	 * 
	 * @param list
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	// @SuppressWarnings("rawtypes")
	// public static String simpleListToJsonStr(List<Map<?, ?>> list)
	// throws IllegalArgumentException, IllegalAccessException {
	// if (list == null || list.size() == 0) {
	// return "[]";
	// }
	// String jsonStr = "[";
	// for (Map<?, ?> map : list) {
	// jsonStr += simpleMapToJsonStr(map) + ",";
	// }
	// jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
	// jsonStr += "]";
	// return jsonStr;
	// }

	/**
	 * 将订单执行的相关信息转换为json字符串，以便保存到草稿箱
	 * 
	 * @param action
	 * @param imgList
	 * @return
	 */

	public static ArrayList<String> strs2List(String strs) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			if (!TextUtils.isEmpty(strs)) {
				String[] array = strs.split(",");
				for (String img : array) {
					list.add(img);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return list;
	}

	public static String list2Strs(List<String> list) {
		if (list == null || list.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String str : list) {
			sb.append(str).append(",");
		}
		String orders = sb.toString();
		orders = orders.substring(0, orders.length() - 1);
		return orders;
	}

	/**
	 * 
	 * @author i-zqluo 解析从网上获取的天气信息的工具类
	 * 
	 */
	public static final class WeaterInfoParser {

		/**
		 * 通过解析content，得到一个一维为城市编号，二维为城市名的二维数组 解析的字符串的形式为:
		 * <code>编号|城市名,编号|城市名,.....</code>
		 * 
		 * @param content
		 *            需要解析的字符串
		 * @return 封装有城市编码与名称的二维数组
		 */
		public static String[][] parseCity(String content) {
			// 判断content不为空
			if (content != null && content.trim().length() != 0) {
				StringTokenizer st = new StringTokenizer(content, ",");
				int count = st.countTokens();
				String[][] citys = new String[count][2];
				int i = 0, index = 0;
				while (st.hasMoreTokens()) {
					String city = st.nextToken();
					index = city.indexOf('|');
					citys[i][0] = city.substring(0, index);
					citys[i][1] = city.substring(index + 1);
					i = i + 1;
				}
				return citys;
			}
			return null;
		}
	}

	/**
	 * 根据给定的url地址访问网络，得到响应内容(这里为GET方式访问)
	 * 
	 * @param url
	 *            指定的url地址
	 * @return web服务器响应的内容
	 */
	public static String getWebContent(String urlStr) {
		URL url = null;
		StringBuilder sb = new StringBuilder();
		try {
			try {
				url = new URL(urlStr);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			BufferedReader r = null;
			try {
				URLConnection con = url.openConnection();
				r = new BufferedReader(new InputStreamReader(
						con.getInputStream(), "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			String str = "";
			do {
				try {
					str = r.readLine();
					sb.append(str);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} while (str != null);
			try {
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
		return sb.toString();
	}

	public static void start_activity(Activity activity, Class<?> cls,
			BasicNameValuePair... name) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		for (int i = 0; i < name.length; i++) {
			intent.putExtra(name[i].getName(), name[i].getValue());
		}
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.push_left_in,
				R.anim.push_left_out);
	}

	public static void start_activityForResult(Activity activity, Class<?> cls,
			int requestCode, BasicNameValuePair... name) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		for (int i = 0; i < name.length; i++) {
			intent.putExtra(name[i].getName(), name[i].getValue());
		}
		activity.startActivityForResult(intent, requestCode);
		activity.overridePendingTransition(R.anim.push_left_in,
				R.anim.push_left_out);
	}

	// ////////////////////////////////////////////////
	private static final int MAX_NONCE = 0 + 10;

	private static final String LABEL_App_sign = "api_sign";
	private static final String LABEL_TIME = "timestamp";
	private static final String LABEL_NONCE = "nonce";
	private static final String LABEL_UID = "uid";

	private static final SecureRandom sRandom = new SecureRandom();

	private static char sHexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String getNonce() {
		byte[] bytes = new byte[MAX_NONCE / 2];
		sRandom.nextBytes(bytes);
		return hexString(bytes);
	}

	public static String hexString(byte[] source) {
		if (source == null || source.length <= 0) {
			return "";
		}

		final int size = source.length;
		final char str[] = new char[size * 2];
		int index = 0;
		byte b;
		for (int i = 0; i < size; i++) {
			b = source[i];
			str[index++] = sHexDigits[b >>> 4 & 0xf];
			str[index++] = sHexDigits[b & 0xf];
		}
		return new String(str);
	}

	private static long getTimestamp() {
		Date date = new Date();
		long i = date.getTime();
		return i;
	}

	private static String getAPIsig(String key, long timestamp, String nonce,
			String uid) {
		// api_sig =
		// MD5("api_key"+@api_key+"nonce"+@nonce+"timestamp"+@timestamp)
		String result = null;
		StringBuilder builder = new StringBuilder();
		synchronized (builder) {
			builder.append(key);
			builder.append(timestamp);
			builder.append(nonce);
			builder.append(uid);
			result = UtilsJava.encode(builder.toString());
			builder.delete(0, builder.length());
		}
		return result;
	}

	/**
	 * &…………………………
	 * 
	 * @param key
	 * @return
	 */
	public static String getParams(String key) {
		String result = "";
		try {
			String[] temp = key.split(":");
			long timestamp = getTimestamp();
			String nonce = getNonce();
			String api_sign = getAPIsig(key, timestamp, nonce, temp[1]);

			StringBuilder builder = new StringBuilder();

			synchronized (result) {
				builder.append(String.format("&" + LABEL_UID + "=%s", temp[1]));
				builder.append(String.format("&" + LABEL_NONCE + "=%s", nonce));
				builder.append(String.format("&" + LABEL_TIME + "=%s",
						timestamp));
				builder.append(String.format("&" + LABEL_App_sign + "=%s",
						api_sign));
				result = builder.toString();
				builder.delete(0, builder.length());
			}
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	public static String getScreenParams(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return "&screen="
				+ (dm.heightPixels > dm.widthPixels ? dm.widthPixels + "*"
						+ dm.heightPixels : dm.heightPixels + "*"
						+ dm.widthPixels);
	}
	// ////////////////////////////////////////////////////////////////////////////////////////////

}
