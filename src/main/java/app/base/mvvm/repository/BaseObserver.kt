package app.base.mvvm.repository

import app.base.IBaseView
import app.base.R
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by daniel on 18-1-31.
 */
abstract class BaseObserver<T>(protected val baseView: IBaseView): Observer<T>{
    override fun onComplete() {
        baseView.dismissLoading()
    }

    override fun onSubscribe(d: Disposable) {
        baseView.showLoading()
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        baseView.dismissLoading()
        baseView.showToast(R.string.connect_failed)
    }

}