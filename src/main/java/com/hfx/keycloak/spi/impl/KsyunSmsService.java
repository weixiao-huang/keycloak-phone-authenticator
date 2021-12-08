package com.hfx.keycloak.spi.impl;

import com.hfx.keycloak.SmsException;
import com.hfx.keycloak.VerificationCodeRepresentation;
import com.hfx.keycloak.spi.SmsService;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.keycloak.models.KeycloakSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class KsyunSmsService implements SmsService<Object> {
    private final KeycloakSession session;

    private static String AUTH_ACCESS_KEY_ENV = "AUTH_ACCESS_KEY";
    private static String AUTH_SECRET_KEY_ENV = "AUTH_SECRET_KEY";
    private static String VERIFICATION_ENDPOINT_ENV = "VERIFICATION_ENDPOINT";

    String ak = "";
    String sk = "";
    String signName = "";
    String ExtId = "dadsdasd";
    //开启了权限，才能使用下面这两个参数
    String content = "";
    String smsType = "";

    public KsyunSmsService(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean send(String phoneNumber, Map<String, ? super Object> params) throws SmsException {
        return true;
    }

    @Override
    public boolean sendVerificationCode(VerificationCodeRepresentation rep, Map<String, ? super Object> params) throws SmsException {
        String templateId = System.getProperty("template_id");
        List<String> extraData = new ArrayList<>();
        extraData.add(rep.getCode());
        params.put("datas", extraData);

        String accessKey = System.getenv(AUTH_ACCESS_KEY_ENV);
        String secretKey = System.getenv(AUTH_SECRET_KEY_ENV);
        String url = System.getenv(VERIFICATION_ENDPOINT_ENV);
        sendCode(url, accessKey, secretKey, rep.getPhoneNumber(), rep.getCode());
        return true;
    }

    public static String sendCode(String url, String accessKey, String secretKey, String phone, String code) {
        CloseableHttpResponse httpResponse = null;
        String result = "";
        SSLContextBuilder builder = new SSLContextBuilder();
        // 创建httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());
            httpClient = HttpClients.custom().setSSLSocketFactory(
                    sslsf).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        // httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Content-Type", "application/json");

        // Set basic auth
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(accessKey, secretKey);
        try {
            Header header = new BasicScheme(StandardCharsets.UTF_8).authenticate(creds , httpPost, null);
            httpPost.addHeader(header);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

//        JSONObject json = new JSONObject();
//        json.put("code", code);
//        json.put("phone", phone);
//        json.put("purpose", "login");
        // 封装post请求参数
        StringEntity stringEntity = new StringEntity(String.format("{\"code\": \"%s\", \"phone\": \"%s\", \"purpose\": \"login\"}", code, phone), "UTF-8");
        httpPost.setEntity(stringEntity);
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
