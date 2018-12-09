package app.base.di.modules

import android.content.Context
import android.content.SharedPreferences
import app.base.di.scope.PerApplication
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Created by daniel on 17-12-23.
 */
@Module
class PreferenceModule(val context: Context) {
        @PerApplication
        @Provides
        @Named(PREFERENCES_APP_SETTINGS)
        fun provideAppPreference(): SharedPreferences = context.getSharedPreferences("${context.packageName}_$PREFERENCES_APP_SETTINGS", Context.MODE_PRIVATE)

        @PerApplication
        @Provides
        @Named(PREFERENCES_USER)
        fun provideUserPreference(): SharedPreferences = context.getSharedPreferences("${context.packageName}_$PREFERENCES_USER", Context.MODE_PRIVATE)

       companion object {
                const val PREFERENCES_USER = "user"
                const val PREFERENCES_APP_SETTINGS = "settings"
            }
}