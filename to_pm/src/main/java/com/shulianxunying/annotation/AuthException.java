package com.shulianxunying.annotation;

/**
 * Created by SuChang on 2017/5/26 10:14.
 */
public class AuthException extends Exception {

    private static final long serialVersionUID = 0L;

    public AuthException() {
        super("没有权限");
    }

    public AuthException(String message) {
        super(message);
    }
}
