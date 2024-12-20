package org.ast.astral4xclient.util;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class OkHttp3 {
    private final OkHttpClient client;

    public OkHttp3() {
        this.client = new OkHttpClient();
    }

    // 发送通用请求
    public String sendRequest(String url, String method, Map<String, String> headers, String requestBody) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url);

        // 添加请求头
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // 根据请求方法设置请求体
        if ("POST".equalsIgnoreCase(method)) {
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(requestBody, JSON);
            requestBuilder.post(body);
        } else if ("PUT".equalsIgnoreCase(method)) {
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(requestBody, JSON);
            requestBuilder.put(body);
        } else if ("DELETE".equalsIgnoreCase(method)) {
            requestBuilder.delete(RequestBody.create(requestBody, MediaType.get("text/plain")));
        } else {
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            if (!(requestBody == null)) {
                RequestBody body = RequestBody.create(requestBody, JSON);
                requestBuilder.method(method, body); // 其他方法如 GET, HEAD 等

            }else {
                requestBuilder.method(method, null); // 其他方法如 GET, HEAD 等

            }
        }

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    // 发送GET请求
    public String sendGetRequest(String url) throws IOException {
        return sendRequest(url, "GET", null, null);
    }

    // 发送POST请求
    public String sendPostRequest(String url, String json) throws IOException {
        return sendRequest(url, "POST", null, json);
    }
}
