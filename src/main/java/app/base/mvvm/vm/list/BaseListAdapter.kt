package app.base.mvvm.vm.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import app.base.R
import app.base.view.OnItemClick
import app.base.widget.ILoadMore

/**
 * Created by daniel on 18-1-15.
 */
abstract class BaseListAdapter<D : Any> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: OnItemClick<D>? = null
    var loadMore: ILoadMore? = null
    private lateinit var footerBinding: ViewDataBinding
    var lists: MutableList<D> = mutableListOf()
    var header: Boolean = false
    val isEmpty = ObservableBoolean(true)

    var footerType: Int = TYPE_FOOTER_LOADING
        set(value) {
            if (value !in TYPE_FOOTER_LOADING..TYPE_FOOTER_NONE) {
                throw Throwable(" Type not right")
            }
            if (field == value) {
                return
            }
            field = value
            notifyItemChanged(itemCount - 1)
        }


    fun clear() {
        lists.clear()
        isEmpty.set(true)
        notifyItemRangeRemoved(0, itemCount - 1)
    }

    open fun addAll(listSet: MutableList<D>) {
        if (listSet.isEmpty()) {
            return
        }
        val start = itemCount
        lists.addAll(listSet)
        val end = itemCount - 1
        isEmpty.set(itemCount == 0)
        notifyItemRangeInserted(start, end)
    }

    open fun setData(listSet: MutableList<D>) {
        lists.clear()
        lists.addAll(listSet)
        isEmpty.set(itemCount == 0)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        var count = lists.size
        if (count > 0 && footerType != TYPE_FOOTER_NONE) {
            count += 1
        }
        if (header) {
            count += 1
        }
        return count
    }

    open fun createFooterView( footerType: Int, layoutInflater: LayoutInflater, parent: ViewGroup? ): ViewDataBinding {
        footerBinding =
                when (footerType) {
                    TYPE_FOOTER_NO_MORE -> DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.footer_no_more_data,
                        parent,
                        false
                    )
                    TYPE_FOOTER_LOADING -> DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.footer_loading,
                        parent,
                        false
                    )
                    TYPE_FOOTER_ERROR -> DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.footer_loading,
                        parent,
                        false
                    )
                    else -> DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.footer_loading,
                        parent,
                        false
                    )
                }
        return footerBinding
    }

    override fun getItemViewType(position: Int): Int {
        var res = -1
        when (position) {
            0 -> {
                if (header) {
                    res = TYPE_HEADER
                } else {
                    res = TYPE_CONTENT
                }
            }
            in 1 until lists.size -> {
                res = TYPE_CONTENT
            }

            lists.size -> {
                if (header) {
                    res = TYPE_CONTENT
                } else {
                    res = footerType
                }
            }
            lists.size + 1 -> {
                res = footerType
            }
        }
        return res
    }

    /*
   * @params
     */
    abstract fun onCreateItemBinding( layoutInflater: LayoutInflater, parent: ViewGroup ): ViewDataBinding

    private fun onCreateHeaderBinding(layoutInflater: LayoutInflater, layoutId:Int, parent: ViewGroup): ViewDataBinding{
      return DataBindingUtil.inflate<ViewDataBinding>(layoutInflater,layoutId,parent,false)
    }

    open fun getHeaderLayoutId() = R.layout.list_header
    open fun bindingHeader(holder: RecyclerView.ViewHolder){}

    abstract fun onCreateVM(position: Int, data: D): Any


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (holder is BaseViewHolder) {
            when (viewType) {
                TYPE_CONTENT -> {
                    val indexInLists = if (header) {
                        position - 1
                    } else {
                        position
                    }
                    holder.bindingVM(onCreateVM(position, lists[indexInLists]))
                }
                TYPE_HEADER -> {
                    bindingHeader(holder)
                }
                TYPE_FOOTER_LOADING -> {
                    loadMore?.onLoadMore()
                }
                TYPE_FOOTER_NO_MORE -> {
                }
                TYPE_FOOTER_ERROR -> {
                    //TODO retry
                }
                TYPE_FOOTER_NONE -> {
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_CONTENT -> BaseViewHolder(onCreateItemBinding(layoutInflater, parent))
            TYPE_HEADER -> BaseViewHolder( onCreateHeaderBinding(layoutInflater, getHeaderLayoutId(),parent) )//TODO for emptyview or error
            in TYPE_FOOTER_LOADING..TYPE_FOOTER_NONE -> {
                BaseViewHolder(createFooterView(viewType, layoutInflater, parent))
            }
            else -> BaseViewHolder(onCreateItemBinding(layoutInflater, parent))
        }
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_CONTENT = 1
        const val TYPE_FOOTER_LOADING = 2
        const val TYPE_FOOTER_NO_MORE = 3
        const val TYPE_FOOTER_ERROR = 4
        const val TYPE_FOOTER_NONE = 5


    }

    abstract class BaseDiffCallback<out D>(val oldList: List<D>, val newList: List<D>) :
        DiffUtil.Callback() {

        override fun getNewListSize() = newList.size

        override fun getOldListSize() = oldList.size

    }

}