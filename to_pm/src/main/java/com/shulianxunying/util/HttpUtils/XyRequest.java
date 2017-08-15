package com.shulianxunying.util.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SuChang on 2017/2/13 16:26.
 */
public class XyRequest {

    private String url;

    private String method;

    private Map<String, String> params; // post方法的参数

    /**
     * Store additional information in extras.
     */
    private Map<String, Object> extras; // post方法的参数也可以放在 nameValuePair

    public XyRequest(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public XyRequest(String url, String method, Map<String, String> params) {
        this.url = url;
        this.method = method;
        this.params = params;
    }

    /**
     * add extra
     *
     * @param key
     * @param value
     */
    public void addExtra(String key, Object value) {
        if (extras == null) {
            extras = new HashMap<String, Object>();
        }
        extras.put(key, value);
    }

    public Object getExtra(String key) {
        if (extras == null) {
            return null;
        }
        return extras.get(key);
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }
}
