package com.lazyframework.standalone.filebrowser.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lazyframework.standalone.filebrowser.demo.BaseActivity;
import com.lazyframework.standalone.filebrowser.UISafeExecutor;
import com.lazyframework.standalone.filebrowser.UISafeExecutor.UISafeTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class BaseFragment extends Fragment {
    final private Handler mHandler = new Handler();
    private static final String TAG_UI_LIFE_CYCLE = BaseActivity.TAG_UI_LIFE_CYCLE;
    private List<WeakReference<UISafeTask>> mTaskList = new ArrayList<>();
    private boolean mDebugLifeCycle;
    private UISafeExecutor mExecutor = new UISafeExecutor();

    @SuppressWarnings("unused")
    public void setDebugLifeCycle(boolean set) {
        mDebugLifeCycle = set;
    }

    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    @SuppressWarnings("unused")
    public boolean onBackPressed() {
        return false;
    }

    public ActionBar getSupportActionBar() {
        FragmentActivity fa = getActivity();
        if (fa instanceof AppCompatActivity) {
            AppCompatActivity aca = (AppCompatActivity) fa;
            return aca.getSupportActionBar();
        }
        return null;
    }

    public WeakReference<UISafeTask> runTask(Runnable runnable) {
        WeakReference<UISafeTask> taskRef = mExecutor.execute(runnable);
        mTaskList.add(taskRef);
        return taskRef;
    }

    public void cancelTask(WeakReference<UISafeTask> taskRef) {
        if (taskRef == null) {
            return;
        }
        boolean removed = mTaskList.remove(taskRef);
        if (removed) {
            mExecutor.cancel(taskRef);
        }
    }

    public void cancelAllTasks() {
        if (mTaskList == null || mTaskList.size() == 0) {
            return;
        }
        mExecutor.cancelAll(mTaskList);
        mTaskList.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onCreate");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onViewCreated");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onActivityCreated");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onStart");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onResume");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onPause");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onStop");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onDestroyView");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onDestroy");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onDetach");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onCreateView");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onHiddenChanged");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onSaveInstanceState");
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (mDebugLifeCycle) {
            Log.d(TAG_UI_LIFE_CYCLE, this.getClass().getSimpleName() + ":onViewStateRestored");
        }
    }
}
