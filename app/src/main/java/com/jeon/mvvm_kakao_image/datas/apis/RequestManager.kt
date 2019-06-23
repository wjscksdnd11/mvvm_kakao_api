package com.jeon.mvvm_kakao_image.datas.apis

import com.jeon.mvvm_kakao_image.BuildConfig
import okhttp3.Interceptor
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


object RequestManager {

    private val ALL_TIMEOUT = 10L
    private val API_KEY = BuildConfig.KAKAO_API_KEY
    private val KAKAO_REST_API = "https://dapi.kakao.com/"

    private var okHttpClient: OkHttpClient
    private var retrofit: Retrofit

    init {
        /*
         * 로깅 인터셉터 연결
         */
        val httpLogging = HttpLoggingInterceptor()
        httpLogging.level = HttpLoggingInterceptor.Level.BASIC


        okHttpClient = OkHttpClient().newBuilder().apply {

            addInterceptor(httpLogging)
            addInterceptor(HeaderSettingInterceptor())
            connectTimeout(ALL_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(ALL_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(ALL_TIMEOUT, TimeUnit.SECONDS)

        }.build()
        /*
         * Retrofit2 + OKHttp3를 연결한다
         */
        retrofit = Retrofit.Builder().apply {
            baseUrl(KAKAO_REST_API)
            client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            addConverterFactory(GsonConverterFactory.create())//gson을 이용해 json파싱
        }.build()
    }

    /*
     *  Request Header를 세팅하는 Interceptor
     */
    private class HeaderSettingInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {

            val chainRequest = chain.request()

            val request = chainRequest.newBuilder().apply {
                addHeader("Accept", "application/json")
                addHeader("Authorization", "KakaoAK $API_KEY")
            }.build()

            return chain.proceed(request)
        }
    }

    /*
     * 현재 선언된 요청 인터페이스 객체(RetrofitInterface)를 리턴한다
     */
    internal fun <T> getRetrofitService(restClass: Class<T>): T {
        return retrofit.create(restClass)
    }
}