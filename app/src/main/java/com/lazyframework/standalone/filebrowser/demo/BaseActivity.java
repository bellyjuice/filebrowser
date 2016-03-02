package com.lazyframework.standalone.filebrowser.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

public class BaseActivity extends AppCompatActivity {
	public static final String TAG_UI_LIFE_CYCLE = "ui_life_cycle";
	private boolean mDebugLifeCycle;

    @SuppressWarnings("unused")
	public void setDebugLifeCycle(boolean set) {
		mDebugLifeCycle = set;
	}

    @SuppressWarnings("unused")
	public void addFragmentSafe(int containerId, Fragment fragment, String tag) {
		if (containerId > 0) {
			Fragment old = findFragmentById(containerId);
			if (old != null) {
				return;
			}
		}
		if (!TextUtils.isEmpty(tag)) {
			Fragment old = findFragmentByTag(tag);
			if (old != null) {
				return;
			}
		}
		FragmentManager manager = getSupportFragmentManager();
		manager.beginTransaction().add(containerId, fragment, tag).commitAllowingStateLoss();
	}

	public Fragment findFragmentById(int containerId) {
		FragmentManager manager = getSupportFragmentManager();
        return manager.findFragmentById(containerId);
	}

	public Fragment findFragmentByTag(String tag) {
		FragmentManager manager = getSupportFragmentManager();
        return manager.findFragmentByTag(tag);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mDebugLifeCycle) {
			Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onCreate");
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mDebugLifeCycle) {
			Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onStop");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDebugLifeCycle) {
			Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onDestroy");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mDebugLifeCycle) {
			Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onPause");
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (mDebugLifeCycle) {
			Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onNewIntent");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mDebugLifeCycle) {
			Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onResume");
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mDebugLifeCycle) {
			Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onStart");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mDebugLifeCycle) {
			Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onSaveInstanceState");
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (mDebugLifeCycle) {
			Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onRestoreInstanceState");
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (mDebugLifeCycle) {
			Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onRestart");
		}
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		if (mDebugLifeCycle) {
			Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onUserLeaveHint");
		}
	}

	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		if (mDebugLifeCycle) {
			Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onUserInteraction");
		}
	}
}
