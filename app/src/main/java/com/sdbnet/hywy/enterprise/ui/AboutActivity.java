package com.sdbnet.hywy.enterprise.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdbnet.hywy.enterprise.R;
import com.sdbnet.hywy.enterprise.ui.base.BaseActivity;
import com.sdbnet.hywy.enterprise.utils.UtilsAndroid;

public class AboutActivity extends BaseActivity {
	private ImageView mBack;
	private TextView tvVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		initControls();
	}

	private void initControls() {
		mBack = (ImageView) findViewById(R.id.common_view_title_img);
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.about_us);
		tvVersion = (TextView) findViewById(R.id.version);
		tvVersion.setText(UtilsAndroid.Set.getVersionName(this));
	}
}
