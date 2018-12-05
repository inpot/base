package app.base.view

import android.view.View

/**
 * Created by daniel on 18-3-5.
 */
interface OnItemClick<in T>{

    fun onItemClick(view: View, data:T)
}