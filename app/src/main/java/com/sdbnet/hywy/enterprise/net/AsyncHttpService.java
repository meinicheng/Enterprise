package com.sdbnet.hywy.enterprise.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.model.ReportModel;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.ToastUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;
import com.sdbnet.hywy.enterprise.utils.UtilsCommon;
import com.sdbnet.hywy.enterprise.utils.UtilsJava;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class AsyncHttpService {
	private static final String TAG = "AsyncHttpService";
	private static final String BASE_URL = "http://182.18.31.50/HYWY";// 正式
	// private static final String BASE_URL = "http://182.18.31.50/HYWY2";// 2
	// private static final String BASE_URL = "http://192.168.1.102:8080/HYWY";
	// public static final String BASE_URL = "http://192.168.1.115:8080/HYWY";
	// 本地TEST
	// public static final String BASE_URL = "http://192.168.1.111:8080/HYWY";//
	// 本地TEST

	private static final AsyncHttpClient client = new AsyncHttpClient();

	static {
		// 设置请求超时
		client.setTimeout(20000);
	}

	/**
	 * 提交GET请求
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 * @param context
	 */
	private static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler, Context context) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	/**
	 * 提交POST请求
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 * @param context
	 */
	private static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler, Context context) {
		if (!UtilsAndroid.Set.checkNetState(context)) {

			Toast.makeText(context,
					context.getResources().getString(R.string.httpError),
					Toast.LENGTH_LONG).show();
			return;
		}

		if ("/userLogin".equals(url)) {
			params.add(Constants.Feild.KEY_USER_TYPE, PreferencesUtil.user_type);
		} else if ("/choosePro".equals(url)) {
			PreferencesUtil.putValue(PreferencesUtil.KEY_SESSION_ID,
					UtilsJava.generateString(15));
			PreferencesUtil.session_id = PreferencesUtil.getValue(
					PreferencesUtil.KEY_SESSION_ID, null);
			params.add(Constants.Feild.KEY_SESSION_ID,
					PreferencesUtil.session_id);
		} else {
			if (TextUtils.isEmpty(PreferencesUtil.user_company)
					|| TextUtils.isEmpty(PreferencesUtil.item_id)
					|| TextUtils.isEmpty(PreferencesUtil.user_id)
					|| TextUtils.isEmpty(PreferencesUtil.user_tel)) {
				return;
			}
			params.add(Constants.Feild.KEY_COMPANY_ID,
					PreferencesUtil.user_company);
			params.add(Constants.Feild.KEY_ITEM_ID, PreferencesUtil.item_id);
			params.add(Constants.Feild.KEY_STAFF_ID, PreferencesUtil.user_id);
			params.add(Constants.Feild.KEY_USER_TYPE, PreferencesUtil.user_type);
			if (TextUtils.isEmpty(PreferencesUtil.session_id)) {
				return;
			} else {
				params.add(Constants.Feild.KEY_SESSION_ID,
						PreferencesUtil.session_id);
			}
		}
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	/**
	 * 获取访问网址
	 * 
	 * @param relativeUrl
	 * @return
	 */
	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}

	/**
	 * 取消请求
	 * 
	 * @param context
	 */
	public static void cancelRequests(Context context) {
		client.cancelRequests(context, true);
	}

	/**
	 * 初始化定位
	 * 
	 * @param hciParentCompany
	 * @return
	 * @throws NumberFormatException
	 * @throws JSONException
	 */
	public static JSONObject initLocate(String hciParentCompany)
			throws NumberFormatException, JSONException {
		// 准备数据
		NameValuePair param1 = new BasicNameValuePair("hciParentCompany",
				hciParentCompany);
		// 使用工具类直接发出POST请求,服务器返回json数据，比如"{userid:12}"
		String response = CustomerHttpClient.post("/initLocate", param1);
		JSONObject root = new JSONObject(response);
		return root;
	}

	/**
	 * 上传设备号
	 * 
	 * @param pid
	 *            用户ID
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param tel
	 *            手机号
	 * @param registrationid
	 *            推送RegistrationID
	 * @param userType
	 *            用户类型
	 * @param responseHandler
	 * @param context
	 */
	public static void upDeviceInfo(String tel, String registrationid,
			String imei, AsyncHttpResponseHandler responseHandler,
			Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_STAFF_ID, pid);
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		// params.add("userType", userType);
		params.add(Constants.Feild.KEY_STAFF_TEL, tel);
		params.add("registrationid", registrationid);
		params.add("imei", imei);
		params.add("deviceType", "3");
		params.add("token", PreferencesUtil.user_token);
		post("/upDeviceInfo", params, responseHandler, context);
	}

	/**
	 * 获取工作组
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param islocation
	 *            工作组是否有定位权限
	 * @param responseHandler
	 * @param context
	 */
	public static void getErpGroups(String islocation,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add("islocation", islocation);
		post("/getErpGroups", params, responseHandler, context);
	}

	/**
	 * 切换工作组
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param grpid
	 *            工作组ID
	 * @param responseHandler
	 * @param context
	 */
	public static void switchGroup(String grpid,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add(Constants.Feild.KEY_GROUP_ID, grpid);
		post("/switchGroup", params, responseHandler, context);
	}

	/**
	 * 订单追踪
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param sysnox
	 *            原始平台订单号
	 * @param responseHandler
	 * @param context
	 */
	public static void traceOrder(String sysnox,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add(Constants.Feild.KEY_PLATFORM_ORIGINAL_NO, sysnox);
		post("/traceOrder", params, responseHandler, context);
	}

	/**
	 * 精确定位
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param pid
	 *            用户ID
	 * @param responseHandler
	 * @param context
	 */
	public static void accuratePosi(String locpid,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		// params.add(Constants.Feild.KEY_STAFF_ID, pid);
		params.add("locpid", locpid);
		post("/accuratePosi", params, responseHandler, context);
	}

	/**
	 * 获取历史轨迹
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param sysnox
	 *            原始平台订单号
	 * @param lineno
	 *            轨迹编号
	 * @param responseHandler
	 * @param context
	 */
	public static void getHistoryLocus(String sysnox, String lineno,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add(Constants.Feild.KEY_PLATFORM_ORIGINAL_NO, sysnox);
		params.add(Constants.Feild.KEY_LINE_ID, lineno);
		post("/historyLocus", params, responseHandler, context);
	}

	/**
	 * 获取历史轨迹
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param truckno
	 *            车牌号
	 * @param datefrom
	 *            日期从
	 * @param dateto
	 *            日期到
	 * @param responseHandler
	 * @param context
	 */
	public static void getContractMotorLocus(String truckno, String datefrom,
			String dateto, AsyncHttpResponseHandler responseHandler,
			Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add(Constants.Feild.KEY_STAFF_TRUCK_NO, truckno);
		params.add(Constants.Params.PARAM_DATE_FROM, datefrom);
		params.add(Constants.Params.PARAM_DATE_TO, dateto);
		post("/contractMotorLocus", params, responseHandler, context);
	}

	/**
	 * 登录
	 * 
	 * @param tel
	 *            手机号
	 * @param pwd
	 *            密码
	 * @param loginType
	 *            登录类型
	 * @param responseHandler
	 * @param context
	 */
	public static void login(String tel, String pwd,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		params.add(Constants.Feild.KEY_STAFF_TEL, tel);
		params.add(Constants.Feild.KEY_STAFF_PWD, pwd);
		post("/userLogin", params, responseHandler, context);
	}

	/**
	 * 获得项目的相关操作权限
	 * 
	 * @param cmpid
	 *            公司编码
	 * @param itemid
	 *            项目特征码
	 * @param pid
	 *            用户ID
	 * @param responseHandler
	 * @param context
	 */
	public static void getPermissionWithProject(String cmpid, String itemid,
			String pid, AsyncHttpResponseHandler responseHandler,
			Context context) {
		RequestParams params = new RequestParams();
		params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add(Constants.Feild.KEY_STAFF_ID, pid);
		params.add("userType", PreferencesUtil.user_type);
		post("/choosePro", params, responseHandler, context);
	}

	/**
	 * 获取历史订单
	 * 
	 * @param cmpid
	 *            公司编码
	 * @param itemid
	 *            项目特征码
	 * @param pageNo
	 *            当前页
	 * @param responseHandler
	 * @param context
	 */
	public static void getHistoryOrders(int pageNo,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add("pageNo", String.valueOf(pageNo));
		post("/historyOrders", params, responseHandler, context);
	}

	/**
	 * 查询历史订单
	 * 
	 * @param cmpid
	 *            公司编码
	 * @param itemid
	 *            项目特征码
	 * @param pageNo
	 *            当前页
	 * @param ordno
	 *            企业订单号
	 * @param createFrom
	 *            订单时间从
	 * @param createTo
	 *            订单时间到
	 * @param oStatus
	 *            订单状态
	 * @param responseHandler
	 * @param context
	 */
	public static void queryHistoryOrders(int pageNo, String ordno,
			String createFrom, String createTo, String oStatus,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add("pageNo", String.valueOf(pageNo));
		params.add(Constants.Feild.KEY_ORDER_NO, ordno);
		params.add("createFrom", createFrom);
		params.add("createTo", createTo);
		params.add("status", oStatus);
		post("/historyOrders", params, responseHandler, context);
	}

	/**
	 * 获取订单统计信息
	 * 
	 * @param cmpid
	 * @param itemid
	 * @param pageNo
	 * @param responseHandler
	 * @param context
	 */
	public static void getStatisticalInfos(int pageNo,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add(Constants.Params.PARAM_PAGE_NO, String.valueOf(pageNo));
		post("/orderStatistical", params, responseHandler, context);
	}

	/**
	 * 查询订单
	 * 
	 * @param cmpid
	 * @param itemid
	 * @param pageNo
	 * @param ordno
	 * @param createFrom
	 * @param createTo
	 * @param ostatus
	 * @param workflow
	 * @param responseHandler
	 * @param context
	 */
	public static void getOrdersByQuery(int pageNo, String ordno,
			String createFrom, String createTo, String ostatus,
			String workflow, AsyncHttpResponseHandler responseHandler,
			Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add(Constants.Feild.KEY_ORDER_NO, ordno);
		params.add("createFrom", createFrom);
		params.add("createTo", createTo);
		params.add("ostatus", ostatus);
		params.add(Constants.Feild.KEY_WORKFLOW, workflow);
		params.add("pageNo", String.valueOf(pageNo));
		post("/accurateOrders", params, responseHandler, context);
	}

	/**
	 * 根据统计获取相应订单
	 * 
	 * @param cmpid
	 * @param itemid
	 * @param pageNo
	 * @param workflow
	 * @param ostatus
	 * @param ordtime
	 * @param responseHandler
	 * @param context
	 */
	public static void getOrdersWithStatistical(int pageNo, String workflow,
			String ostatus, String ordtime,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add(Constants.Feild.KEY_WORKFLOW, workflow);
		params.add(Constants.Feild.KEY_ORDER_CREATE_TIME, ordtime);
		params.add("ostatus", ostatus);
		params.add("pageNo", String.valueOf(pageNo));
		post("/detailStatistical", params, responseHandler, context);
	}

	/**
	 * 附件浏览
	 * 
	 * @param traceid
	 *            追溯流水号
	 * @param responseHandler
	 * @param context
	 */
	public static void getTraceAttachs(String traceid,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		params.add(Constants.Feild.KEY_TRACE_ID, traceid);
		Log.i(TAG, "getTraceAttachs traceid=" + traceid + "");
		post("/traceAttachs", params, responseHandler, context);
	}

	/**
	 * 获取企业资料
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param responseHandler
	 * @param context
	 */
	public static void getEnterpriseInfo(
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		post("/enterpriseInfo", params, responseHandler, context);
	}

	/**
	 * 获取企业合约车辆用户列表
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param responseHandler
	 * @param context
	 */
	public static void getContractUsers(
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		post("/getContractUsers", params, responseHandler, context);
	}

	/**
	 * 修改用户资料
	 * 
	 * @param cmpid
	 * @param itemid
	 * @param pid
	 * @param pname
	 * @param sex
	 * @param loctel
	 * @param truckno
	 * @param trucktype
	 * @param trucklength
	 * @param truckweigth
	 * @param imgList
	 * @param responseHandler
	 * @param context
	 */
	public static void modifyContractUser(String conpid, String pname,
			String sex, String loctel, String truckno, String trucktype,
			String trucklength, String truckweigth,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_STAFF_ID, pid);
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add("conpid", conpid);
		params.add(Constants.Feild.KEY_STAFF_NAME, pname);
		params.add(Constants.Feild.KEY_STAFF_SEX, sex);
		params.add(Constants.Feild.KEY_LOCA_TEL, loctel);
		params.add(Constants.Feild.KEY_STAFF_TRUCK_NO, truckno);
		params.add(Constants.Feild.KEY_STAFF_TRUCK_TYPE, trucktype);
		params.add(Constants.Feild.KEY_STAFF_TRUCK_LENGTH, trucklength);
		params.add(Constants.Feild.KEY_STAFF_TRUCK_WEIGHT, truckweigth);

		post("/modifyContractUser", params, responseHandler, context);
	}

	/**
	 * 返回用户信息
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param pid
	 *            用户ID
	 * @param responseHandler
	 * @param context
	 */
	public static void getContractUserInfo(String conpid,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_STAFF_ID, pid);
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add("conpid", conpid);
		post("/contractUserInfo", params, responseHandler, context);
	}

	/**
	 * 获取企业指定工作组下的用户
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param responseHandler
	 * @param context
	 */
	public static void getGroupUsers(String grpid, String isContract,
			String ustatus, AsyncHttpResponseHandler responseHandler,
			Context context) {
		RequestParams params = new RequestParams();
		params.add(Constants.Feild.KEY_GROUP_ID, grpid);
		params.add(Constants.Feild.KEY_IS_CONTRACT, isContract);
		params.add(Constants.Feild.KEY_USER_STATUS, ustatus);
		post("/getGroupUsers", params, responseHandler, context);
	}

	/**
	 * 修改当前用户帐号密码
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param pid
	 *            用户ID
	 * @param pwd
	 *            密码
	 * @param responseHandler
	 * @param context
	 */
	public static void modifyUserPwd(String pwd,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		// params.add(Constants.Feild.KEY_STAFF_ID, pid);
		params.add(Constants.Feild.KEY_STAFF_PWD, pwd);
		post("/modifyUserPwd", params, responseHandler, context);
	}

	/**
	 * 
	 * @param orders
	 *            订单号
	 * @param responseHandler
	 * @param context
	 */
	public static void getReportExecption(String orders,
			AsyncHttpResponseHandler responseHandler, Context context) {
		// 请求地址 /orderAccides
		// 请求方法 POST
		// 发送方式 HTTP

		// 返回数据形式 JSON
		// 文字编码 UTF-8

		// 参数名 参数概要 必須 值
		// cmpid 企业ID ○ (必填)
		// itemid 项目特征码 ○ (必填)
		// pid 当前用户ID ○
		// userType 用户类型 ○ 0：企业用户 1：个人用户
		// sessionid 用户ID ○ 本地生成，传给服务器；成功后保存本地，后续请求时携带，如果退出，则清除
		// ordno 企业订单号
		PreferencesUtil.initStoreData();
		RequestParams params = new RequestParams();
		if (TextUtils.isEmpty(orders)) {
			ToastUtil.show(context, "orders==null");
			return;
		}
		params.add("ordno", orders);
		post("/orderAccides", params, responseHandler, context);

	}

	/**
	 * 
	 * @param reportModel
	 * @param responseHandler
	 * @param context
	 */
	public static void uploadReportExection(ReportModel reportModel,
			JsonHttpResponseHandler responseHandler, Context context) {
		// ordno 企业订单号 ○ 当前企业的订单号,","隔开
		// accdesc 异常描述 ○ (必填)
		// pname 用户名称 ○ (必填)
		// loctel 用户电话 ○ (必填)
		// acctime 异常发生时间 ○
		// locaddr 定位地址
		// image1…image5 图片
		Log.i("uploadReportExection", reportModel.toString());
		PreferencesUtil.initStoreData();
		RequestParams params = new RequestParams();
		if (TextUtils.isEmpty(reportModel.orders)) {
			ToastUtil.show(context, "orders == null");
			return;
		}
		params.add("ordno", reportModel.orders);

		if (TextUtils.isEmpty(reportModel.explain)) {
			ToastUtil.show(context, "explain == null");
			return;
		}
		params.add("accdesc", reportModel.explain);

		String name = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_NAME);
		if (TextUtils.isEmpty(name)) {
			ToastUtil.show(context, "user name == null");
			return;
		}
		params.add("pname", name);

		String tel = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL);
		if (TextUtils.isEmpty(tel)) {
			ToastUtil.show(context, "Tellphone == null");
			return;
		}
		params.add("loctel", tel);

		// Unnecessary
		params.add("acctime", reportModel.date);
		// Unnecessary
		params.add("locaddr", reportModel.place);

		File f;
		for (int i = 0; i < reportModel.imgList.size(); i++) {
			f = new File(reportModel.imgList.get(i).imagePath);
			if (f.exists() && f.length() > 0) {
				try {
					params.put("image" + (i + 1), f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		post("/accideReport", params, responseHandler, context);
	}

	/**
	 * 
	 * @param reportModel
	 * @param responseHandler
	 * @param context
	 */
	public static void getUserState(String userId,
			JsonHttpResponseHandler responseHandler, Context context) {
		// conpid 查询用户ID ○

		RequestParams params = new RequestParams();
		if (TextUtils.isEmpty(userId) || !UtilsCommon.checkAccount()) {
			return;
		}
		params.add("conpid", userId);
		post("/userStatus", params, responseHandler, context);
	}

}
