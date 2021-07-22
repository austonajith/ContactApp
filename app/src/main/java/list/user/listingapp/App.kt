package list.user.listingapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import list.user.listingapp.utils.isDebug
import list.user.listingapp.utils.isNotDebug
import list.user.listingapp.utils.log.CrashReportingTree
import timber.log.Timber

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun initTimber() {
        isDebug {
            Timber.plant(Timber.DebugTree())
        }
        isNotDebug {
            Timber.plant(CrashReportingTree())
        }
    }
}