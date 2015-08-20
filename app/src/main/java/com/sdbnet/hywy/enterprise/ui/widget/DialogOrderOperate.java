package com.sdbnet.hywy.enterprise.ui.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.net.AsyncHttpService;
import com.sdbnet.hywy.enterprise.ui.HistoryLocusActivity;
import com.sdbnet.hywy.enterprise.ui.ImageBrowserActivity;
import com.sdbnet.hywy.enterprise.ui.PreciseLocateActivity;
import com.sdbnet.hywy.enterprise.ui.UserLoginActivity;
import com.sdbnet.hywy.enterprise.utils.Constants;
import com.sdbnet.hywy.enterprise.utils.PreferencesUtil;
import com.sdbnet.hywy.enterprise.utils.UtilsCommon;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogOrderOperate extends Dialog {

	Activity context;
	private SimpleAdapter lvAdapter;
	private static final String LIST_TEXT = "tvMotorCont";
	private static final String LIST_IMAGEVIEW = "ibMotorImg";
	private ListView lvMotorDia;

	private Map<String, String> map;

	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();

	public DialogOrderOperate(Activity context, Map<String, String> map) {
		super(context, R.style.MotorDialogStyle);
		this.context = context;
		this.map = map;
	}

	public DialogOrderOperate(Activity context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_order_operate_list);

		lvMotorDia = (ListView) findViewById(R.id.lvDialog);
		initListView();
	}

	private void initListView() {
		final List<Map<String, Object>> items = getData();
		lvAdapter = new SimpleAdapter(context, items,
				R.layout.dialog_order_operate_item, new String[] { LIST_TEXT,
						LIST_IMAGEVIEW }, new int[] { R.id.tvMotorCont,
						R.id.ibMotorImg });
		lvMotorDia.setAdapter(lvAdapter);
		lvMotorDia.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, Object> item = items.get(position);
				if (Constants.Value.TEXT_CALL.equals(item.get(LIST_TEXT))) {
					// 拨打电话
					Intent telIntent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel:"
									+ map.get(Constants.Feild.KEY_LOCA_TEL)));
					context.startActivity(telIntent);

				} else if (Constants.Value.TEXT_LOCAL.equals(item
						.get(LIST_TEXT))) {
					// 历史轨迹
					// BasicNameValuePair bvp5 = new BasicNameValuePair(
					// Constants.Feild.KEY_PLATFORM_ORIGINAL_NO,
					// map.get(Constants.Feild.KEY_PLATFORM_ORIGINAL_NO));
					// BasicNameValuePair bvp6 = new BasicNameValuePair(
					// Constants.Feild.KEY_LINE_ID, map
					// .get(Constants.Feild.KEY_LINE_ID));
					// UtilsCommon.start_activity(context,
					// HistoryLocusActivity.class, bvp5, bvp6);
					Intent intent = new Intent(context,
							HistoryLocusActivity.class);
					intent.putExtra(Constants.Feild.KEY_PLATFORM_ORIGINAL_NO,
							map.get(Constants.Feild.KEY_PLATFORM_ORIGINAL_NO));
					intent.putExtra(Constants.Feild.KEY_LINE_ID,
							map.get(Constants.Feild.KEY_LINE_ID));
					intent.putExtra(HistoryLocusActivity.FILED_QUERY_TYPE,
							HistoryLocusActivity.QUERY_TYPE_ORDER);
					context.startActivity(intent);

				} else if (Constants.Value.TEXT_BROSWER.equals(item
						.get(LIST_TEXT))) {
					// 附件浏览
					loadAttachs(map.get(Constants.Feild.KEY_TRACE_ID));
				} else if (Constants.Value.TEXT_POSITION.equals(item
						.get(LIST_TEXT))) {
					// 车辆定位
					BasicNameValuePair bvp1 = new BasicNameValuePair(
							Constants.Feild.KEY_COMPANY_ID, map
									.get(Constants.Feild.KEY_COMPANY_ID));
					BasicNameValuePair bvp2 = new BasicNameValuePair(
							Constants.Feild.KEY_ITEM_ID, map
									.get(Constants.Feild.KEY_ITEM_ID));
					BasicNameValuePair bvp3 = new BasicNameValuePair(
							Constants.Feild.KEY_STAFF_ID, map
									.get(Constants.Feild.KEY_STAFF_ID));
					BasicNameValuePair bvp4 = new BasicNameValuePair(
							Constants.Feild.KEY_LOCA_TEL, map
									.get(Constants.Feild.KEY_LOCA_TEL));

					UtilsCommon
							.start_activity(context,
									PreciseLocateActivity.class, bvp1, bvp2,
									bvp3, bvp4);
				}
				DialogOrderOperate.this.dismiss();

			}
		});
	}

	/**
	 * 获取操作项列表
	 * 
	 * @return
	 */
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> iteMap = null;

		if (Constants.Value.YES.equals(map.get(Constants.Feild.KEY_IS_CALL))
				&& !TextUtils.isEmpty(map.get(Constants.Feild.KEY_LOCA_TEL))) {
			iteMap = new HashMap<String, Object>();
			iteMap.put(LIST_TEXT, Constants.Value.TEXT_CALL);
			iteMap.put(LIST_IMAGEVIEW, R.drawable.white_driver);
			list.add(iteMap);
		}

		if (Constants.Value.YES.equals(map.get(Constants.Feild.KEY_IS_LOCAATE))
				&& !TextUtils.isEmpty(map.get(Constants.Feild.KEY_LINE_ID))) {
			iteMap = new HashMap<String, Object>();
			iteMap.put(LIST_TEXT, Constants.Value.TEXT_POSITION);
			iteMap.put(LIST_IMAGEVIEW, R.drawable.white_location);
			list.add(iteMap);

			iteMap = new HashMap<String, Object>();
			iteMap.put(LIST_TEXT, Constants.Value.TEXT_LOCAL);
			iteMap.put(LIST_IMAGEVIEW, R.drawable.white_locus);
			list.add(iteMap);
		}
		// 附件浏览图标
		int imgCount = 0;
		try {
			imgCount = Integer.parseInt(map.get(Constants.Feild.KEY_IMG_COUNT));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Constants.Value.YES.equals(map.get(Constants.Feild.KEY_IS_SCAN))
				&& imgCount > 0) {
			iteMap = new HashMap<String, Object>();
			iteMap.put(LIST_TEXT, Constants.Value.TEXT_BROSWER);
			iteMap.put(LIST_IMAGEVIEW, R.drawable.white_business);
			list.add(iteMap);
		}
		return list;
	}

	/**
	 * 加载附件
	 * 
	 * @param traceid
	 */
	private void loadAttachs(String traceid) {
		AsyncHttpService.getTraceAttachs(traceid,
				new JsonHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Toast.makeText(
								context,
								context.getResources().getString(
										R.string.httpError), Toast.LENGTH_SHORT)
								.show();
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						System.out.println("attachs: " + response.toString());
						try {
							int errCode = response
									.getInt(Constants.Feild.KEY_ERROR_CODE);
							if (errCode != 0) {
								String msg = response
										.getString(Constants.Feild.KEY_MSG);
								switch (errCode) {
								case 41:
									returnLogin(msg);
									break;
								case 42:
									returnLogin(msg);
									break;
								default:
									Toast.makeText(context, msg,
											Toast.LENGTH_SHORT).show();
									break;
								}
								return;
							}

							JSONArray arrs = response
									.getJSONArray(Constants.Feild.KEY_PICS);
							list.clear();

							if (arrs.length() == 0) {
								Toast.makeText(context, "该用户未上传图片附件",
										Toast.LENGTH_SHORT).show();
								return;
							}

							// 如果用户上传了附件，则跳转到图片浏览界面
							Intent intent = new Intent(context,
									ImageBrowserActivity.class);
							intent.putExtra(Constants.Feild.KEY_TRACE_ID,
									map.get(Constants.Feild.KEY_TRACE_ID));
							context.startActivity(intent);

						} catch (Exception e) {
							e.printStackTrace();
						}
						super.onSuccess(statusCode, headers, response);
					}

				}, context);
	}

	private void returnLogin(String msg) {
		PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS, false);
		new AlertDialog.Builder(context)
				.setTitle("系统提示")
				.setMessage(msg)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 点击ok，退出当前帐号
								PreferencesUtil.clearLocalData(PreferenceManager
										.getDefaultSharedPreferences(context));
								Intent intent = new Intent(context,
										UserLoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
										| Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(intent);
							}

						}).show();
	}
}
