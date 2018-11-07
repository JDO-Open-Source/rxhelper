package com.jidouauto.eddie.mvpdemo.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProgressLoadingView implements LoadingView {

    private final LinkedHashMap<String, LoadingInfo> pendingDialogMessage = new LinkedHashMap<>();
    private Context mContext;
    private ProgressDialog progressDialog;

    public ProgressLoadingView(Context context) {
        mContext = context;
    }

    @Override
    public void showLoading(String tag) {
        showLoading(tag, null);
    }

    @Override
    public void showLoading(String tag, String message) {
        showLoading(tag, null, message);
    }

    @Override
    public void showLoading(String tag, String title, String message) {
        showLoading(tag, title, message, false);
    }

    @Override
    public void showLoading(String tag, String title, String message, boolean cancelable) {
        pendingDialogMessage.put(tag, new LoadingInfo(title, message, cancelable));
        checkLoading();
    }

    @Override
    public void cancel(String tag) {
        pendingDialogMessage.remove(tag);
        checkLoading();
    }

    @Override
    public void cancelAll() {
        pendingDialogMessage.clear();
        checkLoading();
    }

    private DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            Map.Entry<String, LoadingInfo> lastElement = getLatestLoadingInfo();
            if (lastElement != null) {
                pendingDialogMessage.remove(lastElement.getKey());
                checkLoading();
            }
        }
    };

    private Map.Entry<String, LoadingInfo> getLatestLoadingInfo() {
        Iterator<Map.Entry<String, LoadingInfo>> iterator = pendingDialogMessage.entrySet().iterator();
        Map.Entry<String, LoadingInfo> lastElement = null;
        while (iterator.hasNext()) {
            lastElement = iterator.next();
        }
        return lastElement;
    }

    private void checkLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setOnDismissListener(null);
            progressDialog.dismiss();
            progressDialog.setOnDismissListener(dismissListener);
        }
        if (pendingDialogMessage != null && pendingDialogMessage.size() > 0) {
            Map.Entry<String, LoadingInfo> lastElement = getLatestLoadingInfo();
            if (progressDialog == null) {
                progressDialog = ProgressDialog.show(mContext, lastElement.getValue().title, lastElement.getValue().message);
                progressDialog.setOnDismissListener(dismissListener);
            } else if (!progressDialog.isShowing()) {
                progressDialog.setTitle(lastElement.getValue().title);
                progressDialog.setMessage(lastElement.getValue().message);
                progressDialog.setCancelable(lastElement.getValue().cancelable);
                progressDialog.show();
            } else {
                progressDialog.setTitle(lastElement.getValue().title);
                progressDialog.setMessage(lastElement.getValue().message);
                progressDialog.setCancelable(lastElement.getValue().cancelable);
            }

        }
    }

    class LoadingInfo {
        String title;
        String message;
        boolean cancelable;

        public LoadingInfo(String title, String message, boolean cancelable) {
            this.title = title;
            this.message = message;
            this.cancelable = cancelable;
        }
    }
}
