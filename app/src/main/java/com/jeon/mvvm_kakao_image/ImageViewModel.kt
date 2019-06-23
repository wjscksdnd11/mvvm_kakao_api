package com.jeon.mvvm_kakao_image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jeon.mvvm_kakao_image.datas.ImageData
import com.jeon.mvvm_kakao_image.datas.apis.KakaoApiService
import com.jeon.mvvm_kakao_image.datas.source.ImageDataSource
import com.jeon.mvvm_kakao_image.datas.source.ImageDataSourceFactory
import io.reactivex.disposables.CompositeDisposable

class ImageViewModel : ViewModel() {

    private val networkService = KakaoApiService.getService()
    var imagesList: LiveData<PagedList<ImageData>>
    private val compositeDisposable = CompositeDisposable()
    private val pageSize = 15
    private var imageDataSourceFactory: ImageDataSourceFactory
    private val config: PagedList.Config
    var imageUrl: MutableLiveData<String> = MutableLiveData()

    init {
        config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize)
            .setEnablePlaceholders(false)
            .build()
        imageDataSourceFactory = ImageDataSourceFactory(compositeDisposable, networkService, null)
        imagesList = LivePagedListBuilder<Int, ImageData>(imageDataSourceFactory, config).build()

    }


    fun setQuery(queryParams: String) {
        imageDataSourceFactory.query = queryParams
        imageDataSourceFactory.imageDataSourceLiveData.value?.invalidate()
    }

    fun getState(): LiveData<State> = Transformations.switchMap<ImageDataSource, State>(
        imageDataSourceFactory.imageDataSourceLiveData,
        ImageDataSource::state
    )

    fun retry() {
        imageDataSourceFactory.imageDataSourceLiveData.value?.retry()
    }

    fun listIsEmpty(): Boolean {
        return imagesList.value?.isEmpty() ?: true
    }

    fun itemClick(url: String) {
        imageUrl.postValue(url)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}