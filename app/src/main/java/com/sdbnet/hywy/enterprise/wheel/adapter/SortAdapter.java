package com.sdbnet.hywy.enterprise.wheel.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.model.SortModel;
import com.sdbnet.hywy.enterprise.model.VehicleSortModel;
import com.sdbnet.hywy.enterprise.ui.EditInfoActivity;
import com.sdbnet.hywy.enterprise.ui.GroupMotorsActivity;
import com.sdbnet.hywy.enterprise.ui.MainActivity;
import com.sdbnet.hywy.enterprise.utils.UtilsCommon;

import org.apache.http.message.BasicNameValuePair;

import java.util.List;

public class SortAdapter extends BaseAdapter implements SectionIndexer {
	private List<VehicleSortModel> list = null;
	private Context mContext;
	private OnClickListener listener;
	private int redict;

	public SortAdapter(Context mContext, List<VehicleSortModel> list,int redict) {
		this.mContext = mContext;
		this.list = list;
		this.redict = redict;
		initControls();
	}

	private void initControls() {
		listener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(redict == 1){
					VehicleSortModel sortModel = (VehicleSortModel) arg0.getTag();
					BasicNameValuePair bvp = new BasicNameValuePair("pid", sortModel.getHdiId());
					BasicNameValuePair bvp2 = new BasicNameValuePair("plate", sortModel.getHdiPlate());
					BasicNameValuePair bvp3 = new BasicNameValuePair("mTag", "5");
					UtilsCommon.start_activity((GroupMotorsActivity) mContext,MainActivity.class, bvp, bvp2, bvp3);
					return;
				} else if(redict ==2){
					VehicleSortModel sortModel = (VehicleSortModel) arg0.getTag();
					Intent intent = new Intent();
					intent.setClass(mContext, EditInfoActivity.class);
					intent.putExtra("pid", sortModel.getHdiId());
					intent.putExtra("ustatus", sortModel.getUstatus());
					((GroupMotorsActivity) mContext).startActivityForResult(intent, 20);
					return;
				}
			}
		};
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<VehicleSortModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_motor_vehicles_show, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.tvVehicle);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_AutoPlay);
		layout.setTag(mContent);
		layout.setOnClickListener(listener);
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		viewHolder.tvTitle.setText(this.list.get(position).getSortContent());
		return view;

	}

	final static class ViewHolder {
		TextView tvTitle;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	@Override
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}