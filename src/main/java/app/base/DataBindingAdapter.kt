package app.base

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.*
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import androidx.databinding.BindingAdapter

import app.base.di.scope.ListType
import app.base.widget.OnTextChanged
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import java.util.regex.Pattern
import kotlin.math.roundToInt


@BindingAdapter(value = ["normalTitleColor", "selectedTitleColor"], requireAll = true)
fun bindTabLayoutTextColor(tabLayout: TabLayout, normalTitleColor: Int, selectedTitleColor: Int) {
    tabLayout.setTabTextColors(normalTitleColor, selectedTitleColor)
}

@BindingAdapter(value = ["android:checked", "onCheckedChangeListener"])
fun bindCheckedState(compoundButton: CompoundButton, checked: Boolean, onCheckedChangeListener: CompoundButton.OnCheckedChangeListener) {
    compoundButton.setOnCheckedChangeListener(null)
    compoundButton.isChecked = checked
    compoundButton.setOnCheckedChangeListener(onCheckedChangeListener)
}

@BindingAdapter(value = ["viewPager", "adapter"])
fun bindTabLayoutToViewPager(tabLayout: TabLayout, viewPagerId: Int, pagerAdapter: PagerAdapter) {
    val viewPager = tabLayout.rootView.findViewById<View>(viewPagerId) as ViewPager
    if (viewPager.adapter == null)
        viewPager.adapter = pagerAdapter
    tabLayout.setupWithViewPager(viewPager)
}

@BindingAdapter(value = ["isDrawerOpen", "drawerGravity"])
fun controlDrawer(drawerLayout: DrawerLayout, isDrawerOpen: Boolean, gravity: Int) {
    if (isDrawerOpen) {
        drawerLayout.openDrawer(gravity)
    } else {
        drawerLayout.closeDrawer(gravity)
    }
}

fun getPath(resources:Resources,url:String?):String{
    if(url != null && !TextUtils.isEmpty(url)){
        return if(url.startsWith("http")){ url
        }else{
            "${resources.getString(R.string.api_host)}$url"
        }
    }else{
        return ""
    }

}


@BindingAdapter(value = ["blurImageUrl", "placeHolder"])
fun loadImageByUrlBlur(imageView: ImageView, url: String?, placeHolder: Drawable) {
    var path = getPath(imageView.context.resources,url)
    if(TextUtils.isEmpty(path)){
        imageView.setImageDrawable(placeHolder)
    }else{
        val option = RequestOptions().placeholder(placeHolder).error(placeHolder).fallback(placeHolder)
        Glide.with(imageView.context)
            .load(path)
            .apply(option)
            .into(imageView)
    }
}

@BindingAdapter(value = ["blurImageUrl", "placeHolder"])
fun loadImageByUrlBlur(imageView: ImageView, url: Uri, placeHolderRes: Int) {
    val option = RequestOptions().placeholder(placeHolderRes).error(placeHolderRes).fallback(placeHolderRes)
    Glide.with(imageView.context)
        .load(url)
        .apply(option)
        .into(imageView)
}

@BindingAdapter(value = ["blurImageUrl", "placeHolder"])
fun loadImageByUrlBlur(imageView: ImageView, url: String?, placeHolderRes: Int) {
    var path = getPath(imageView.context.resources,url)
    if(TextUtils.isEmpty(path)){
        imageView.setImageResource(placeHolderRes)
    }
    val option = RequestOptions().placeholder(placeHolderRes).error(placeHolderRes).fallback(placeHolderRes)
    Glide.with(imageView.context)
        .load(path)
        .apply(option)
        .into(imageView)
}


@BindingAdapter(value = ["android:drawableRight"])
fun setDrawableRight(textView: TextView, res: Int) {
    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0)
}

@BindingAdapter(value = ["textRes", "textColorRes"], requireAll = false)
fun setTextRes(textView: TextView, textRes: Int, textColorRes: Int) {
    if (textRes > 0)
        textView.setText(textRes)
    if (textColorRes > 0)
        textView.setTextColor(textView.resources.getColor(textColorRes))
}

@BindingAdapter(value = ["onTextChanged"])
fun bindTextChangedListener(editText: EditText, onTextChanged: OnTextChanged) {
    editText.onTextChange { onTextChanged.onTextChanged(it) }
}

@BindingAdapter(value = ["expand", "withAnim"])
fun setAppBarLayoutExpended(appBarLayout: AppBarLayout, expand: Boolean, withAnim: Boolean) {
    appBarLayout.setExpanded(expand, withAnim)
}

@BindingAdapter(value = ["layoutManager", "adapter", "onScrollListener"])
fun bindLoadMoreRecyclerView(recyclerView: RecyclerView,
                             layoutManager: RecyclerView.LayoutManager,
                             adapter: RecyclerView.Adapter<*>,
                             onScrollListener: RecyclerView.OnScrollListener) {
    recyclerView.adapter = adapter
    recyclerView.layoutManager = layoutManager
    recyclerView.addOnScrollListener(onScrollListener)
}

@BindingAdapter(value = ["showPassword"])
fun bindEditTextInputType(editText: EditText, showPassword: Boolean) {
    editText.inputType = if (showPassword) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
    editText.setSelection(if (editText.text == null) 0 else editText.text.length)
}

@BindingAdapter(value = ["orientation"])
fun bindRecyclerView(recyclerView: RecyclerView, orientation: String) {
    var ori = if (TextUtils.equals(orientation, ListType.HORIZONTAL)) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
    when (orientation) {
        ListType.HORIZONTAL -> ori = RecyclerView.HORIZONTAL
        ListType.VERTICAL -> ori = RecyclerView.VERTICAL
        else -> ori = RecyclerView.HORIZONTAL
    }
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, ori, false)
}

@BindingAdapter(value = ["requestFocus"])
fun bindRequestFocusEvent(editText: EditText, requestFocus: Boolean) {
    if (requestFocus) {
        editText.requestFocusFromTouch()
    } else {
        editText.clearFocus()
    }
}

/**
 * two way binding for refreshing attr
 *
 *
 */
@BindingAdapter(value = ["refreshing", "refreshListener", "refreshingAttrChanged"], requireAll = false)
fun bindSwipRefreshingState(swipeRefreshLayout: SwipeRefreshLayout, refreshing: Boolean, onRefreshListener: SwipeRefreshLayout.OnRefreshListener?, bindingListener: InverseBindingListener) {
    swipeRefreshLayout.isRefreshing = refreshing
    swipeRefreshLayout.setOnRefreshListener {
        onRefreshListener?.onRefresh()
        bindingListener.onChange()

    }
}

@InverseBindingAdapter(attribute = "refreshing", event = "refreshingAttrChanged")
fun bindingIsRefresing(swipeRefreshLayout: SwipeRefreshLayout): Boolean {
    return swipeRefreshLayout.isRefreshing
}

@BindingAdapter(value = ["drawerVisible"])
fun toggleDrawer(drawerLayout: DrawerLayout, drawerVisible: Boolean) {
    if (drawerVisible) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            return
        drawerLayout.openDrawer(GravityCompat.START, true)
    } else {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START))
            return
        drawerLayout.closeDrawer(GravityCompat.START, true)
    }
}

@BindingAdapter(value = ["icon", "msg"])
fun bindEmptyViewDrawable(textView: TextView, iconRes: Int, msgRes: Int) {
    if (iconRes != 0)
        textView.setCompoundDrawablesWithIntrinsicBounds(0, iconRes, 0, 0)
    if (msgRes != 0)
        textView.setText(msgRes)
}


@BindingAdapter(value = ["globalLayoutListener"])
fun bindOnGlobalLayoutListener(view: View, onGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener) {
    view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
}

@BindingAdapter(value = ["colorSchemeResources"])
fun bingSwipRefeshSchemaColor(refreshLayout: SwipeRefreshLayout, colorSchemeResources: Int) {
    val colors = intArrayOf(colorSchemeResources)
    refreshLayout.setColorSchemeColors(*colors)
}


@BindingAdapter(value = ["adapter"])
fun bingGridView(gridView: GridView, adapter: ListAdapter) {
    gridView.adapter = adapter
    adapter.apply {  }
}

fun EditText.onTextChange(body :(txt:String?) -> Unit){
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            body.invoke(s?.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })


}

@BindingAdapter(value = ["limitDecimal"])
fun EditText.limitDecimal(limitDecimal:Int){
    this.onTextChange {
        val regex = "^\\d+.$"
        val r = Pattern.compile(regex)
        val matcher = r.matcher(it)
        if (matcher.matches()) {
            this.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(it!!.length +limitDecimal))
        }
    }
}

@BindingAdapter("android:layout_marginTop")
fun setLayoutMarginTop(view: View,margin: Float) {
    val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.topMargin = margin.roundToInt()
    view.layoutParams = layoutParams
}

@BindingAdapter("android:layout_marginStart")
fun setLayoutHeight(view: View,margin: Float) {
    val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.marginStart = margin.roundToInt()
    view.layoutParams = layoutParams
}