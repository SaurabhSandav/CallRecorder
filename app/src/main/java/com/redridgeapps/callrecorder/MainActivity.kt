package com.redridgeapps.callrecorder

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.redridgeapps.callrecorder.utils.PREF_IS_FIRST_RUN
import com.redridgeapps.callrecorder.utils.get
import com.redridgeapps.callrecorder.utils.modify
import com.redridgeapps.callrecorder.viewmodel.utils.ComposeViewModelFetcher
import com.redridgeapps.callrecorder.viewmodel.utils.ComposeViewModelStores
import com.redridgeapps.ui.routing.composeHandleBackPressed
import com.redridgeapps.ui.showUI
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefs: SharedPreferences

    @Inject
    lateinit var composeViewModelFetcher: ComposeViewModelFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val composeViewModelStores by viewModels<ComposeViewModelStores>()

        prefs.modify(PREF_IS_FIRST_RUN, false)
        val isFirstRun = prefs.get(PREF_IS_FIRST_RUN)

        showUI(isFirstRun, composeViewModelStores, composeViewModelFetcher)
    }

    override fun onBackPressed() {
        if (!composeHandleBackPressed())
            super.onBackPressed()
    }
}
