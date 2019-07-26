package com.artistinfo.data

import android.content.Context
import com.artistinfo.TheApplication
import com.artistinfo.domain.data.ApplicationProvider
import javax.inject.Inject

class ApplicationProviderImpl @Inject constructor() : ApplicationProvider {
    private lateinit var theApplication: TheApplication

    override fun init(theApplication: TheApplication) {
        this.theApplication = theApplication
    }

    override val applicationContext: Context
        get() = theApplication.applicationContext


}