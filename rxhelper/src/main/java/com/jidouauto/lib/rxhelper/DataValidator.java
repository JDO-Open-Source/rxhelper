package com.jidouauto.lib.rxhelper;

public interface DataValidator<T, R extends Exception> {

    void validate(T data) throws R;

}
