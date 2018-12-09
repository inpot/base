package app.base.di.component

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import app.base.di.modules.AppModule
import app.base.di.modules.PreferenceModule
import app.base.di.scope.PerApplication
import dagger.Component
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

/**
 * Created by daniel on 17-11-27.
 */
@PerApplication
@Component(modules = [AppModule::class])
interface AppComp {

    fun appContext(): Context

    fun application(): Application

    fun getRetrofit(): Retrofit

    @Named(PreferenceModule.PREFERENCES_USER)
    fun getUserPreferences(): SharedPreferences

    @Named(PreferenceModule.PREFERENCES_APP_SETTINGS)
    fun getAppPreferences(): SharedPreferences

    fun getResource(): Resources

}