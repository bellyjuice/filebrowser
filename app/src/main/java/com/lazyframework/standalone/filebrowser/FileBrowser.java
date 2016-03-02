package com.lazyframework.standalone.filebrowser;

import java.lang.ref.WeakReference;
import com.lazyframework.standalone.filebrowser.FileBrowserRunnable.FileBrowseParamBuilder;

/**
 * Created by Administrator on 2016-3-1.
 * Browse file in thread pool
 */
@SuppressWarnings("unused")
public class FileBrowser extends UISafeExecutor {

    public WeakReference<UISafeTask> browse(FileBrowseParamBuilder builder) {
        FileBrowserRunnable runnable = new FileBrowserRunnable(builder);
        return execute(runnable);
    }
}
