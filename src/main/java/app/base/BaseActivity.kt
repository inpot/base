package app.base

import android.app.Dialog
import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import app.base.di.IBuildComp
import app.base.di.component.ActivityComp
import app.base.di.component.DaggerActivityComp
import app.base.di.modules.ActivityModule
import app.base.mvvm.repository.IRepository
import app.base.mvvm.view.IView
import app.base.mvvm.vm.BaseVM

/**
 * Created by daniel on 17-11-28.
 * base activity for all activity that can use dagger  and databinding
 */
abstract class BaseActivity : AppCompatActivity(), IBuildComp, IBaseView {
    lateinit var activityComp: ActivityComp
    private var loadingDialog: AppCompatDialog? = null
    val TAG = "BaseActivity"

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       initDI()
    }

    fun initDI(){
        val application = application as BaseApplication
        activityComp = DaggerActivityComp.builder()
            .appComp(application.appComp)
            .activityModule(ActivityModule(this))
            .build()
        buildComp()
    }


    fun isInitialized():Boolean{
        return ::activityComp.isInitialized
    }



    private var  toast: Toast? = null
    @CallSuper
    override fun showToast(msg:String){
        runOnUiThread {
            try{

                if(toast == null){
                    toast = Toast.makeText(this,msg,Toast.LENGTH_SHORT)
                }
                toast?.setText(msg)
                toast?.show()
            }catch (e:Exception){
                Log.w(TAG,"showToast ${e.message}")
                toast = null
            }
        }
    }

    @CallSuper
    override fun showToast(msgId: Int) {
        showToast(getString(msgId))
    }

    @CallSuper
    override fun dismissLoading() {
        val showing = loadingDialog?.isShowing?:false
        if(showing){
            try{
                loadingDialog?.dismiss()
            }catch (e:Exception){
                Log.w(TAG,"dissmissLoading ${e.message}")
                loadingDialog = null
            }
        }
    }

    @CallSuper
    override fun showLoading() {
        if(loadingDialog == null){
            loadingDialog = onCreateLoadingDialog()
        }
        showDialog(loadingDialog)
    }

    override fun onCreateLoadingDialog(): AppCompatDialog? {
        val loadingDialog = AppCompatDialog(this)
        loadingDialog.window.setBackgroundDrawableResource(android.R.color.transparent);
        loadingDialog.setContentView(ProgressBar(this))
        loadingDialog.setCanceledOnTouchOutside(false)
        return loadingDialog
    }

    /**
     *  bind viewmodel to viewbinding
     *  @param homeAsUp for back button in toolbar
     *
     * */

    protected fun <B : ViewDataBinding, P : IRepository, V : IView> bindViewModel(layoutResId: Int, viewModel: BaseVM<P, V>, homeAsUp: Boolean): B {
        val binding = DataBindingUtil.setContentView<B>(this, layoutResId)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.apply {
            subtitle = ""
            setSupportActionBar(this)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(homeAsUp)
        viewModel.repository.setLifecycleOwner(this)
        binding.setVariable(BR.vm, viewModel)
        if (ViewDataBinding.getBuildSdkInt() < Build.VERSION_CODES.KITKAT) {
            binding.executePendingBindings()
        }
        return binding
    }

    /**
     * set back menu action for all activity
     *
     * **/
    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.run {
            if (this.itemId == android.R.id.home) {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
    lateinit var  imm : InputMethodManager

    override fun onPause() {
        super.onPause()
        if(!::imm.isInitialized){
            imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }
//        imm.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }

    override fun showDialog(dialog:Any?){
        if(!isFinishing &&!isDestroyed){
            try {
                when(dialog){
                    is Dialog ->{ dialog.show() }
                    is DialogFragment -> {dialog.show(supportFragmentManager,"$dialog")}
                    else ->{ Log.w(TAG," cannot show dialog type error:$dialog") }
                }
            }catch (e:Exception){
                Log.w(TAG,"cannot show dialog: ${e.message}")
            }
        }else{
            Log.w(TAG,"cannot show dialog: status error ")
        }
    }
}