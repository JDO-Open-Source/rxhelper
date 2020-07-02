package com.jidouauto.eddie.mvpdemo.bean;

import com.jidouauto.eddie.mvpdemo.exception.DataException;
import com.jidouauto.eddie.mvpdemo.utils.StringUtils;
import com.jidouauto.lib.rxhelper.Validator;
import com.jidouauto.eddie.mvpdemo.exception.BaseException;

public class UserInfo implements Validator {
    private String username;
    private int age;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "username='" + username + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public void validate() throws BaseException {
        if (StringUtils.isEmpty(username)) {
            throw new DataException(-1, "username is empty!");
        }
    }
}
