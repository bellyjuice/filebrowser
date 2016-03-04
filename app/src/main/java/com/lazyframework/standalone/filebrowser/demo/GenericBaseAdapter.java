package com.lazyframework.standalone.filebrowser.demo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class GenericBaseAdapter<Item> extends BaseAdapter {
	private List<Item> mList;
	private LayoutInflater mInflater;
	
	public GenericBaseAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}
	
	public void setData(List<Item> list) {
		mList = list;
		notifyDataSetChanged();
	}
	
	public void addData(List<Item> list) {
		if (mList == null) {
			mList = list;
		} else {
			mList.addAll(list);
		}
		notifyDataSetChanged();
	}
	
	public List<Item> getData() {
		return mList;
	}

	@Override
	public int getCount() {
		if (mList == null) {
			return 0;
		}
		return mList.size();
	}

	@Override
	public Item getItem(int position) {
		if (mList == null) {
			return null;
		}
		if (position < 0 || position >= mList.size()) {
			return null;
		}
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getConvertView(position, mInflater);
			Object holder = getViewHolder(position, convertView);
			convertView.setTag(holder);
		}
		Object holder = convertView.getTag();
		Item item = getItem(position);
		if (item != null) {
			bindItemToViewHolder(position, item, holder, convertView);
		}
		return convertView;
	}

	public abstract View getConvertView(int position, LayoutInflater inflater);
	
	public abstract Object getViewHolder(int position, View convertView);
	
	public abstract void bindItemToViewHolder(int position, Item item, Object viewHolder, View convertView);
}
