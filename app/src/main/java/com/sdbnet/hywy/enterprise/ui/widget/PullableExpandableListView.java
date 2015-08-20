package com.sdbnet.hywy.enterprise.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

public class PullableExpandableListView extends ExpandableListView implements
		Pullable {

	public PullableExpandableListView(Context context) {
		super(context);
	}

	public PullableExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableExpandableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown() {
		if (getCount() == 0) {

			return true;
		} else if (getFirstVisiblePosition() == 0
				&& getChildAt(0).getTop() >= 0) {

			return true;
		} else
			return false;
	}

	@Override
	public boolean canPullUp() {
		if (getCount() == 0) {

			return true;
		} else if (getLastVisiblePosition() == (getCount() - 1)) {

			if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
					&& getChildAt(
							getLastVisiblePosition()
									- getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
				return true;
		}
		return false;
	}

}
