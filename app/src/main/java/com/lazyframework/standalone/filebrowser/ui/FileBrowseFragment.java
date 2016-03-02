package com.lazyframework.standalone.filebrowser.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lazyframework.standalone.filebrowser.FileBrowserRunnable;
import com.lazyframework.standalone.filebrowser.R;
import com.lazyframework.standalone.filebrowser.UISafeExecutor;
import com.lazyframework.standalone.filebrowser.support.Utils;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBrowseFragment extends BaseListFragment {
    private static final String DEFAULT_PATH = Environment.getExternalStorageDirectory().getPath();
    private String mCurrentPath = DEFAULT_PATH;
    private static final String SAVED_PATH = "SAVED_PATH";
    private static final String SAVED_TOP_POSITION = "SAVED_TOP_POSITION";
    private static final String SAVED_OFFSET_FROM_TOP = "SAVED_OFFSET_FROM_TOP";
    private FileBrowseAdapter mAdapter;
    private WeakReference<UISafeExecutor.UISafeTask> mCurrentTask;

    private static class PositionInfo {
        int topPosition;
        int offsetFromTop;
    }

    private Map<String, PositionInfo> mPositionMap = new HashMap<>();
    private boolean mPositionOnce;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lazy_extended_list_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            String savedPath = savedInstanceState.getString(SAVED_PATH);
            if (!TextUtils.isEmpty(savedPath)) {
                mCurrentPath = savedPath;
                PositionInfo pi = new PositionInfo();
                pi.topPosition = savedInstanceState.getInt(SAVED_TOP_POSITION);
                pi.offsetFromTop = savedInstanceState.getInt(SAVED_OFFSET_FROM_TOP);
                mPositionMap.put(mCurrentPath, pi);
            }
        }
        createAdapter();
        browseTo(mCurrentPath);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
        mCurrentTask = null;
        cancelAllTasks();
    }

    private void createAdapter() {
        mAdapter = new FileBrowseAdapter(getActivity());
        setAdapter(mAdapter);
    }

    private void browseTo(String path) {
        if (mCurrentTask != null) {
            cancelTask(mCurrentTask);
        }
        mPositionOnce = true;
        FileBrowserRunnable.FileBrowseParamBuilder builder = new FileBrowserRunnable.FileBrowseParamBuilder();
        builder.addBrowseFolder(path, 4).setExcludeFolder(true).setOnBrowseListener(new FileBrowserRunnable.OnBrowseListener() {

            @Override
            public void onFileDataLoaded(List<FileBrowserRunnable.FileData> list) {
                hideLoading();
                if (mPositionOnce) {
                    mAdapter.setData(null);
                }
                mAdapter.addData(list);
                if (mPositionOnce) {
                    PositionInfo pi = mPositionMap.get(mCurrentPath);
                    if (pi != null) {
                        ((ExtendedListView) mList).setPositionFromTop(pi.topPosition, pi.offsetFromTop);
                    } else {
                        ((ExtendedListView) mList).setPositionFromTop(0, 0);
                    }
                    mPositionOnce = false;
                }
            }

            @Override
            public void onExtraFileDataLoaded(Map<String, FileBrowserRunnable.ExtraFileData> map) {
                List<FileBrowserRunnable.FileData> list = mAdapter.getData();
                for (FileBrowserRunnable.FileData fd : list) {
                    FileBrowserRunnable.ExtraFileData efd = map.get(fd.filePath);
                    if (efd != null) {
                        fd.extra = efd;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onBrowsingFolder(String folder) {
                ActionBar ab = getSupportActionBar();
                if (ab != null) {
                    String shorterFolder = folder.replaceFirst(mCurrentPath, "");
                    ab.setTitle(shorterFolder);
                }
            }
        }).addMimeTypeFilter("image/");
        mCurrentTask = runTask(new FileBrowserRunnable(builder));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Object o = mAdapter.getItem(position);
        if (o instanceof FileBrowserRunnable.FileData) {
            FileBrowserRunnable.FileData data = (FileBrowserRunnable.FileData) o;
            if (data.isDirectory) {
                saveCurrentPathPosition();
                mCurrentPath = data.filePath;
                browseTo(mCurrentPath);
            } else {
                Utils.openFileExternal(getActivity(), data.filePath);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString(SAVED_PATH, mCurrentPath);
            int topIndex = 0;
            int offsetFromTop = 0;
            PositionInfo pi = mPositionMap.get(mCurrentPath);
            if (pi != null) {
                topIndex = pi.topPosition;
                offsetFromTop = pi.offsetFromTop;
            }
            outState.putInt(SAVED_TOP_POSITION, topIndex);
            outState.putInt(SAVED_OFFSET_FROM_TOP, offsetFromTop);
        }
    }

    @Override
    public boolean onBackPressed() {
        File current = new File(mCurrentPath);
        if (DEFAULT_PATH.equals(current.getPath())) {
            return false;
        } else {
            clearCurrentPathPosition();
            mCurrentPath = current.getParent();
            browseTo(mCurrentPath);
            return true;
        }
    }

    private void saveCurrentPathPosition() {
        if (!TextUtils.isEmpty(mCurrentPath)) {
            PositionInfo pi = new PositionInfo();
            pi.topPosition = mList.getFirstVisiblePosition();
            if (mList.getChildCount() > 0) {
                View child = mList.getChildAt(0);
                pi.offsetFromTop = child.getTop();
            }
            mPositionMap.put(mCurrentPath, pi);
        }
    }

    private void clearCurrentPathPosition() {
        mPositionMap.remove(mCurrentPath);
    }
}
