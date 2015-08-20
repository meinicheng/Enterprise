package com.sdbnet.hywy.enterprise.utils;

import android.os.Environment;

import com.sdbnet.hywy.enterprise.R;

import java.util.HashMap;
import java.util.Map;

public class Constants {

	public static final Map<String, Integer> MENU_MAP = new HashMap<String, Integer>();

	// <string name="menu_location">首页</string>
	// <string name="menu_info">公司资料</string>
	// <string name="menu_order_list">单量统计</string>
	// <string name="menu_order_trace">订单追踪</string>
	// <string name="menu_order_query">综合查询</string>
	// <string name="menu_motor_locus">轨迹查询</string>
	// <string name="menu_contract_users">合约车辆</string>
	// <string name="menuOrder">我的派单</string>
	// <string name="menuManage">用户管理</string>
	// <string name="menuSetting">系统设置</string>
	// <string name="menuSwitch">切换公司</string>
	// <string name="menuAddcompany">创建公司</string>
	static {
		MENU_MAP.put("CMP001", R.string.menu_location);// "首页");
		MENU_MAP.put("CMP002", R.string.menu_info);// "公司资料");
		MENU_MAP.put("CMP003", R.string.menu_order_list);// "单量统计");
		MENU_MAP.put("CMP004", R.string.menu_order_trace);// "订单追踪");
		MENU_MAP.put("CMP005", R.string.menu_order_query);// "综合查询");
		MENU_MAP.put("CMP006", R.string.menu_motor_locus);// "轨迹查询");
		MENU_MAP.put("CMP007", R.string.menu_contract_users);// "合约车辆");
		// MENU_MAP.put("CMP001", "首页");
		// MENU_MAP.put("CMP002", "公司资料");
		// MENU_MAP.put("CMP003", "单量统计");
		// MENU_MAP.put("CMP004", "订单追踪");
		// MENU_MAP.put("CMP005", "综合查询");
		// MENU_MAP.put("CMP006", "轨迹查询");
		// MENU_MAP.put("CMP007", "合约车辆");
	}

	public static final class Value {
		public static final String LOGIN_EMPLOYEE = "0";
		public static final String LOGIN_PU = "1";
		public static final String YES = "1";
		public static final String NO = "0";
		public static final String TEXT_CALL = "拨打电话";
		public static final String TEXT_POSITION = "精确定位";
		public static final String TEXT_LOCAL = "历史轨迹";
		public static final String TEXT_BROSWER = "附件浏览";
		public static final int ORDER_STATU_SIGNIN = 1;
		public static final int ORDER_STATU_UNSIGNIN = 2;
		public static final int ORDER_STATU_ABSENT = 3;
		public static final int IMAGE_COUNT = 10;
		public static final int WORKING=1;
		public static final int WORKED=0;
	}

	public static final class Params {
		public static final String METHOD = "method";
		public static final String METHOD_ACCURATE_ORDERS = "accurateOrders";
		public static final String METHOD_DETAIL_STATISTICAL = "detailStatistical";
		public static final String PARAM_CREATE_FROM = "createFrom";
		public static final String PARAM_CREATE_TO = "createTo";
		public static final String PARAM_DATE_FROM = "datefrom";
		public static final String PARAM_DATE_TO = "dateto";
		public static final String PARAM_WORKFLOW = "workflow";
		public static final String PARAM_OSTATUS = "ostatus";
		public static final String PARAM_PAGE_NO = "pageNo";
		public static final String PARAM_REDICT = "redict";

	}

	public static final class RequestCode {
		public static final int QUERY_ORDER = 1;
		public static final int CHOOSE_GROUP = 2;
		public static final int QUERY_CHOOSE_MODEL = 5;
		public static final int QUERY_CHOOSE_LENGTH = 6;
		public static final int QUERY_CHOOSE_LOAD = 7;
	}

	public static final class ResultCode {
		public static final int QUERY_ORDER = 1;
		public static final int CHOOSE_GROUP = 2;
		public static final int QUERY_ORDER_LIST = 3;
		public static final int QUERY_ORDER_POST = 4;
		public static final int QUERY_CHOOSE_MODEL = 5;
		public static final int QUERY_CHOOSE_LENGTH = 6;
		public static final int QUERY_CHOOSE_LOAD = 7;
	}

	public static final class Feild {
		public static final String KEY_ERROR_CODE = "errcode";
		public static final String KEY_MSG = "msg";
		public static final String KEY_TOKEN = "token";
		public static final String KEY_ITEMS = "cmpItems";
		public static final String KEY_COMPANY_ID = "cmpid";
		public static final String KEY_COMPANY_NAME = "cmpname";
		public static final String KEY_COMPANY_TEL_1 = "telephone1";
		public static final String KEY_COMPANY_TEL_2 = "telephone2";
		public static final String KEY_COMPANY_ADDRESS = "cmpaddress";
		public static final String KEY_COMPANY_LOGO = "logo";
		public static final String KEY_COMPANY_REMARK = "remark";
		public static final String KEY_COMPANY_EMAIL = "email";
		public static final String KEY_COMPANY_URL = "url";
		public static final String KEY_COMPANY_LINKMAN = "linkman";
		public static final String KEY_ACTION_BTN = "btnname";
		public static final String KEY_WORKFLOW = "workflow";
		public static final String KEY_WORKFLOW_NAME = "wfname";
		public static final String KEY_START_NODE = "startnode";
		public static final String KEY_ITEM_ID = "itemid";
		public static final String KEY_ITEM_NAME = "itemname";
		public static final String KEY_STAFF = "staff";
		public static final String KEY_STAFF_ID = "pid";
		public static final String KEY_STAFF_PWD = "pwd";
		public static final String KEY_STAFF_TEL = "tel";
		public static final String KEY_STAFF_STATUS = "status";
		public static final String KEY_STAFF_CAN_CALL = "call";
		public static final String KEY_STAFF_CAN_LOCAT = "locat";
//		public static final String KEY_STAFF_CAN_BROSWER = "broswer";
		public static final String KEY_STAFF_NAME = "pname";
		public static final String KEY_LOCA_TEL = "loctel";
		public static final String KEY_LOCA_TIME = "loctime";
		public static final String KEY_LOCA_ADDRESS = "locaddress";
		public static final String KEY_LOCA_LONGITUDE = "longitude";
		public static final String KEY_LOCA_LATITUDE = "latitude";
		public static final String KEY_LOCA_COORDINATES = "coordinates";
		public static final String KEY_STAFF_SEX = "sex";
		public static final String KEY_STAFF_TRUCK_NO = "truckno";
		public static final String KEY_STAFF_TRUCK_TYPE = "trucktype";
		public static final String KEY_STAFF_TRUCK_LENGTH = "trucklength";
		public static final String KEY_STAFF_TRUCK_WEIGHT = "truckweight";
		public static final String KEY_CITY = "city";
		public static final String KEY_IS_ADMIN = "isadm";
		public static final String KEY_MENU = "menu";
		public static final String KEY_EXECUTE_ACTION = "scan";
		public static final String KEY_COMPANY_ORDTITLE = "ordtitle";
		public static final String KEY_COMPANY_CNOTITLE = "cnotitle";
		public static final String KEY_ACTION_ID = "actidx";
		public static final String KEY_ACTION = "action";
		public static final String KEY_ACTION_NAME = "actname";
		public static final String KEY_ACTION_COUNT = "count";
		public static final String KEY_ACTION_CONTENT = "actmemo";
		public static final String KEY_ACTION_TIME = "acttime";
		public static final String KEY_LINE_ID = "lineno";
		public static final String KEY_LINE_NAME = "linename";
		public static final String KEY_SIGN = "sign";
		public static final String KEY_THIRD_PARTY = "acttype";
		public static final String KEY_WORK_GROUPS = "workgroups";
		public static final String KEY_WORK_GROUP = "workgroup";
		public static final String KEY_GROUP_ID = "grpid";
		public static final String KEY_GROUP_NAME = "grpname";
		public static final String KEY_GROUP_LOCATION = "islocation";
		public static final String KEY_GROUP_IS_LOCATION = "islocation";
		public static final String KEY_STAFFS = "staffs";
		public static final String KEY_ORDERS = "orders";
		public static final String KEY_ORDER = "order";
		public static final String KEY_ORDER_NO = "ordno";
		public static final String KEY_PLATFORM_NO = "sysno";
		public static final String KEY_PLATFORM_ORIGINAL_NO = "sysnox";
		public static final String KEY_ORDER_CREATE_TIME = "ordtime";
		public static final String KEY_TRACES = "traces";
		public static final String KEY_TRACE_ID = "traceid";
		public static final String KEY_ENTERPRISE = "enterprise";
		public static final String KEY_PICS = "pics";
		public static final String KEY_PIC_ID = "picid";
		public static final String KEY_PIC_SMALL = "smallimg";
		public static final String KEY_PIC_BIG = "bigimg";
		public static final String KEY_ROLE_NAME = "rname";
		public static final String KEY_ACTIONS = "actions";
		public static final String KEY_STATISTICAL = "statistical";
		public static final String KEY_STATISTICAL_COUNT = "count";
		public static final String KEY_CODE_LENGTH = "codelength";
		public static final String KEY_ACTMEMOINNER = "actmemoinner";
		public static final String KEY_USER_TYPE = "userType";
		public static final String KEY_SESSION_ID = "sessionid";
		public static final String KEY_IS_CONTRACT = "isContract";
		public static final String KEY_USER_STATUS = "ustatus";
		// zhangyu
		public static final String KEY_IS_LOCAATE = "islocate";
		public static final String KEY_IMG_COUNT = "imgcount";
		public static final String KEY_IS_CALL = "iscall";

		public static final String KEY_IS_SCAN = "isscan";
	
		// options array feild
		public static final String KEY_OPTIONS = "options";
		public static final String KEY_SCAN_MODEL = "scanModel";

	}

	/**
	 * 操作步骤的动作码
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class Step {
		public static final String CHWK = "CHWK"; // 切换上下班
		public static final String NEXT = "NEXT"; // 扫描完成点下一步
		public static final String POST = "POST"; // 订单执行提交
		public static final String LOCA = "LOCA"; // 定位
		public static final String EDIT = "EDIT"; // 编辑用户资料
		public static final String SAVE = "SAVE"; // 保存编辑后的用户资料
		public static final String DRAFT = "DRAFT"; // 草稿箱操作
	}

	public static final class SDBNET {
		public static final String BASE_PATH = Environment
				.getExternalStorageDirectory().getPath() + "/sdbnet/";
		public static final String PATH_PHOTO = BASE_PATH + "PhotoCache/";
		public static final String SDPATH_FORMATS = BASE_PATH + ".formats/";
		public static final String SDPATH_CRASH=BASE_PATH+".carsh/";
	}
}
