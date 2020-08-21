package com.think.core.net;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public final class HttpFactory {
    public static final int DEFAULT_TIMEOUT = 15000;

    private final static X509TrustManager TRUST_MANAGER = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // TODO 不检查
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // TODO 不检查
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    private final static HostnameVerifier HOSTNAME_VERIFER = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                // TODO 主机名不做校验
                return true;
            }
        };


    public static HttpURLConnection getHttpUrlConnect(String url) throws Exception {
        URL httpUrl = new URL(url);
        HttpURLConnection httpURLConnection = null;
        if("https".equalsIgnoreCase(httpUrl.getProtocol())){
            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(null,new TrustManager[]{TRUST_MANAGER},new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(ssl.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(HOSTNAME_VERIFER);
            httpURLConnection = (HttpsURLConnection) httpUrl.openConnection();
        }else{
            httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
        }
        // 设置连接和读取超时
        httpURLConnection.setConnectTimeout(DEFAULT_TIMEOUT);
        httpURLConnection.setReadTimeout(DEFAULT_TIMEOUT);
        // 设置输入输出
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        // 是否使用缓存
        httpURLConnection.setUseCaches(false);
        // 是否允许重定向
        httpURLConnection.setInstanceFollowRedirects(true);
        httpURLConnection.setRequestProperty("Charset", "UTF-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        return httpURLConnection;
    }

    public enum HTTP_METHOD{
        GET,POST
    }

}
