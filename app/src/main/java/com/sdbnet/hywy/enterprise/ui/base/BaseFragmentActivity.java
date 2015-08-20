package com.sdbnet.hywy.enterprise.ui.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import com.sdbnet.hywy.enterprise.ActivityStackManager;
import com.sdbnet.hywy.enterprise.R;

public class BaseFragmentActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// MobclickAgent.onError(this);
		ActivityStackManager.getStackManager().pushActivity(this);

	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	public void defaultFinish() {
		super.finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityStackManager.getStackManager().popActivity(this);
	}

}
