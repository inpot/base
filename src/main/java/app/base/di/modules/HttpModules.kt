package app.base.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import app.base.BuildConfig
import app.base.R
import app.base.di.scope.PerApplication
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by daniel on 17-10-18.
 */

@Module(includes = [PreferenceModule::class])
object HttpModules {
    @JvmStatic
    @Provides
    @PerApplication
    fun provideRetrofit(moshi: Moshi, context: Context, preference:SharedPreferences): Retrofit {
        val baseUrl = context.getString(R.string.api_host)
        val okHttpBuilder= OkHttpClient.Builder()
        okHttpBuilder.connectTimeout(10, TimeUnit.SECONDS)
        okHttpBuilder.readTimeout(10, TimeUnit.SECONDS)
        okHttpBuilder.writeTimeout(10,TimeUnit.SECONDS)
        okHttpBuilder.addInterceptor {
            val original = it.request();
            val token = preference.getString("token",null)
            val requestBuilder = original.newBuilder()
            if(!TextUtils.isEmpty(token)){
                requestBuilder.addHeader("token", "value1")
            }
            val request = requestBuilder.build();
            val response = it .proceed(request);
            if(BuildConfig.DEBUG){
                Log.i("http"," ${original.method()} : ${original.url()} + ${original.headers()}")
            }
            response
        }
        val builder = Retrofit.Builder().baseUrl(baseUrl)
                .client(okHttpBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
        return builder.build()
    }

    @JvmStatic
    @Provides
    @PerApplication
    fun provideMoshi()  = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
}