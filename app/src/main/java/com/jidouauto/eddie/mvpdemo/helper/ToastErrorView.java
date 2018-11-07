package com.jidouauto.eddie.mvpdemo.helper;

import android.content.Context;
import android.widget.Toast;

import com.jidouauto.eddie.mvpdemo.R;

public class ToastErrorView implements ErrorView {

    Toast toast;
    Context mContext;

    public ToastErrorView(Context context) {
        mContext = context;
    }

    @Override
    public void onNetworkError() {
        if (null != toast) {
            toast.cancel();
        }
        toast = Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onMessageError(String message) {
        if (null != toast) {
            toast.cancel();
        }
        toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
