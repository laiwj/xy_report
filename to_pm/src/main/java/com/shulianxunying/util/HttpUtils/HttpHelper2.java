package com.shulianxunying.util.HttpUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

/**
 * Created by Administrator on 2015/7/7.
 */
public class HttpHelper2 {
    private static final Logger logger = Logger.getLogger(HttpHelper2.class.getName());

    public static final String GET = "GET";
    public static final String HEAD = "HEAD";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String TRACE = "TRACE";
    public static final String CONNECT = "CONNECT";

    private RequestConf DEFAULT_REQUESTCONF;

    public HttpHelper2() {
        DEFAULT_REQUESTCONF = new RequestConf();
    }

    public HttpHelper2(boolean parseJson) {
        this.DEFAULT_REQUESTCONF = new RequestConf(5000, 5000, 5000, parseJson);
    }

    public HttpHelper2(RequestConf DEFAULT_REQUESTCONF) {
        this.DEFAULT_REQUESTCONF = DEFAULT_REQUESTCONF;
    }

    public HttpHelper2(int HTTP_CONNECTION_TIMEOUT, int HTTP_SOCKET_TIMEOUT, int HTTP_CONNECT_TIMEOUT, boolean parseJson) {
        DEFAULT_REQUESTCONF = new RequestConf(HTTP_CONNECTION_TIMEOUT, HTTP_SOCKET_TIMEOUT, HTTP_CONNECT_TIMEOUT, parseJson);
    }

    /**
     * 文件下载
     *
     * @param httpClient
     * @param url
     * @param file_name
     * @param headers
     * @return
     */
    public static File downloadFile(HttpClient httpClient, String url, String file_name, Map<String, String> headers) {
        HttpGet httpGet = new HttpGet(url);
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpGet.setHeader(header.getKey(), header.getValue());
            }
        }
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        HttpEntity httpEntity = response.getEntity();
        byte[] b = new byte[1024 * 1024];
        try {
            DataInputStream di = new DataInputStream(httpEntity.getContent());
            File f = new File(file_name);//tempPicFile
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            int j = 0;
            while ((j = di.read(b)) != -1) {
                fo.write(b, 0, j);
            }
            fo.flush();
            di.close();
            fo.close();
            httpGet.releaseConnection();
            return f;
        } catch (IOException e5) {
            e5.printStackTrace();
            httpGet.releaseConnection();
            return null;
        }
    }


    /**
     * 提交文件
     */
    public Page submitFile(HttpClient client, String url, String fileKey, File file, Map<String, String> paramMap, String charset, Map<String, String> headers, boolean isGzip) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpPost.setHeader(header.getKey(), header.getValue());
            }
        }
        FileBody bin = new FileBody(file);
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart(fileKey, bin);//file1为请求后台的File upload;属性
        if (paramMap != null) {
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue()));//普通参数;属性
            }
        }
        httpPost.setEntity(reqEntity);
        try {
            HttpResponse response = client.execute(httpPost);
            return handleResponse(url, charset, isGzip, response);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Page doGet(HttpClient httpClient, String url) {
        return get(httpClient, url, 0, 1, "uft-8", null, false, null);
    }

    public Page doGet(HttpClient httpClient, String url, Map<String, String> headers) {
        return get(httpClient, url, 0, 1, "uft-8", headers, false, null);
    }

    public Page doGet(HttpClient httpClient, String url, String charset, Map<String, String> headers) {
        return get(httpClient, url, 0, 1, charset, headers, false, null);
    }


    public Page doGet(HttpClient httpClient, String url, long delay, int maxTryCount, Map<String, String> headers) {
        return get(httpClient, url, delay, maxTryCount, "utf-8", headers, false, null);
    }

    public Page doGet(HttpClient httpClient, String url, long delay, int maxTryCount, String charset, Map<String, String> headers) {
        return get(httpClient, url, delay, maxTryCount, charset, headers, false, null);
    }

    public Page doGet(HttpClient httpClient, String url, long delay, int maxTryCount, String charset, Map<String, String> headers, boolean isGzip) {
        return get(httpClient, url, delay, maxTryCount, charset, headers, isGzip, null);
    }

    /**
     * get 的基础调用方法
     *
     * @param httpClient
     * @param url
     * @param delay
     * @param maxTryCount
     * @param charset
     * @param headers
     * @param isGzip
     * @param proxy
     * @return
     */
    private Page get(HttpClient httpClient, String url, long delay, int maxTryCount, String charset, Map<String, String> headers, boolean isGzip, HttpHost proxy) {
        // 组装 request
        HttpUriRequest httpUriRequest = getHttpUriRequest(new XyRequest(url, HttpHelper2.GET), null, headers, proxy);
        return excute(httpClient, httpUriRequest, url, delay, maxTryCount, charset, isGzip);
    }

    public Page doPost(HttpClient httpClient, String url, Map<String, String> params) {
        return post(httpClient, url, params, 0, 1, "utf-8", null, false, null);
    }

    public Page doPost(HttpClient httpClient, String url, Map<String, String> params, String charset, Map<String, String> headers) {
        return post(httpClient, url, params, 0, 1, charset, headers, false, null);
    }

    public Page doPost(HttpClient httpClient, String url, Map<String, String> params, Map<String, String> headers) {
        return post(httpClient, url, params, 0, 1, "utf-8", headers, false, null);
    }

    public Page doPost(HttpClient httpClient, String url, Map<String, String> params, long delay, int maxTryCount, Map<String, String> headers) {
        return post(httpClient, url, params, delay, maxTryCount, "utf-8", headers, false, null);
    }

    public Page doPost(HttpClient httpClient, String url, Map<String, String> params, long delay, int maxTryCount, String charset, Map<String, String> headers) {
        return post(httpClient, url, params, delay, maxTryCount, charset, headers, false, null);
    }

    public Page doPost(HttpClient httpClient, String url, Map<String, String> params, long delay, int maxTryCount, String charset, Map<String, String> headers, boolean isGzip) {
        return post(httpClient, url, params, delay, maxTryCount, charset, headers, isGzip, null);
    }

    /**
     * post的基础调用方法
     *
     * @param httpClient
     * @param url
     * @param params
     * @param delay
     * @param maxTryCount
     * @param charset
     * @param headers
     * @param isGzip
     * @param proxy
     * @return
     */
    private Page post(HttpClient httpClient, String url, Map<String, String> params, long delay, int maxTryCount, String charset, Map<String, String> headers, boolean isGzip, HttpHost proxy) {
        HttpUriRequest httpUriRequest = getHttpUriRequest(new XyRequest(url, HttpHelper2.POST, params), null, headers, proxy);
        return excute(httpClient, httpUriRequest, url, delay, maxTryCount, charset, isGzip);
    }


    /**
     * request处理方法，综合处理所有 get post put之类的
     *
     * @param httpClient
     * @param httpUriRequest
     * @param url
     * @param delay
     * @param maxTryCount
     * @param charset
     * @param isGzip
     * @return
     */
    private Page excute(HttpClient httpClient, HttpUriRequest httpUriRequest, String url, long delay, int maxTryCount, String charset, boolean isGzip) {
        HttpResponse httpResponse = null;
        do {
            try {
                httpResponse = httpClient.execute(httpUriRequest); //执行request
                return handleResponse(url, charset, isGzip, httpResponse);
            } catch (IOException e) {
                logger.warn("http执行失败，剩余重试次数" + maxTryCount);
                if (maxTryCount-- > 0) {
                    try {
                        Thread.sleep(delay);
                        continue;
                    } catch (InterruptedException e1) {
                        logger.warn("http执行睡眠失败");
                    }
                }
            } finally {
                try {
                    if(httpResponse != null)
                        EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException e) {
                    logger.warn("close response fail", e);
                }
            }
        } while (maxTryCount > 0 && httpResponse == null);
        return null;
    }


    /**
     * @param request     其中有 url 和 method
     * @param requestConf 超时相关设置
     * @param headers     headers设置
     * @param proxy       是否使用代理
     * @return 生成可使用的 request
     * @brief
     */
    protected HttpUriRequest getHttpUriRequest(XyRequest request, RequestConf requestConf, Map<String, String> headers, HttpHost proxy) {
        RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        if (requestConf == null) {
            requestConf = this.DEFAULT_REQUESTCONF;
        }
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectionRequestTimeout(requestConf.HTTP_CONNECTION_TIMEOUT)
                .setSocketTimeout(requestConf.HTTP_SOCKET_TIMEOUT)
                .setConnectTimeout(requestConf.HTTP_CONNECT_TIMEOUT)
                .setCookieSpec(CookieSpecs.BEST_MATCH);
        if (proxy != null) {
            requestConfigBuilder.setProxy(proxy);
            request.addExtra("proxy", proxy);
        }
        requestBuilder.setConfig(requestConfigBuilder.build());
        return requestBuilder.build();
    }

    /**
     * @param request
     * @return RequestBuilder
     * @brief 根据 method 生成对应的 RequestBuilder
     */
    protected RequestBuilder selectRequestMethod(XyRequest request) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase(GET)) {
            return RequestBuilder.get();
        } else if (method.equalsIgnoreCase(POST)) {
            RequestBuilder requestBuilder = RequestBuilder.post();
            if (request.getParams() != null) {
                for (Map.Entry<String, String> entry : request.getParams().entrySet()) {
                    requestBuilder.addParameter(entry.getKey(), entry.getValue());
                }
            }
            NameValuePair[] nameValuePair = (NameValuePair[]) request.getExtra("nameValuePair");
            if (nameValuePair != null && nameValuePair.length > 0) {
                requestBuilder.addParameters(nameValuePair);
            }
            return requestBuilder;
        } else if (method.equalsIgnoreCase(HEAD)) {
            return RequestBuilder.head();
        } else if (method.equalsIgnoreCase(PUT)) {
            return RequestBuilder.put();
        } else if (method.equalsIgnoreCase(DELETE)) {
            return RequestBuilder.delete();
        } else if (method.equalsIgnoreCase(TRACE)) {
            return RequestBuilder.trace();
        }
        throw new IllegalArgumentException("Illegal HTTP Method " + method);
    }


    /**
     * 获取网页文本内容
     * 如果为302的response 则返回 location
     *
     * @param response
     * @param charset  为null则自动查找response返回的编码
     * @return
     */
    public static String getHtml(HttpResponse response, String charset, boolean isGzip) {
        if (response == null)
            return null;
        if (response.getStatusLine().getStatusCode() == 302) {
            Header location = response.getFirstHeader("Location");
            if (location == null)
                location = response.getFirstHeader("location");
            if (response == null)
                return null;
            return location.getValue();
        }
        if (charset == null) {
            String content_type;
            try {
                content_type = response.getFirstHeader("Content-Type").getValue();       //  text/html; charset=utf-8
                String[] content_type_array = content_type.split(";");
                for (String type : content_type_array) {
                    if (type.toLowerCase().contains("charset")) {
                        type = type.toLowerCase().trim();
                        charset = type.substring(type.indexOf("charset") + 8);
                    }
                }
            } catch (NullPointerException e) {
            }
            if (charset == null)
                charset = "UTF-8";
        }
        try {
            HttpEntity httpEntity = response.getEntity();
            InputStream is;
            if (isGzip) {
                try {
                    is = new GZIPInputStream(httpEntity.getContent());
                } catch (ZipException e) {
                    is = httpEntity.getContent();
                }
                if (charset == null || "".equals(charset)) {
                    return org.apache.commons.io.IOUtils.toString(is);
                }
                return org.apache.commons.io.IOUtils.toString(is, charset);
            } else {
                return EntityUtils.toString(httpEntity);
            }
        } catch (ConnectionClosedException e) {
            logger.error(e.getMessage(), e);
        } catch (ConnectTimeoutException | SocketTimeoutException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param url          内容来自url
     * @param charset      字符编码
     * @param isGzip       是否压缩
     * @param httpResponse
     * @return
     * @throws IOException
     * @brief 处理 response返回，获取字符内容
     */
    protected Page handleResponse(String url, String charset, boolean isGzip, HttpResponse httpResponse) throws IOException {
        String content = getHtml(httpResponse, charset, isGzip);
        Page page = new Page();
        page.setRawText(content);
        if (this.DEFAULT_REQUESTCONF.isParseJson()) {
            try {
                page.setJson(JSONObject.parseObject(content));
            }catch (JSONException e){

            }
        }
        page.setUrl(url);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        page.setHeaders(httpResponse.getAllHeaders());
        return page;
    }


}
