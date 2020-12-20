package com.think.core.net.myokhttp;

import com.think.core.net.myokhttp.body.Request;
import com.think.core.net.myokhttp.interceptor.Interceptor;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public final class HttpClient {

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
    private final static HostnameVerifier HOSTNAME_VERIFIER = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            // TODO 主机名不做校验
            return true;
        }
    };

    /** 默认的http连接池的初始容量*/
    public static final int DEFAULT_CONNECTION_POOL_CAPABILITY = 64;
    /** 默认的http连接池的大小 */
    public static final int DEFAULT_CONNECTION_POOL_LIMIT = 64;
    /** 连接超时时间 */
    private static final int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;
    /** 读取数据超时时间 */
    private static final int DEFAULT_READ_TIMEOUT = 10 * 1000;
    /**
     * 分发器
     */
    final Dispatcher dispatcher;
    /**
     * 自定义的拦截器链
     */
    final List<Interceptor> interceptorList;

    final HostnameVerifier hostnameVerifier;

    final X509TrustManager x509TrustManager;
    /**
     * http 连接池
     */
    final ConnectionPool connectionPool;

    final int connectTimeoutMillis;

    final int readTimeoutMillis;

    final boolean usedCache;

    public HttpClient() {
        this(new Builder());
    }

    public HttpClient(Builder builder) {
        this.dispatcher = builder.dispatcher;
        this.interceptorList = builder.interceptorList;
        if (builder.hostnameVerifier == null) {
            this.hostnameVerifier = HOSTNAME_VERIFIER;
        } else {
            this.hostnameVerifier = builder.hostnameVerifier;
        }
        if (builder.x509TrustManager == null) {
            this.x509TrustManager = TRUST_MANAGER;
        } else {
            this.x509TrustManager = builder.x509TrustManager;
        }
        this.connectionPool = builder.connectionPool;
        this.readTimeoutMillis = builder.readTimeoutMillis;
        this.connectTimeoutMillis = builder.connectTimeoutMillis;
        this.usedCache = builder.usedCache;
    }

    public Call newCall(Request request) {
        return new RealCall(this, request);
    }

    public Dispatcher dispatcher() {
        return this.dispatcher;
    }

    public List<Interceptor> interceptors() {
        return this.interceptorList;
    }


    public X509TrustManager x509TrustManager() {
        return this.x509TrustManager;
    }

    public HostnameVerifier hostnameVerifier() {
        return this.hostnameVerifier;
    }

    public ConnectionPool connectionPool() {
        return this.connectionPool;
    }

    public int connectTimeoutMillis(){
        return connectTimeoutMillis;
    }

    public boolean usedCache(){
        return usedCache;
    }

    public int readTimeoutMillis(){
        return readTimeoutMillis;
    }

    public static final class Builder {

        Dispatcher dispatcher;

        List<Interceptor> interceptorList;

        HostnameVerifier hostnameVerifier;

        X509TrustManager x509TrustManager;

        ConnectionPool connectionPool;

        int connectTimeoutMillis;

        int readTimeoutMillis;

        boolean usedCache;

        public Builder() {
            this.dispatcher = new Dispatcher();
            this.interceptorList = new ArrayList<>();
            this.connectionPool = new ConnectionPool(DEFAULT_CONNECTION_POOL_CAPABILITY, DEFAULT_CONNECTION_POOL_LIMIT);
            this.hostnameVerifier = HOSTNAME_VERIFIER;
            this.x509TrustManager = TRUST_MANAGER;
            this.connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT;
            this.readTimeoutMillis = DEFAULT_READ_TIMEOUT;
        }

        public Builder interceptor(Interceptor interceptor) {
            this.interceptorList.add(interceptor);
            return this;
        }

        public Builder dispatcher(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
            return this;
        }

        public Builder x509TrustManager(X509TrustManager x509TrustManager) {
            this.x509TrustManager = x509TrustManager;
            return this;
        }

        public Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        public Builder connectionPool(ConnectionPool connectionPool) {
            this.connectionPool = connectionPool;
            return this;
        }

        public Builder connectTimeoutMillis(int connectTimeoutMillis){
            this.connectTimeoutMillis = connectTimeoutMillis;
            return this;
        }

        public Builder readTimeoutMillis(int readTimeoutMillis){
            this.readTimeoutMillis = readTimeoutMillis;
            return this;
        }

        public Builder useCache(boolean usedCache){
            this.usedCache = usedCache;
            return this;
        }

        public HttpClient build() {
            return new HttpClient(this);
        }
    }
}
