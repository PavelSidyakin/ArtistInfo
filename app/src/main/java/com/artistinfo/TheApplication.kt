package com.artistinfo

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import com.artistinfo.di.AppComponent
import com.artistinfo.di.DaggerAppComponent
import com.artistinfo.domain.data.ApplicationProvider
import javax.inject.Inject

class TheApplication : Application() {
    @Inject
    lateinit var applicationProvider: ApplicationProvider

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .build()

        appComponent.inject(this)

        applicationProvider.init(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    companion object {
        private lateinit var appComponent: AppComponent

        fun getAppComponent(): AppComponent {
            return appComponent
        }
    }
}