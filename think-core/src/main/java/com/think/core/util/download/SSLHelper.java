package com.think.core.util.download;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLHelper {

    static SSLSocketFactory getSSLSocketFactory(){
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null,new TrustManager[0],new SecureRandom());
            return sslContext.getSocketFactory();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
