package com.haizhi.empower.util.okhttp;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.haizhi.empower.util.okhttp.ssl.TrustAllCerts;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author chenb
 * @Description EasyOKClient
 **/
@Slf4j
@Component
@Scope("prototype")
public class EasyOkClient {

    public final OkHttpClient httpClient;

    /**
     * 连接超时时间 单位秒(默认10s)
     */
    private static final int CONNECT_TIMEOUT = 10;
    /**
     * 写超时时间 单位秒(默认 0 , 不超时)
     */
    private static final int WRITE_TIMEOUT = 0;
    /**
     * 回复超时时间 单位秒(默认30s)
     */
    private static final int READ_TIMEOUT = 30;
    /**
     * 底层HTTP库所有的并发执行的请求数量
     */
    private static final int DISPATCHER_MAX_REQUESTS = 32;
    /**
     * 底层HTTP库对每个独立的Host进行并发请求的数量
     */
    private static final int DISPATCHER_MAX_REQUESTS_PER_HOST = 8;
    /**
     * 底层HTTP库中复用连接对象的最大空闲数量
     */
    private static final int CONNECTION_POOL_MAX_IDLE_COUNT = 16;
    /**
     * 底层HTTP库中复用连接对象的回收周期（单位分钟）
     */
    private static final int CONNECTION_POOL_MAX_IDLE_MINUTES = 5;

    /**
     * 初始化默认连接池
     */
    private static final ConnectionPool DEFAULT_CONNETCTION_POOL = new ConnectionPool(CONNECTION_POOL_MAX_IDLE_COUNT, CONNECTION_POOL_MAX_IDLE_MINUTES, TimeUnit.MINUTES);


    public static final String CONTENT_TYPE_KEY = "content-type";
    public static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
    public static final String CONTENT_TYPE_W3FORM = "application/x-www-form-urlencoded";


    /**
     * 构建一个默认配置的 HTTP Client 类
     */
    public EasyOkClient() {
        this(
                CONNECT_TIMEOUT,
                READ_TIMEOUT,
                WRITE_TIMEOUT,
                DISPATCHER_MAX_REQUESTS,
                DISPATCHER_MAX_REQUESTS_PER_HOST,
                DEFAULT_CONNETCTION_POOL
        );
    }

    /**
     * 构建一个自定义配置的 HTTP Client 类
     */
    public EasyOkClient(int connTimeout, int readTimeout, int writeTimeout, int dispatcherMaxRequests,
                        int dispatcherMaxRequestsPerHost, ConnectionPool connectionPool) {

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(dispatcherMaxRequests);
        dispatcher.setMaxRequestsPerHost(dispatcherMaxRequestsPerHost);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dispatcher(dispatcher);
        builder.connectionPool(connectionPool);
        builder.connectTimeout(connTimeout, TimeUnit.SECONDS);
        builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);

        // 配置https
        builder.sslSocketFactory(createSSLSocketFactory());
        builder.hostnameVerifier((s, sslSession) -> true);

        httpClient = builder
                .build();
    }

    /**
     * get
     */
    public <T> T get(String url, Map<String, Object> queryParams, Class<T> clazz) {
        return get(url, queryParams, Maps.newHashMap(), clazz);
    }

    /**
     * get
     */
    public <T> T get(String url, Map<String, Object> queryParams, Map<String, Object> headers, Class<T> clazz) {
        log.info("EasyOKClient请求地址：{}", url);
        log.info("EasyOKClient请求参数：{}", queryParams);
        log.info("EasyOKClient请求头：{}", headers);

        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        // 设置参数
        Optional.ofNullable(queryParams)
                .ifPresent(its -> its.forEach((key, value) -> httpBuilder.addQueryParameter(key, ObjectUtils.isEmpty(value) ? "" : value.toString())));
        // 创建请求
        Request.Builder requestBuilder = new Request.Builder().get().url(httpBuilder.build());

        return JSON.parseObject(send(requestBuilder, headers), clazz);
    }

    /**
     * post
     * content-type: application/json
     */
    public <T> T jsonPost(String url, Map<String, Object> params, Class<T> clazz) {
        return jsonPost(url, params, Maps.newHashMap(), clazz);
    }

    public <T> T jsonPost(String url, String body, Class<T> clazz) {
        HashMap<String, Object> headers = Maps.newHashMap();
        headers.put(CONTENT_TYPE_KEY, CONTENT_TYPE_JSON);
        return post(url, getRequestBody(body.getBytes(StandardCharsets.UTF_8), headers), headers, clazz);
    }

    public <T> T jsonPut(String url, String body, Class<T> clazz) {
        HashMap<String, Object> headers = Maps.newHashMap();
        headers.put(CONTENT_TYPE_KEY, CONTENT_TYPE_JSON);
        return put(url, getRequestBody(body.getBytes(StandardCharsets.UTF_8), headers), headers, clazz);
    }

    public Response jsonPut(String url, String body) {
        HashMap<String, Object> headers = Maps.newHashMap();
        headers.put(CONTENT_TYPE_KEY, CONTENT_TYPE_JSON);
        return put(url, getRequestBody(body.getBytes(StandardCharsets.UTF_8), headers), headers);
    }

    public <T> T jsonPost(String url, String body, Map<String, Object> headers, Class<T> clazz) {
        headers.put(CONTENT_TYPE_KEY, CONTENT_TYPE_JSON);
        return post(url, getRequestBody(body.getBytes(StandardCharsets.UTF_8), headers), headers, clazz);
    }

    public <T> T jsonPost(String url, Map<String, Object> params, Map<String, Object> headers, Class<T> clazz) {
        headers.put(CONTENT_TYPE_KEY, CONTENT_TYPE_JSON);
        log.info("post请求参数：{}", params);
        return post(url, getRequestBody(JSON.toJSONString(params).getBytes(StandardCharsets.UTF_8), headers), headers, clazz);
    }

    /**
     * post
     * content-type: application/x-www-form-urlencoded
     */
    public <T> T w3FormPost(String url, Map<String, Object> params,Map<String, Object> headers, Class<T> clazz) {
        headers.put(CONTENT_TYPE_KEY, CONTENT_TYPE_W3FORM);

        log.info("EasyOKClient请求地址：{}", url);
        log.info("EasyOKClient请求方式：{}", "w3FormPost");
        log.info("EasyOKClient请求参数：{}", params);
        log.info("EasyOKClient请求头：{}", headers);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            String value = String.valueOf(params.get(key));
            formBodyBuilder.add(key, value);
        }
        FormBody formBody = formBodyBuilder.build();
        Request.Builder request = new Request
                .Builder()
                .post(formBody)
                .url(url);
        return JSON.parseObject(send(request, headers), clazz);
    }


    private RequestBody getRequestBody(byte[] body, Map<String, Object> headers) {
        RequestBody rBody;
        if (body != null && body.length > 0) {
            MediaType mediaType = MediaType.parse(String.valueOf(headers.get(CONTENT_TYPE_KEY)));
            rBody = RequestBody.create(mediaType, body);
        } else {
            rBody = RequestBody.create(null, new byte[0]);
        }
        return rBody;
    }

    public <T> T post(String url, RequestBody body, Map<String, Object> headers, Class<T> clazz) {
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        Request.Builder requestBuilder = new Request.Builder().post(body).url(httpBuilder.build());
        return JSON.parseObject(send(requestBuilder, headers), clazz);
    }

    public <T> T put(String url, RequestBody body, Map<String, Object> headers, Class<T> clazz) {
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        Request.Builder requestBuilder = new Request.Builder().put(body).url(httpBuilder.build());
        return JSON.parseObject(send(requestBuilder, headers), clazz);
    }

    public Response put(String url, RequestBody body, Map<String, Object> headers) {
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        Request.Builder requestBuilder = new Request.Builder().put(body).url(httpBuilder.build());
        return sender(requestBuilder, headers);
    }

    public Response delete(String url) {
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        Request.Builder requestBuilder = new Request.Builder().delete().url(httpBuilder.build());
        return send(requestBuilder);
    }

    private String send(Request.Builder requestBuilder, Map<String, Object> headers) {
        log.info("EasyOKClient请求地址：{}");
        Response response;
        try {
            Optional.ofNullable(headers)
                    .ifPresent(its -> its.forEach((key, value) -> requestBuilder.header(key, value.toString())));
            response = httpClient.newCall(requestBuilder.build()).execute();
        } catch (Exception ex) {
            throw new RuntimeException("调用远程接口【" + requestBuilder.build().url().toString() + "】失败", ex);
        }
        try {
            assert response.body() != null;
            String responseStr = response.body().string();
            log.info("EasyOKClient请求返回值：{}", responseStr);
            return responseStr;
        } catch (Exception e) {
            throw new RuntimeException("请求接口【" + requestBuilder.build().url() + "】失败", e);
        }
    }

    private Response sender(Request.Builder requestBuilder, Map<String, Object> headers) {
        try {
            Optional.ofNullable(headers)
                    .ifPresent(its -> its.forEach((key, value) -> requestBuilder.header(key, value.toString())));
            return httpClient.newCall(requestBuilder.build()).execute();
        } catch (Exception ex) {
            throw new RuntimeException("调用远程接口【" + requestBuilder.build().url().toString() + "】失败", ex);
        }
    }

    private Response send(Request.Builder requestBuilder) {
        Response response = null;
        try {
            response = httpClient.newCall(requestBuilder.build()).execute();
        } catch (Exception ex) {
            throw new RuntimeException("调用远程接口【" + requestBuilder.build().url().toString() + "】失败", ex);
        }
        return response;
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    /**
     * 返回值不做处理的请求
     *
     * @param requestMethod GET、POST、PUT、DELETE、HEAD
     * @param url
     * @param queryParams
     * @param bodyParams
     * @param headers
     * @param contentType
     * @return
     */
    public String stringRequest(String requestMethod, String url, Map<String, Object> queryParams, Map<String, Object> bodyParams, Map<String, Object> headers, String contentType, String extraBody) {
        log.info("EasyOkClient#stringRequest=>url:{}", url);
        log.info("EasyOkClient#stringRequest==>url:{}", requestMethod);
        log.info("EasyOkClient#stringRequest===contentType:{}", contentType);
        log.info("EasyOkClient#stringRequest====>header:{}", headers);
        log.info("EasyOkClient#stringRequest=====>queryParams:{}", queryParams);
        log.info("EasyOkClient#stringRequest======>bodyParams:{}", bodyParams);
        log.info("EasyOkClient#stringRequest======>extraBody:{}", extraBody);

        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        // 设置query参数
        Optional.ofNullable(queryParams)
                .ifPresent(its -> its.forEach((key, value) -> httpBuilder.addQueryParameter(key, value.toString())));
        // 创建请求
        Request.Builder requestBuilder;
        if ("GET".equalsIgnoreCase(requestMethod)) {
            // get请求只处理query参数
            requestBuilder = new Request.Builder().get();
        } else {
            if (StringUtils.isEmpty(contentType) || contentType.contains("x-www-form-urlencoded")) {
                // 默认表单提交
                if (!StringUtils.isEmpty(contentType)) {
                    headers.put(CONTENT_TYPE_KEY, CONTENT_TYPE_W3FORM);
                }
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                Set<String> keySet = bodyParams.keySet();
                for (String key : keySet) {
                    String value = String.valueOf(bodyParams.get(key));
                    try {
                        formBodyBuilder.add(key, URLEncoder.encode(value, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        log.error("EasyOkClient#stringRequest URLEncoder.encode error!");
                    }
                }
                FormBody formBody = formBodyBuilder.build();
                requestBuilder = new Request.Builder().method(requestMethod.toUpperCase(), formBody);
            } else {
                // 其他的统一设置contentType， json
                if (!StringUtils.isEmpty(contentType)) {
                    headers.put(CONTENT_TYPE_KEY, CONTENT_TYPE_JSON);
                }
                MediaType mediaType = MediaType.parse("application/json");

                /*JSONObject parameters = new JSONObject();
                if (!StringUtils.isEmpty(extraBody)) {
                    parameters = new Gson().fromJson(extraBody, JSONObject.class);
                }*/
                String content = "";
                if (!StringUtils.isEmpty(extraBody)) {
                    content = extraBody;
                }
                RequestBody requestBody = RequestBody.create(mediaType, content);
//                RequestBody requestBody = getRequestBody(JSON.toJSONString(bodyParams).getBytes(StandardCharsets.UTF_8), headers);
                requestBuilder = new Request.Builder().method(requestMethod.toUpperCase(), requestBody);
            }
        }
        if ("head".equalsIgnoreCase(requestMethod)) {
            return sendWithHeadResp(requestBuilder.url(httpBuilder.build()), headers);
        } else {
            return send(requestBuilder.url(httpBuilder.build()), headers);
        }
    }

    /**
     * 只获取返回体的head部分
     *
     * @param requestBuilder
     * @param headers
     * @return
     */
    private String sendWithHeadResp(Request.Builder requestBuilder, Map<String, Object> headers) {
        log.info("EasyOKClient请求地址：{}");
        Response response;
        try {
            Optional.ofNullable(headers)
                    .ifPresent(its -> its.forEach((key, value) -> requestBuilder.header(key, value.toString())));
            response = httpClient.newCall(requestBuilder.build()).execute();
        } catch (Exception ex) {
            throw new RuntimeException("调用远程接口【" + requestBuilder.build().url().toString() + "】失败", ex);
        }
        try {
            Headers retHeaders = response.headers();
            String headStr = JSON.toJSONString(retHeaders);
            log.info("EasyOKClient请求返回Header：{}", headStr);
            return headStr;
        } catch (Exception e) {
            throw new RuntimeException("请求接口【" + requestBuilder.build().url() + "】失败", e);
        }
    }
}
