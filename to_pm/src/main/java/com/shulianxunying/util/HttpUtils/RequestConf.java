package com.shulianxunying.util.HttpUtils;

/**
 * Created by SuChang on 2017/2/13 16:47.
 * request 配置
 */
public class RequestConf {

    public int HTTP_CONNECTION_TIMEOUT = 1000 * 5;
    public int HTTP_SOCKET_TIMEOUT = 1000 * 5;
    public int HTTP_CONNECT_TIMEOUT = 1000 * 5;
    public boolean parseJson = false;

    public RequestConf() {
    }

    public RequestConf(int HTTP_CONNECTION_TIMEOUT, int HTTP_SOCKET_TIMEOUT, int HTTP_CONNECT_TIMEOUT, boolean parseJson) {
        this.HTTP_CONNECTION_TIMEOUT = HTTP_CONNECTION_TIMEOUT;
        this.HTTP_SOCKET_TIMEOUT = HTTP_SOCKET_TIMEOUT;
        this.HTTP_CONNECT_TIMEOUT = HTTP_CONNECT_TIMEOUT;
        this.parseJson = parseJson;
    }

    public boolean isParseJson() {
        return parseJson;
    }

    public void setParseJson(boolean parseJson) {
        this.parseJson = parseJson;
    }

    public int getHTTP_CONNECTION_TIMEOUT() {
        return HTTP_CONNECTION_TIMEOUT;
    }

    public RequestConf setHTTP_CONNECTION_TIMEOUT(int HTTP_CONNECTION_TIMEOUT) {
        this.HTTP_CONNECTION_TIMEOUT = HTTP_CONNECTION_TIMEOUT;
        return this;
    }

    public int getHTTP_SOCKET_TIMEOUT() {
        return HTTP_SOCKET_TIMEOUT;
    }

    public RequestConf setHTTP_SOCKET_TIMEOUT(int HTTP_SOCKET_TIMEOUT) {
        this.HTTP_SOCKET_TIMEOUT = HTTP_SOCKET_TIMEOUT;
        return this;
    }

    public int getHTTP_CONNECT_TIMEOUT() {
        return HTTP_CONNECT_TIMEOUT;
    }

    public RequestConf setHTTP_CONNECT_TIMEOUT(int HTTP_CONNECT_TIMEOUT) {
        this.HTTP_CONNECT_TIMEOUT = HTTP_CONNECT_TIMEOUT;
        return this;
    }
}
