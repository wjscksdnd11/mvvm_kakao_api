package com.jeon.mvvm_kakao_image.datas.source

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.jeon.mvvm_kakao_image.State
import com.jeon.mvvm_kakao_image.datas.ImageData
import com.jeon.mvvm_kakao_image.datas.apis.KakaoApiService
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers

class ImageDataSource(
    private val networkService: KakaoApiService,
    private val compositeDisposable: CompositeDisposable,
    private var query:String?
) : PageKeyedDataSource<Int, ImageData>() {
    val state: MutableLiveData<State> = MutableLiveData()
    private var isEnd: Boolean = false

    private var retryCompletable: Completable? = null


    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ImageData>) {
        updateState(State.LOADING)
        isEnd = false
        query?.let {
            networkService.getImages(query = it, sort = "accuracy",page = 1, size = params.requestedLoadSize)
                .subscribe(
                    { response ->
                        updateState(State.DONE)
                        callback.onResult(response.documents,
                            null,
                            2
                        )
                    },
                    {
                        updateState(State.ERROR)
                        setRetry(Action { loadInitial(params, callback) })
                    }
                )
        }?.let {
            compositeDisposable.add(
                it
            )
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ImageData>) {
        updateState(State.LOADING)
        if(!isEnd) {
            query?.let {
                networkService.getImages(
                    query = it,
                    sort = "accuracy",
                    page = params.key,
                    size = params.requestedLoadSize
                )
                    .subscribe(
                        { response ->
                            updateState(State.DONE)
                            isEnd = response.meta.is_end
                            callback.onResult(response.documents, params.key + 1)

                        }, {
                            updateState(State.ERROR)
                            setRetry(Action { loadAfter(params, callback) })
                        }
                    )
            }?.let {
                compositeDisposable.add(
                    it
                )
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ImageData>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updateState(stateArg: State) {
        this.state.postValue(stateArg)
    }

    fun retry() {
        if (retryCompletable !=null){
            compositeDisposable.add(retryCompletable!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
            )
        }
    }

    private fun setRetry(action: Action){
        retryCompletable = Completable.fromAction(action)
    }
}
