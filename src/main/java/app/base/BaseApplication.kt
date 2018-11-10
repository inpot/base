package app.base

import androidx.multidex.MultiDexApplication
import app.base.di.IBuildComp
import app.base.di.component.AppComp
import app.base.di.component.DaggerAppComp
import app.base.di.modules.*

/**
 * Created by daniel on 17-12-23.
 */
open class BaseApplication():MultiDexApplication(),IBuildComp{
    lateinit var appComp: AppComp
    override fun onCreate() {
        super.onCreate()
        buildComp()
    }

    override fun buildComp() {
      appComp =  DaggerAppComp.builder()
              .preferenceModule(PreferenceModule(this))
              .appModule(AppModule(this))
              .build()
    }

}
