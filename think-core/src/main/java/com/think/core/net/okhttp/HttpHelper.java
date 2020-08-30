package com.think.core.net.okhttp;


import android.os.Handler;
import android.os.Looper;

import com.think.core.util.FileUtils;
import com.think.core.util.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpHelper {

    /**
     * 缓存文件名称
     */
    private static final String CACHE_FILE = FileUtils.CACHE_ROOT_PATH + File.separator + "cache";
    /**
     * 临时存储目录
     */
    private static final String TEMP_DIR = FileUtils.DATA_PATH + File.separator + "temp";
    /**
     * 默认缓存大小、超时时间（秒）
     */
    private static final int CACHE_SIZE = 100 * 1024 * 1024;
    private static final int CONNECT_TIMEOUT_SIZE = 10;
    private static final int WRITE_TIMEOUT_SIZE = 10;
    private static final int READ_TIMEOUT_SIZE = 30;
    /**
     * 下载文件缓冲区的大小
     */
    private static final int DOWNLOAD_BUFFER_SIZE = 10 * 1024;

    private final OkHttpClient CLIENT;

    private final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 内部类实现单例，实现线程安全，延迟加载
     */
    private static class InnerObject {
        private final static HttpHelper INSTANCE = new HttpHelper();
    }

    public static HttpHelper getInstance() {
        return InnerObject.INSTANCE;
    }

    private HttpHelper() {
        // 配置缓存目录
        File cacheDirectory = new File(CACHE_FILE);
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs();
        }
        // 配置http请求缓存
        Cache cache = new Cache(cacheDirectory, CACHE_SIZE);
        CLIENT = new OkHttpClient.Builder()
                //设置连接超时
                .connectTimeout(CONNECT_TIMEOUT_SIZE, TimeUnit.SECONDS)
                //设置写超时
                .writeTimeout(WRITE_TIMEOUT_SIZE, TimeUnit.SECONDS)
                //设置读超时
                .readTimeout(READ_TIMEOUT_SIZE, TimeUnit.SECONDS)
                //是否自动重连
                .retryOnConnectionFailure(true)
                // 添加响应缓存
                .cache(cache)
                // 不验证证书
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                })
                // 请求、响应日志拦截器
                .addInterceptor(new LogInterceptor())
                .build();
    }

    public void request(final RequestWrapper requestWrapper, final CallBack callBack) {
        CLIENT.newCall(requestWrapper.request()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // 异常信息
                onError(requestWrapper.isRunMainThread(), e, callBack);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        onSuccess(requestWrapper, response, callBack);
                    } catch (IOException e) {
                        onError(requestWrapper.isRunMainThread(), e, callBack);
                    }
                } else {
                    String text = response.toString();
                    HttpHelper.this.onFailure(requestWrapper.isRunMainThread(), text, callBack);
                }
                response.close();
            }
        });
    }


    /**
     * 请求响应成功
     *
     * @param requestWrapper 请求包装类
     * @param response       OkHttp 响应 {@link Response}
     * @param callback       回调接口
     * @throws IOException 异常信息
     */
    private void onSuccess(RequestWrapper requestWrapper,
                           Response response,
                           final CallBack callback) throws IOException {
        ResponseBody responseBody = response.body();
        ResponseWrapper.Builder builder = new ResponseWrapper.Builder();
        if (responseBody != null && responseBody.contentType() != null) {
            String type = responseBody.contentType().type();
            String subType = responseBody.contentType().subtype();
            switch (type) {
                case ContentType.APPLICATION:
                    // JSON 数据
                    if (ContentType.JSON.equals(subType)) {
                        builder.responseType(requestWrapper.responseType())
                                .responseText(responseBody.string());
                    }
//                    else if (ContentType.STREAM.equals(subType)) {
//
//                    }
                    // 配置响应的类型为流类型，标识文件下载

                    try {
                        download(callback, requestWrapper, responseBody, builder);
                    } catch (Exception e) {
                        onError(requestWrapper.isRunMainThread(), e, callback);
                    }
                    break;
                case ContentType.TEXT:
                default:
                    builder.responseType(String.class)
                            .responseText(responseBody.string());
                    try {
                        callback.success(builder.build());
                    } catch (Exception e) {
                        onError(requestWrapper.isRunMainThread(), e, callback);
                    }
            }
        } else {
            // 非正常响应直接响应message
            builder.responseType(String.class)
                    .responseText(response.message());
            try {
                callback.success(builder.build());
            } catch (Exception e) {
                onError(requestWrapper.isRunMainThread(), e, callback);
            }
        }
    }

    private void onFailure(String message, CallBack callback) {
        callback.failure(message);
    }

    private void onFailure(boolean isRunMain, final String message, final CallBack callback) {
        if (isRunMain) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.failure(message);
                }
            });
        } else {
            callback.failure(message);
        }
    }

    private void onError(Exception e, CallBack callback) {
        callback.onError(new NetworkException(NetworkException.ExceptionCode.EXCEPTION));
    }

    private void onError(boolean isRunMain, Exception e, final CallBack callback) {
        if (isRunMain) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(new NetworkException(NetworkException.ExceptionCode.EXCEPTION));
                }
            });
        } else {
            callback.onError(new NetworkException(NetworkException.ExceptionCode.EXCEPTION));
        }
    }

    /**
     * 下载处理，此时success中的回调信息返回空的实体包装，<br/>
     * 若在success回调中出现异常则触发failure回调。<br/>
     * 如果需要在下载中接受下载进度，可以在ResponseWrapper中<br/>
     * 实现{@link ProgressCallback}接口用于接收下载进度
     *
     * @param callBack       回调函数
     * @param requestWrapper 请求包装类
     * @param responseBody   请求响应实体
     * @param builder        请求响应包装构造类
     * @throws IOException 出现的异常信息
     */
    private void download(
            final CallBack callBack,
            RequestWrapper requestWrapper,
            ResponseBody responseBody,
            ResponseWrapper.Builder builder) throws IOException {
        builder
                .contentType(ContentType.STREAM)
                .progressCallback(requestWrapper.progressCallback());
        ProgressCallback progressCallback = requestWrapper.progressCallback();
        // 请求包装类中提供的下载目录
        String downloadDir = requestWrapper.downloadDir();
        File downloadDirFile = new File(downloadDir);
        if (!downloadDirFile.exists()) {
            downloadDirFile.mkdirs();
        }
        // 重命名下载文件，FileUtils会判断本地文件自动重命名文件
        String fileName = FileUtils.downloadRename(downloadDir
                + File.separator + requestWrapper.downloadFileName());
        // 请求包装类中提供的下载文件名，默认url截取值,文件存在会重命名
        File downloadFileName = new File(fileName);
        if (!downloadFileName.exists()) {
            downloadFileName.createNewFile();
        }
        // 生成response包装
        final ResponseWrapper responseWrapper = builder
                .downloadLocalFileName(fileName)
                .responseText(fileName)
                .build();
        // 总大小
        long totalSize = responseBody.contentLength();
        // 已经下载的大小
        long downloadSize = 0;
        // 读取的长度
        int readLength = 0;
        // 读取缓冲区
        byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
        try (FileOutputStream fileOutputStream = new FileOutputStream(downloadFileName);
             InputStream inputStream = responseBody.byteStream()) {
            while ((readLength = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, readLength);
                downloadSize += readLength;
                // 计算百分比
                // int percent = (int) ((downloadSize * 1.0f / totalSize) * 100);
                if (progressCallback != null) {
                    progressCallback.progress(downloadSize, totalSize);
                }
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            throw e;
        }
        if (requestWrapper.isRunMainThread()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        callBack.success(responseWrapper);
                    } catch (Exception e) {
                        onError(e, callBack);
                    }
                }
            });
        } else {
            try {
                callBack.success(responseWrapper);
            } catch (Exception e) {
                onError(e, callBack);
            }
        }
    }

    public void get(RequestWrapper requestWrapper, CallBack callBack) {
        request(requestWrapper, callBack);
    }

    public void post(RequestWrapper requestWrapper, CallBack callBack) {
        request(requestWrapper, callBack);
    }

    public void download(RequestWrapper requestWrapper, CallBack callBack) {
        request(requestWrapper, callBack);
    }


    /**
     * 对外公开的手动释放内存线程池方法，在内存不足时可调用
     */
    public void closeThreadPools() {
        CLIENT.dispatcher().executorService().shutdown();   //清除并关闭线程池
        CLIENT.connectionPool().evictAll();                 //清除并关闭连接池
        try {
            if (CLIENT.cache() != null) {
                CLIENT.cache().close();                             //清除cache
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对外公开的取消当前请求
     */
    public void cancelRequest(Call call) {
        if (call.isCanceled()) {
            call.cancel();  //取消请求
        }
    }

    /**
     * 构建请求对象
     *
     * @param url
     * @param params
     * @param type
     * @return
     */
    private Request buildRequest(String url, Map<String, Object> params, HttpMethodType type) {
        Request.Builder builder = new Request.Builder();
        builder.url(url)
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Content-Type", "application/x-www-form-urlencoded");
        if (type == HttpMethodType.GET) {
            builder.get();
        } else if (type == HttpMethodType.POST) {
            RequestBody body = buildRequestBody(params);
            builder.post(body);
            try {
                long length = body.contentLength();
                builder.addHeader("Content-Length", length + "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.build();
    }

    /**
     * 通过Map的键值对构建post请求对象的body
     *
     * @param params
     * @return
     */
    private RequestBody buildRequestBody(final Map<String, Object> params) {
        FormBody.Builder builder = new FormBody.Builder(); //form表达提交
        Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            String key = next.getKey();
            Object value = next.getValue();
            builder.add(key, value + "");
        }
        return builder.build();
    }

    /**
     * 这个枚举用于指明是哪一种提交方式
     */
    public enum HttpMethodType {
        GET,
        POST
    }


    public static void main(String[] args) {
        RequestWrapper requestWrapper = new RequestWrapper.Builder()
                .url("https://dl.cdxdyg.cn/client-download/Clash-Windows.7z")
                .get()
                .build();
        HttpHelper.getInstance().download(requestWrapper, new CallBack() {
            @Override
            public void success(ResponseWrapper t) throws Exception {
                System.out.println("download file = " + t.downloadLocalFileName());
            }

            @Override
            public void failure(String message) {
                System.out.println();
            }

            @Override
            public void onError(NetworkException e) {
                System.err.println();
            }
        });
    }
}
