package com.jidouauto.eddie.mvpdemo.bean;

import com.jidouauto.lib.middleware.Validator;
import com.jidouauto.lib.middleware.exception.BaseException;

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
    public void validateResult() throws BaseException {

    }
}
