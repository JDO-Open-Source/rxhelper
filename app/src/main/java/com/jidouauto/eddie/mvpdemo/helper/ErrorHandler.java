package com.jidouauto.eddie.mvpdemo.helper;

import com.jidouauto.eddie.mvpdemo.exception.MsgException;
import com.jidouauto.eddie.mvpdemo.exception.NetworkException;

public class ErrorHandler {

    public ErrorView mMessageView;

    public ErrorHandler(ErrorView messageView) {
        mMessageView = messageView;
    }


    public boolean handError(Throwable e) {
        return handError(e, null);
    }

    public boolean handError(Throwable e, String logicErrorMsg) {
        if (e instanceof MsgException) {
            mMessageView.onMessageError(e.getMessage());
            return true;
        } else if (e instanceof NetworkException) {
            mMessageView.onNetworkError();
            return true;
        }
        if (logicErrorMsg != null) {
            mMessageView.onMessageError(logicErrorMsg);
        }
        return false;
    }

}
