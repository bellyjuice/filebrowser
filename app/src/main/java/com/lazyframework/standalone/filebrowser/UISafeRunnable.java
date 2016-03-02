package com.lazyframework.standalone.filebrowser;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class UISafeRunnable implements Runnable {
    private AtomicBoolean cancel = new AtomicBoolean();
    private Handler internalHandler = new InternalHandler();

    public void cancel() {
        cancel.set(true);
        internalHandler.removeCallbacksAndMessages(null);
    }

    public boolean isCancelled() {
        return cancel.get();
    }

    private static class InternalMessageObj {
        private WeakReference<UISafeRunnable> selfRef;
        private Object obj;

        private InternalMessageObj(UISafeRunnable me, Object obj) {
            selfRef = new WeakReference<>(me);
            this.obj = obj;
        }
    }

    private static class InternalHandler extends Handler {

        private InternalHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof InternalMessageObj) {
                InternalMessageObj internalObj = (InternalMessageObj) msg.obj;
                if (internalObj.selfRef == null) {
                    return;
                }
                UISafeRunnable runnable = internalObj.selfRef.get();
                if (runnable != null && !runnable.isCancelled()) {
                    msg.obj = internalObj.obj;
                    runnable.handleMessageInUI(msg);
                }
            }
        }
    }

    public abstract void handleMessageInUI(Message msg);

    protected void sendMessageToUI(int what, Object obj) {
        InternalMessageObj internalObj = new InternalMessageObj(this, obj);
        internalHandler.obtainMessage(what, internalObj).sendToTarget();
    }
}
