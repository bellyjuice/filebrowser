package com.lazyframework.standalone.filebrowser.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.lazyframework.standalone.filebrowser.R;

public abstract class BaseListFragment extends AsyncLoadFragment {
    protected ListView mList;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mList = (ListView) mContentContainer.findViewById(R.id.list);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick((ListView) parent, view, position, id);
            }
        });
        mList.setEmptyView(mEmptyView);
        requestFocus(mList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mList.setAdapter(null);
        mList = null;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
    }

    public void setAdapter(ListAdapter adapter) {
        if (mList != null) {
            mList.setAdapter(adapter);
        }
    }
}
