package com.jidouauto.eddie.mvpdemo.config;

import android.content.Context;

import com.jidouauto.eddie.mvpdemo.BuildConfig;
import com.jidouauto.eddie.mvpdemo.R;

public class HttpConfigImpl implements HttpConfig {

    private Context mContext;

    public HttpConfigImpl(Context context){
        mContext = context.getApplicationContext();
    }

    @Override
    public String language() {
        return mContext.getString(R.string.language);
    }

    @Override
    public String version() {
        return BuildConfig.VERSION_NAME;
    }
}
