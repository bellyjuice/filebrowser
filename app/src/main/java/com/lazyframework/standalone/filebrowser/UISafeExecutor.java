package com.lazyframework.standalone.filebrowser;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UISafeExecutor {
    private static final int CORE_POOL_SIZE = 0;
    private static final int MAX_POOL_SIZE = 1;
    private ThreadPoolExecutor mInnerExecutor;
	
	public static class UISafeTask extends FutureTask<Void> {
		private Runnable r;

		public UISafeTask(Runnable runnable, Void result) {
			super(runnable, result);
			r = runnable;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			if (r instanceof UISafeRunnable) {
				((UISafeRunnable) r).cancel();
			}
			return super.cancel(mayInterruptIfRunning);
		}
	}

    public UISafeExecutor() {
        mInnerExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @SuppressWarnings("unused")
	public WeakReference<UISafeTask> execute(Runnable runnable) {
		UISafeTask task = new UISafeTask(runnable, null);
		mInnerExecutor.execute(task);
		return new WeakReference<>(task);
	}

    @SuppressWarnings("unused")
	public void cancel(WeakReference<UISafeTask> taskRef) {
		if (taskRef == null) {
			return;
		}
		UISafeTask task = taskRef.get();
		if (task != null) {
			task.cancel(true);
			mInnerExecutor.purge();
		}
	}

    @SuppressWarnings("unused")
	public void cancelAll(List<WeakReference<UISafeTask>> list) {
		if (list == null || list.size() <= 0) {
			return;
		}
		for (WeakReference<UISafeTask> taskRef : list) {
			UISafeTask task = taskRef.get();
			if (task != null) {
				task.cancel(true);
			}
		}
		mInnerExecutor.purge();
	}
}
