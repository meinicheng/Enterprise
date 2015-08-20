package com.sdbnet.hywy.enterprise.utils;

import com.sdbnet.hywy.enterprise.model.Enterprise;
import com.sdbnet.hywy.enterprise.model.ExecuteAction;
import com.sdbnet.hywy.enterprise.model.Staff;

import org.json.JSONException;
import org.json.JSONObject;

public class UtilsModel {
	/**
	 * 解析Staff信息
	 * 
	 * @param jsonObj
	 * @return
	 */
	public static Staff jsonToStaff(JSONObject jsonObj) {
		Staff staff = new Staff();
		try {
			staff.setPid(jsonObj.getString(Constants.Feild.KEY_STAFF_ID));
			staff.setPname(jsonObj.getString(Constants.Feild.KEY_STAFF_NAME));
			staff.setLatitude(jsonObj
					.getDouble(Constants.Feild.KEY_LOCA_LATITUDE));
			staff.setLocaddress(jsonObj
					.getString(Constants.Feild.KEY_LOCA_ADDRESS));
			staff.setLoctel(jsonObj.getString(Constants.Feild.KEY_LOCA_TEL));
			staff.setLoctime(jsonObj.getString(Constants.Feild.KEY_LOCA_TIME));
			staff.setLongitude(jsonObj
					.getDouble(Constants.Feild.KEY_LOCA_LONGITUDE));
			staff.setTrucklength(jsonObj
					.getDouble(Constants.Feild.KEY_STAFF_TRUCK_LENGTH));
			staff.setTruckno(jsonObj
					.getString(Constants.Feild.KEY_STAFF_TRUCK_NO));
			staff.setTrucktype(jsonObj
					.getString(Constants.Feild.KEY_STAFF_TRUCK_TYPE));
			staff.setTruckweight(jsonObj
					.getDouble(Constants.Feild.KEY_STAFF_TRUCK_WEIGHT));
			staff.setStatus(jsonObj.getString(Constants.Feild.KEY_STAFF_STATUS));
			return staff;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Staff jsonToStaff(String jsonStr) {
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(jsonStr);
			return jsonToStaff(jsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解析Enterprise信息
	 * 
	 * @param jsonObj
	 * @return
	 */
	public static Enterprise jsonToEnterprise(JSONObject jsonObj) {
		Enterprise company = new Enterprise();
		try {
			company.setCmpaddress(jsonObj
					.getString(Constants.Feild.KEY_COMPANY_ADDRESS));
			company.setCmpname(jsonObj
					.getString(Constants.Feild.KEY_COMPANY_NAME));
			company.setEmail(jsonObj
					.getString(Constants.Feild.KEY_COMPANY_EMAIL));
			company.setItemname(jsonObj
					.getString(Constants.Feild.KEY_ITEM_NAME));
			company.setLinkman(jsonObj
					.getString(Constants.Feild.KEY_COMPANY_LINKMAN));
			company.setLogo(jsonObj.getString(Constants.Feild.KEY_COMPANY_LOGO));
			company.setRemark(jsonObj
					.getString(Constants.Feild.KEY_COMPANY_REMARK));
			company.setTelephone1(jsonObj
					.getString(Constants.Feild.KEY_COMPANY_TEL_1));
			company.setTelephone2(jsonObj
					.getString(Constants.Feild.KEY_COMPANY_TEL_2));
			company.setUrl(jsonObj.getString(Constants.Feild.KEY_COMPANY_URL));
			return company;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Enterprise jsonToEnterprise(String jsonStr) {
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(jsonStr);
			return jsonToEnterprise(jsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ExecuteAction jsonToExecuteAction(JSONObject jsonObj) {
		ExecuteAction action = new ExecuteAction();

		try {
			action.setActidx(jsonObj.getString(Constants.Feild.KEY_ACTION_ID));
			action.setAction(jsonObj.getString(Constants.Feild.KEY_ACTION));
			action.setActmemo(jsonObj
					.getString(Constants.Feild.KEY_ACTION_CONTENT));
			action.setActname(jsonObj
					.getString(Constants.Feild.KEY_ACTION_NAME));
			action.setLinename(jsonObj.getString(Constants.Feild.KEY_LINE_NAME));
			action.setLineno(jsonObj.getString(Constants.Feild.KEY_LINE_ID));
			action.setSign(jsonObj.getString(Constants.Feild.KEY_SIGN));
			action.setActype(jsonObj.getString(Constants.Feild.KEY_THIRD_PARTY));

			action.setBtnname(jsonObj.getString(Constants.Feild.KEY_ACTION_BTN));
			action.setWorkflow(jsonObj.getString(Constants.Feild.KEY_WORKFLOW));
			action.setStartnode(jsonObj
					.getString(Constants.Feild.KEY_START_NODE));
			return action;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ExecuteAction jsonToExecuteAction(String jsonStr) {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			return jsonToExecuteAction(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
