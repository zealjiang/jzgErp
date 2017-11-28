package com.jzg.erp.http;

import android.text.TextUtils;

import com.jzg.erp.app.JzgApp;
import com.jzg.erp.utils.LogUtil;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * <b>类名称：</b> CustomerOkHttpClient <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2016年03月08日 下午3:24<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
public class CustomerOkHttpClient {

    public static OkHttpClient client;

    private CustomerOkHttpClient() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static void create() {
        int maxCacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(JzgApp.getAppContext().getCacheDir(), maxCacheSize);
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);

                String cacheControl = request.cacheControl().toString();
                if (TextUtils.isEmpty(cacheControl)) {
                    cacheControl = "public, max-age=60 ,max-stale=2419200";
                }
                return response.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            }
        };

        // add your other interceptors …
        // add logging as last interceptor
        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(new LoggingInterceptor())
                .cache(cache)
                .build();
    }

    public static OkHttpClient getClient() {
        if (client == null) {
            create();
        }
        return client;
    }

    static class LoggingInterceptor implements Interceptor {
        private static final String TAG = "CustomerOkHttpClient";

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            try {
                Request request = chain.request();
                LogUtil.i(TAG, "request=" + request);
                long t1 = System.nanoTime();
                LogUtil.i(TAG, String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
                Response response = chain.proceed(request);
                if (response!=null) {
                    long t2 = System.nanoTime();
                    LogUtil.i(TAG, String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));
                    final String responseString = new String(response.body().bytes());
                    LogUtil.i(TAG, "Response: " + responseString);
                    return response.newBuilder().body(ResponseBody.create(response.body().contentType(), responseString)).build();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }


}
