package app.base.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import app.base.BuildConfig
import app.base.R
import app.base.di.HttpLogInterceptor
import app.base.di.scope.PerApplication
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
        okHttpBuilder.connectTimeout(30, TimeUnit.SECONDS)
        okHttpBuilder.readTimeout(30, TimeUnit.SECONDS)
        okHttpBuilder.writeTimeout(30,TimeUnit.SECONDS)
        if(BuildConfig.DEBUG) {
            val logging = HttpLogInterceptor()
            logging.level = HttpLogInterceptor.Level.BODY
            okHttpBuilder.addInterceptor(logging)
        }
        okHttpBuilder.addInterceptor {
            val original = it.request();
            val token = preference.getString("token",null)
            val requestBuilder = original.newBuilder()
            if(!TextUtils.isEmpty(token)){
                requestBuilder.addHeader("token", token)
            }
            val request = requestBuilder.build();
            val response = it .proceed(request);
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