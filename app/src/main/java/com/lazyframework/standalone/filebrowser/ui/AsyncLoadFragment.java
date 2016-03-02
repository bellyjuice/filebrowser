package com.lazyframework.standalone.filebrowser.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.lazyframework.standalone.filebrowser.R;

/**
 * All fragments which are derived from me should extend layout template
 * "lazy_async_load_fragment_template.xml"
 *
 * @author Administrator
 */

public abstract class AsyncLoadFragment extends BaseFragment {
    final private class RequestFocusRunnable implements Runnable {
        private ViewGroup mSubject;

        private RequestFocusRunnable(ViewGroup subject) {
            mSubject = subject;
        }

        @Override
        public void run() {
            if (mSubject != null) {
                mSubject.focusableViewAvailable(mSubject);
            }
        }
    }

    /**
     * 内容页面的容器。所有内容页面应该都在这个容器里。
     */
    protected View mContentContainer;
    /**
     * 加载内容时的进度条。
     */
    protected View mProgressBar;
    /**
     * 加载失败或者无内容时的提示，它也应该在内容容器里。
     */
    protected View mEmptyView;

    private boolean mContentShown;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mContentContainer = view.findViewById(R.id.content_container);
        mEmptyView = view.findViewById(R.id.empty);
        showLoading();
    }

    /**
     * 初始化控件之后，如果需要为它请求焦点，可以用这个方法。
     *
     * @param subject 需要请求焦点的控件，类型为{@link ViewGroup}
     */
    protected void requestFocus(ViewGroup subject) {
        post(new RequestFocusRunnable(subject));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContentContainer = null;
        mProgressBar = null;
        mEmptyView = null;
    }

    public void showLoading() {
        setContentShown(false, false);
    }

    public void hideLoading() {
        boolean animate = false;
        if (getView() != null && getView().getWindowToken() != null) {
            animate = true;
        }
        setContentShown(true, animate);
    }

    private void setContentShown(boolean shown, boolean animate) {
        if (mContentShown == shown) {
            return;
        }
        mContentShown = shown;
        if (shown) {
            if (animate) {
                //mProgressBar.startAnimation(getSwitchOutAnimation(getActivity()));
                mContentContainer.startAnimation(getSwitchInAnimation(getActivity()));
            } else {
                mProgressBar.clearAnimation();
                mContentContainer.clearAnimation();
            }
            mProgressBar.setVisibility(View.INVISIBLE);
            mContentContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressBar.startAnimation(getSwitchInAnimation(getActivity()));
                mContentContainer.startAnimation(getSwitchOutAnimation(getActivity()));
            } else {
                mProgressBar.clearAnimation();
                mContentContainer.clearAnimation();
            }
            mProgressBar.setVisibility(View.VISIBLE);
            mContentContainer.setVisibility(View.INVISIBLE);
        }
    }

    protected Animation getSwitchInAnimation(Context context) {
        return AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
    }

    protected Animation getSwitchOutAnimation(Context context) {
        return AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
    }
}
