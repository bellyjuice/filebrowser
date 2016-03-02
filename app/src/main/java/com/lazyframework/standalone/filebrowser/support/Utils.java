package com.lazyframework.standalone.filebrowser.support;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

public class Utils {
	public enum NETWORK_STATE {
		OFFLINE, MOBILE, WIFI,
	}

    @SuppressWarnings("deprecation,unused")
	public static NETWORK_STATE getNetworkState(Context context) {
		if (context == null) {
			return NETWORK_STATE.OFFLINE;
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return NETWORK_STATE.OFFLINE;
		}
		NetworkInfo network = cm.getActiveNetworkInfo();
		if (network == null || !network.isAvailable()) {
			return NETWORK_STATE.OFFLINE;
		}
		NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean mobileOK = (mobile != null && mobile.isConnected());
		boolean wifiOK = (wifi != null && wifi.isConnected());
		if (!mobileOK && !wifiOK) {
			return NETWORK_STATE.OFFLINE;
		}
		if (mobileOK) {
			return NETWORK_STATE.MOBILE;
		} else {
			return NETWORK_STATE.WIFI;
		}
	}

	public static String formatFileSize(long number) {
		float result = number;
		String suffix = "B";
		if (result > 900) {
			suffix = "KB";
			result = result / 1024;
		}
		if (result > 900) {
			suffix = "MB";
			result = result / 1024;
		}
		if (result > 900) {
			suffix = "GB";
			result = result / 1024;
		}
		if (result > 900) {
			suffix = "TB";
			result = result / 1024;
		}
		if (result > 900) {
			suffix = "PB";
			result = result / 1024;
		}
		String value;
		if (result == 0) {
			value = "0";
		} else if (result < 1) {
			value = String.format("%.1f", result);
		} else if (result < 10) {
			value = String.format("%.1f", result);
		} else if (result < 100) {
			value = String.format("%.0f", result);
		} else {
			value = String.format("%.0f", result);
		}
		return value + suffix;
	}

	public static String getMimeType(String filePath) {
		String type = null;
		String extension = getSuffix(filePath);
		if (!TextUtils.isEmpty(extension)) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		if (TextUtils.isEmpty(type)) {
			type = "file/*";
		}
		return type;
	}
	
	public static String getSuffix(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return null;
		}
		int dotPos = filePath.lastIndexOf('.');
		if (dotPos >= 0 && dotPos < filePath.length() - 1) {
			return filePath.substring(dotPos + 1);
		}
		return null;
	}

	public static void openFileExternal(Context context, String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return;
		}
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = getMimeType(filePath);
		intent.setDataAndType(Uri.fromFile(new File(filePath)), type);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
