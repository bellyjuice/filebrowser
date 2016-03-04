package com.lazyframework.standalone.filebrowser.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lazyframework.standalone.filebrowser.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lazy_sample_activity);
        FileBrowseFragment f = new FileBrowseFragment();
        f.setDebugLifeCycle(true);
        addFragmentSafe(R.id.fragment_container, f, null);
        initImageLoader();
    }

    @Override
    public void onBackPressed() {
        Fragment f = findFragmentById(R.id.fragment_container);
        if (f instanceof BaseFragment) {
            BaseFragment bf = (BaseFragment) f;
            if (bf.onBackPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3).threadPriority(Thread.MIN_PRIORITY).memoryCacheSizePercentage(12)
                .tasksProcessingOrder(QueueProcessingType.FIFO).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
    }
}