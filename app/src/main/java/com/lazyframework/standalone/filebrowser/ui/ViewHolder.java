package com.lazyframework.standalone.filebrowser.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
    public TextView primaryText;
	public ImageView primaryImage;
    public TextView secondaryText;

	public ViewHolder(View container, int primaryImageId, int primaryTextId, int secondaryTextId) {
        primaryText = (TextView) container.findViewById(primaryTextId);
        secondaryText = (TextView) container.findViewById(secondaryTextId);
		primaryImage = (ImageView) container.findViewById(primaryImageId);
	}
}
