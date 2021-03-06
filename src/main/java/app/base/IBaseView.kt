package app.base

import androidx.appcompat.app.AppCompatDialog

/**
 * Created by daniel on 18-1-31.
 */
interface IBaseView{
    fun showLoading()
    fun onCreateLoadingDialog():AppCompatDialog?
    fun dismissLoading()
    fun showToast(msg:String)
    fun showToast(msgId:Int)
    fun showDialog(dialog:Any?)
}