package com.shulianxunying.decorator;

/**
 * Created by jiangwei on 2016/8/30 0030.
 */
public class RequestLimitException extends Exception{

    private static final long serialVersionUID = 1364225358754654702L;
    public RequestLimitException() {
        super("HTTP请求超出设定的限制");
    }
    public RequestLimitException(String message) {
        super(message);
    }
}
