package com.jidouauto.lib.middleware.network;

import android.content.Context;

import com.jidouauto.lib.middleware.network.persistentcookiejar.PersistentCookieJar;
import com.jidouauto.lib.middleware.network.persistentcookiejar.cache.SetCookieCache;
import com.jidouauto.lib.middleware.network.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * The type Retrofit client.
 *
 * @author eddie Retrofit和OkHttpClient的包装类,用于存储Retrofit实例以及对应的OkHttpClient实例
 */
public class RetrofitClient {

    /**
     * The Retrofit.
     */
    Retrofit retrofit;

    /**
     * The Ok http client.
     */
    OkHttpClient okHttpClient;

    /**
     * 通过{@link OkHttpClient.Builder}和{@link Retrofit.Builder}构造一个RetrofitClient实例
     *
     * @param okHttpBuilder   {@link RetrofitClient#newDefaultOkHttpBuilder(CookieJar, HttpLoggingInterceptor.Level)}
     * @param retrofitBuilder {@link RetrofitClient#newDefaultRetrofitBuilder()} ()}
     */
    public RetrofitClient(OkHttpClient.Builder okHttpBuilder, Retrofit.Builder retrofitBuilder) {
        okHttpClient = okHttpBuilder.build();
        retrofitBuilder.client(okHttpClient);
        retrofit = retrofitBuilder.build();
    }


    /**
     * 获取默认配置的RetrofitClient
     *
     * @param baseUrl HOST
     * @param level   {@link HttpLoggingInterceptor.Level}
     */
    public RetrofitClient(String baseUrl, HttpLoggingInterceptor.Level level) {
        this(newDefaultOkHttpBuilder(null, level), newDefaultRetrofitBuilder().baseUrl(baseUrl));
    }

    /**
     * 获取默认配置的RetrofitClient，包含CookieJar
     *
     * @param baseUrl   HOST
     * @param cookieJar {@link RetrofitClient#createSharedPrefsCookieJar(Context)}
     * @param level     {@link HttpLoggingInterceptor.Level}
     */
    public RetrofitClient(String baseUrl, CookieJar cookieJar, HttpLoggingInterceptor.Level level) {
        this(newDefaultOkHttpBuilder(cookieJar, level), newDefaultRetrofitBuilder().baseUrl(baseUrl));
    }

    /**
     * Gets retrofit.
     *
     * @return the retrofit
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * Sets retrofit.
     *
     * @param retrofit the retrofit
     */
    public void setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    /**
     * Gets ok http client.
     *
     * @return the ok http client
     */
    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * Sets ok http client.
     *
     * @param okHttpClient the ok http client
     */
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    /**
     * Create shared prefs cookie jar cookie jar.
     *
     * @param context the context
     * @return the cookie jar
     */
    public static CookieJar createSharedPrefsCookieJar(Context context) {
        SharedPrefsCookiePersistor spcp = new SharedPrefsCookiePersistor(context);
        CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), spcp);
        return cookieJar;
    }

    /**
     * New default ok http builder ok http client . builder.
     *
     * @param cookieJar the cookie jar
     * @param level     the level
     * @return the ok http client . builder
     */
    public static OkHttpClient.Builder newDefaultOkHttpBuilder(CookieJar cookieJar, HttpLoggingInterceptor.Level level) {
        int connectionTimeout = 10 * 1000;
        int readTimeout = 10 * 1000;
        int writeTimeout = 10 * 1000;

        HttpLoggingInterceptor mHttpLoggingInterceptor = new HttpLoggingInterceptor();
        mHttpLoggingInterceptor.setLevel(level);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
        builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        builder.addInterceptor(mHttpLoggingInterceptor);
        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
        }
        builder.sslSocketFactory(createSSLSocketFactory(), new HttpsTrustManager());
        builder.hostnameVerifier(new TrustAllHostnameVerifier());
        return builder;
    }

    /**
     * New default retrofit builder retrofit . builder.
     *
     * @return the retrofit . builder
     */
    public static Retrofit.Builder newDefaultRetrofitBuilder() {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.addConverterFactory(ScalarsConverterFactory.create());
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return retrofitBuilder;
    }

    /**
     * Create ssl socket factory ssl socket factory.
     *
     * @return the ssl socket factory
     */
    public static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new HttpsTrustManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return sSLSocketFactory;
    }

    /**
     * The type Https trust manager.
     */
    public static class HttpsTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * The type Trust all hostname verifier.
     */
    public static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }

    /**
     * Create t.
     *
     * @param <T>     the type parameter
     * @param service the service
     * @return t t
     * @see {@link Retrofit#create(Class)}
     */
    public <T> T create(final Class<T> service) {
        return retrofit.create(service);
    }

}


