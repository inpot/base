package app.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import app.base.di.IBuildComp
import app.base.di.component.ActivityComp
import app.base.di.component.DaggerFragmentComp
import app.base.di.component.FragmentComp
import app.base.mvvm.repository.IRepository
import app.base.mvvm.view.IView
import app.base.mvvm.vm.BaseVM
import app.base.widget.NoBgDialog
import java.lang.Exception

/**
 * Created by daniel on 18-1-26.
 */
abstract class BaseFragment : Fragment(), IBuildComp, IBaseView {

    val TAG = "BaseFragment"
    private var loadingDialog: AppCompatDialog? = null
    lateinit var mFragmentComp: FragmentComp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildComp()
    }

    protected fun <B : ViewDataBinding, P : IRepository, V : IView> bindViewModel(
            layoutInflater: LayoutInflater,
            container: ViewGroup?,
            layoutResId: Int,
            viewModel: BaseVM<P, V>): B {
        val binding = DataBindingUtil.inflate<B>(layoutInflater, layoutResId, container, false)
        viewModel.repository.setLifecycleOwner(this)
        binding.setVariable(BR.vm, viewModel)
        if (ViewDataBinding.getBuildSdkInt() < Build.VERSION_CODES.KITKAT) {
            binding.executePendingBindings()
        }
        return binding
    }

    fun fragmentComp(): FragmentComp {
        if (!::mFragmentComp.isInitialized)
            mFragmentComp = DaggerFragmentComp.builder()
                    .activityComp(activityComp())
                    .build()
        return mFragmentComp
    }

    fun activityComp(): ActivityComp {
        val tmp = activity as BaseActivity
        if(!tmp.isInitialized()){
            tmp.initDI()
        }
        return tmp.activityComp
    }

    @CallSuper
    override fun dismissLoading() {
        val showing = loadingDialog?.isShowing?:false
        if(showing) {
            try {
                loadingDialog?.dismiss()
            }catch (e:Exception){
                Log.w(TAG,"dissmissLoading ${e.message}")
                loadingDialog = null
            }
        }
    }

    @CallSuper
    override fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = onCreateLoadingDialog()
        }
        val canShow =  (activity?.isFinishing?:true).not()
        if(canShow){
            try {
                loadingDialog?.show()
            }catch (e:Exception){
                Log.w(TAG,"showLoading ${e.message}")
                loadingDialog = null
            }
        }
    }

    override fun onCreateLoadingDialog(): AppCompatDialog? {
        val tmp  = context;
        return if (tmp != null) {
            val loadingDialog = NoBgDialog(tmp)
            loadingDialog.setContentView(ProgressBar(tmp))
            loadingDialog.setCanceledOnTouchOutside(false)
            loadingDialog
        }else{
            null
        }
    }

    private var  toast: Toast? = null
    override fun showToast(msg: String) {
        context?.apply {
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
        context?.resources?.apply { showToast(getString(msgId)) }
    }
}