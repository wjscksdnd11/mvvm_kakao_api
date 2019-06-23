package com.jeon.mvvm_kakao_image.datas.source

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.jeon.mvvm_kakao_image.datas.ImageData
import com.jeon.mvvm_kakao_image.datas.apis.KakaoApiService
import io.reactivex.disposables.CompositeDisposable

class ImageDataSourceFactory(
    private val compositeDisposable: CompositeDisposable,
    private val networkService: KakaoApiService,
    var query:String?
) : DataSource.Factory<Int, ImageData>(){

    val imageDataSourceLiveData = MutableLiveData<ImageDataSource>()
    override fun create(): DataSource<Int, ImageData> {
        val newsDataSource = ImageDataSource(networkService,compositeDisposable,query)
        imageDataSourceLiveData.postValue(newsDataSource)
        return newsDataSource
    }

}