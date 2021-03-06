package app.base.mvvm.vm.list

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.base.mvvm.repository.IRepository
import app.base.mvvm.view.IView
import app.base.mvvm.vm.BaseVM
import app.base.widget.ILoadMore
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by daniel on 18-1-15.
 */
abstract class BaseListVM<Rep : IRepository, V : IView, D : Any> : ILoadMore, Observer<List<D>>, BaseVM<Rep, V>() {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var  adapter: BaseListAdapter<D>
    open var PAGE_SIZE = 25
    private var currentPage = 0
    var refreshing = ObservableBoolean(false)
    var loading = false


    val status = ObservableInt(STATUS_EMPTY)

    open fun initialize(repository:Rep,view:V,layoutManager: RecyclerView.LayoutManager,adapter: BaseListAdapter<D>){
        super.initialize(repository, view)
        this.adapter = adapter
        this.adapter.loadMore = this
        this.layoutManager = layoutManager

    }


    val refreshingListener = SwipeRefreshLayout.OnRefreshListener {
        loadData()
    }

    fun loadData() {
        if (loading) {
            return
        }
        currentPage = 0
        loading = true
        onLoadData(currentPage)
    }

    override fun onLoadMore() {
        if (loading) {
            return
        }
        currentPage++
        onLoadData(currentPage)
    }

    abstract fun onLoadData(page: Int)


    fun bindResult(result: List<D>?) {
        refreshing.set(false)
        val size = result?.size ?: 0
        if (size < PAGE_SIZE) {
            adapter.footerType = BaseListAdapter.TYPE_FOOTER_NO_MORE
        } else {
            adapter.footerType = BaseListAdapter.TYPE_FOOTER_LOADING
        }

        val listSet = mutableListOf<D>()
        if (result != null && size > 0) {
            listSet.addAll(result)
        }
        if (currentPage == 0) {
            adapter.setData(listSet)
        } else {
            if(!listSet.isEmpty()){
                adapter.addAll(listSet)
            }
        }
        status.set(if(adapter.itemCount > 0) STATUS_CONTENT else STATUS_EMPTY)
        loading = false
    }

    fun bindError(errorCode: Int, msg: String) {
        loading = false
        refreshing.set(false)
        if (currentPage > 0) {
            adapter.footerType = BaseListAdapter.TYPE_FOOTER_ERROR
        }else{
            adapter.clear()
            status.set(STATUS_ERROR)
        }
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        bindError(-1, "网络错误,稍后再试")
        view.dismissLoading()
    }

    override fun onNext(t: List<D>) {
        bindResult(t)
    }

    override fun onComplete() {
        view.dismissLoading()
    }

    override fun onSubscribe(d: Disposable) {
        view.showLoading()
    }


    companion object {
        const val STATUS_CONTENT = 1
        const val STATUS_EMPTY = 0
        const val STATUS_ERROR = -1
    }

}

