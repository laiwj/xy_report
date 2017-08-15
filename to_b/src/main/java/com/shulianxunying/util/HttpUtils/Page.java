package com.shulianxunying.util.HttpUtils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;

/**
 * Created by SuChang on 2017/2/13 15:25.
 */
public class Page {

//    private String html;

    private JSONObject json;

    private String rawText;

    private String url;

    private int statusCode;

    private Header[] headers;

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
