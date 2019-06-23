package com.jeon.mvvm_kakao_image.datas.apis

import com.jeon.mvvm_kakao_image.datas.KakaoData
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoApiService {

    @GET("/v2/search/image")
    fun getImages(
        @Query("query") query: String,
        @Query("sort") sort: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Observable<KakaoData>

    companion object {
        fun getService() = RequestManager.getRetrofitService(KakaoApiService::class.java)
    }
}