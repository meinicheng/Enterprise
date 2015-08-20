package com.sdbnet.hywy.enterprise.ui.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.model.Enterprise;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.base.BaseFrament;
import com.sdbnet.hywy.enterprise.ui.widget.DialogLoading;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsError;
import com.sdbnet.hywy.enterprise.utils.UtilsModel;

import org.apache.http.Header;
import org.json.JSONObject;

public class InfoFragment extends BaseFrament {

	private TextView mTextName;
	private TextView mTextCContactor;
	private TextView mTextTel;

	private Enterprise company = null;

	private Dialog mLoadDailog;
	private TextView mTextAddress;
	private TextView mTextDescribe;
	private TextView mTextUrl;
	private TextView mTextEmai;
	private TextView mTextPorjectName;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_show_info, null);
		initControls();
		loadDatas();
		return view;
	}

	/**
	 * 加载公司资料
	 */
	private void loadDatas() {
		AsyncHttpService.getEnterpriseInfo(new JsonHttpResponseHandler() {

			@Override
			public void onStart() {
				mLoadDailog.show();
				super.onStart();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				mLoadDailog.dismiss();
				showMsgLong(R.string.httpisNull);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				System.out.println("info: " + response.toString());
				super.onSuccess(statusCode, headers, response);
				try {
					if (!UtilsError.isErrorCode(getActivity(), response)) {
						// 解析公司资料信息
						JSONObject jsonObj = response
								.getJSONObject(Constants.Feild.KEY_ENTERPRISE);
						company = UtilsModel.jsonToEnterprise(jsonObj);
						fillDatas();
						refreshLocalData();
					}
				} catch (Exception e) {
					e.printStackTrace();
					showMsgLong(R.string.network_exception_please_try_again_later);
				} finally {
					mLoadDailog.dismiss();
				}
			}
		}, getActivity());
	}

	/**
	 * 将数据填充界面
	 */
	private void fillDatas() {
		mTextAddress.setText(company.getCmpaddress());
//		mTextDescribe.setText("                          "+company.getRemark());
//		mTextDescribe.setText("\t\t\t\t\t\t\t\t\t"+company.getRemark());
		mTextDescribe.setText(company.getRemark());
		mTextEmai.setText(company.getEmail());
		mTextName.setText(company.getCmpname());
		mTextTel.setText(company.getTelephone1());
		mTextUrl.setText(company.getUrl());
		mTextPorjectName.setText(company.getItemname());
		mTextCContactor.setText(company.getLinkman());

	}

	/**
	 * 更新本地缓存
	 */
	private void refreshLocalData() {
		PreferencesUtil.putValue(PreferencesUtil.KEY_USER_PARENT_NAME,
				company.getCmpname());
		PreferencesUtil.initStoreData();
	}

	private void initControls() {
		mTextName = (TextView) view
				.findViewById(R.id.fragment_show_info_tv_company_name);
		mTextCContactor = (TextView) view
				.findViewById(R.id.fragment_show_info_tv_contactor);
		mTextTel = (TextView) view
				.findViewById(R.id.fragment_show_info_tv_company_tel);
		mTextAddress = (TextView) view
				.findViewById(R.id.fragment_show_info_tv_company_address);
		mTextDescribe = (TextView) view
				.findViewById(R.id.fragment_show_info_tv_company_describe);
		mTextUrl = (TextView) view
				.findViewById(R.id.fragment_show_info_tv_company_url);
		mTextEmai = (TextView) view
				.findViewById(R.id.fragment_show_info_tv_company_email);
		mTextPorjectName = (TextView) view
				.findViewById(R.id.fragment_show_info_tv_project_name);
		mLoadDailog = new DialogLoading(getActivity(),
				getString(R.string.tip_loading));

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}
