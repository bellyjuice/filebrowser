package com.lazyframework.standalone.filebrowser.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lazyframework.standalone.filebrowser.FileBrowserRunnable.FileData;
import com.lazyframework.standalone.filebrowser.R;
import com.lazyframework.standalone.filebrowser.support.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-3-1.
 * For browse list ui
 */
@SuppressWarnings("unused")
public class FileBrowseAdapter extends BaseAdapter {
    private String itemCountFormat;
    private List<FileData> list = new ArrayList<>();
    private LayoutInflater inflater;
    private DisplayImageOptions imageOp = new DisplayImageOptions.Builder().cacheInMemory(true).resetViewBeforeLoading(true)
            .displayer(new FadeInBitmapDisplayer(500, true, true, false)).build();

    public FileBrowseAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        itemCountFormat = context.getString(R.string.x_items);
    }

    public void setData(List<FileData> list) {
        this.list.clear();
        addData(list);
    }

    public void addData(List<FileData> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public List<FileData> getData() {
        return list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lazy_list_item_1_image_2_text_fixed_height, null);
            ViewHolder holder = new ViewHolder(convertView, R.id.list_item_primary_image, R.id.list_item_primary_text, R.id.list_item_secondary_text);
            convertView.setTag(holder);
        }
        Object objHolder = convertView.getTag();
        Object objItem = getItem(position);
        if (objHolder instanceof ViewHolder && objItem instanceof FileData) {
            ViewHolder holder = (ViewHolder) objHolder;
            FileData item = (FileData) objItem;
            String mimeType = Utils.getMimeType(item.filePath);
            if (holder.primaryImage != null) {
                if (!TextUtils.isEmpty(mimeType)
                        && (mimeType.startsWith("video/") || mimeType.startsWith("image/"))) {
                    ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.FILE.wrap(item.filePath), holder.primaryImage, imageOp);
                } else {
                    int iconId = R.mipmap.file_browser_file;
                    if (item.isDirectory) {
                        iconId = R.mipmap.file_browser_folder;
                    }
                    ImageLoader.getInstance().cancelDisplayTask(holder.primaryImage);
                    holder.primaryImage.setImageResource(iconId);
                }
            }
            if (holder.primaryText != null) {
                holder.primaryText.setText(item.name);
            }
            if (holder.secondaryText != null) {
                if (item.isDirectory) {
                    if (item.extra != null) {
                        String itemCount = String.format(itemCountFormat, item.extra.fileCountInFolder);
                        holder.secondaryText.setText(itemCount);
                        holder.secondaryText.setVisibility(View.VISIBLE);
                    } else {
                        holder.secondaryText.clearAnimation();
                        holder.secondaryText.setVisibility(View.INVISIBLE);
                    }
                } else {
                    String size = Utils.formatFileSize(item.fileLength);
                    holder.secondaryText.setText(size);
                }
            }
        }
        return convertView;
    }
}
