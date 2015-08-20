package com.sdbnet.hywy.enterprise.ui.base;

import android.support.v4.app.Fragment;
import android.widget.Toast;

public class BaseFrament extends Fragment {
	protected void showMsg(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

	protected void showMsg(int resId) {
		showMsg(getString(resId));
	}

	protected void showMsgLong(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}

	protected void showMsgLong(int resId) {
		showMsgLong(getString(resId));
	}
}
