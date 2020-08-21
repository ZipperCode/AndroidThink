package com.think.core.net;

import android.text.TextUtils;

import com.think.core.util.LogUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;


public class HttpUtils {

    private static final String TAG = HttpUtils.class.getSimpleName();
    /**
     * 重试次数
     */
    private static final int RETRY_TIME = 3;

    public static String doGet(String url) {
        LogUtils.debug(TAG + " => doGet " + url);
        int tryTime = 1;
        do {
            try {
                HttpURLConnection httpURLConnection = HttpFactory.getHttpUrlConnect(url);
                httpURLConnection.setRequestMethod(HttpFactory.HTTP_METHOD.GET.name());
                long l1 = System.currentTimeMillis();
                httpURLConnection.connect();

                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    InputStream in = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder stringBuilder = new StringBuilder(in.available());
                    String line = null;
                    String separator = System.getProperty("line.separator");
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append(separator);
                    }
                    in.close();
                    bufferedReader.close();
                    String result = stringBuilder.toString();
                    LogUtils.debug("[" + TAG + "] ==> times : " + (System.currentTimeMillis() - l1) + " code : " + responseCode + "\t response :" + result);
                    if (TextUtils.isEmpty(result)) {
                        return "";
                    }
                    return result;
                } else {
                    LogUtils.debug("[" + TAG + "] ==> code : " + responseCode);
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                tryTime++;
            }
        } while (tryTime <= RETRY_TIME);
        return "";
    }

    public static String doPost(String url, Map<String, String> params) {
        return doPost(url, params, true);
    }

    /**
     * post请求方法，isRetry为是否重试
     *
     * @param url     请求的url
     * @param params  请求的参数，键值对都只能是字符串
     * @param isRetry 是否重试
     * @return 结果
     */
    public static String doPost(String url, Map<String, String> params, boolean isRetry) {
        LogUtils.debug(TAG + " => doPost " + url + " params => " + params);
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        StringBuilder paramStrig = new StringBuilder();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            try {
                paramStrig.append(URLEncoder.encode(next.getKey(), "UTF-8"))
                        .append("=").append(URLEncoder.encode(next.getValue(), "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        paramStrig.deleteCharAt(paramStrig.length() - 1);
        LogUtils.debug(TAG + " url = " + url + "?" + paramStrig.toString());

        int tryTime = 1;
        do {
            try {
                HttpURLConnection httpURLConnection = HttpFactory.getHttpUrlConnect(url);
                httpURLConnection.setRequestMethod(HttpFactory.HTTP_METHOD.POST.name());

                // 获取输出流
                DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
                // 将要传递的数据写入数据输出流,不要使用out.writeBytes(param); 否则中文时会出错
                out.write(paramStrig.toString().getBytes("UTF-8"));
                // 输出缓存
                out.flush();
                // 关闭数据输出流
                out.close();

                long l1 = System.currentTimeMillis();
                httpURLConnection.connect();

                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    InputStream in = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder stringBuilder = new StringBuilder(in.available());
                    String line = null;
                    String separator = System.getProperty("line.separator");
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append(separator);
                    }
                    in.close();
                    bufferedReader.close();
                    String result = stringBuilder.toString();
                    LogUtils.debug("[" + TAG + "] ==> times : " + (System.currentTimeMillis() - l1) + " code : " + responseCode + "\t response :" + result);
                    if (TextUtils.isEmpty(result)) {
                        return "";
                    }
                    return result;
                } else {
                    LogUtils.debug("[" + TAG + "] ==> code : " + responseCode);
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                tryTime++;
            }
        } while (tryTime <= RETRY_TIME && isRetry);
        return "";
    }
}
