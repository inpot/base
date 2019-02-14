package app.base

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.util.Log
import android.webkit.WebView
import androidx.annotation.CallSuper
import androidx.multidex.MultiDexApplication
import com.hjq.toast.ToastUtils
import app.base.di.IBuildComp
import app.base.di.component.AppComp
import app.base.di.component.DaggerAppComp
import app.base.di.modules.*

/**
 * Created by daniel on 17-12-23.
 */
open class BaseApplication():MultiDexApplication(),IBuildComp{
    lateinit var appComp: AppComp
    val TAG = "BaseApplication"
    override fun onCreate() {
        super.onCreate()
        val pid = Process.myPid()
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runnings = am.runningAppProcesses
        val current = runnings.firstOrNull{it.pid == pid}
        Log.w(TAG,"proccessName ${current?.processName}")
        if(current?.processName == "$packageName"){
            initMainProcess(current)
        }else{
            initOtherProcess(current)
        }
    }

    @CallSuper
    open fun initMainProcess(process:ActivityManager.RunningAppProcessInfo){
        buildComp()
        ToastUtils.init(this)
    }

    @CallSuper
    open fun initOtherProcess(current:ActivityManager.RunningAppProcessInfo?){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            try{
                WebView.setDataDirectorySuffix("${current?.processName}")
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    override fun buildComp() {
      appComp =  DaggerAppComp.builder()
              .preferenceModule(PreferenceModule(this))
              .appModule(AppModule(this))
              .build()
    }

}
