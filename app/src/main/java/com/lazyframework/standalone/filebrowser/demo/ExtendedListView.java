package com.lazyframework.standalone.filebrowser.demo;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.widget.ListView;

public class ExtendedListView extends ListView {
	private int mTopPosition;
	private int mOffsetFromTop;
	private boolean mPositionSet;

	public ExtendedListView(Context context) {
		super(context);
	}

	public ExtendedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExtendedListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (mPositionSet) {
			setSelectionFromTop(mTopPosition, mOffsetFromTop);
			mPositionSet = false;
		}
		super.onLayout(changed, l, t, r, b);
	}

	public void setPositionFromTop(int position, int offset) {
		mTopPosition = position;
		mOffsetFromTop = offset;
		mPositionSet = true;
		if (VERSION.SDK_INT >= 8) {
			// stop fling
			smoothScrollBy(0, 0);
		}
		requestLayout();
	}
}
