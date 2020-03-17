package com.amartin.marvelapplication

import android.app.Activity
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingRegistry
import androidx.test.rule.ActivityTestRule
import com.amartin.marvelapplication.api.MarvelServiceImpl
import com.amartin.marvelapplication.di.NamesDI
import com.amartin.marvelapplication.utils.dispatcher
import com.jakewharton.espresso.OkHttp3IdlingResource
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.concurrent.thread

abstract class TestBaseUi<T : Activity>(activityClass: Class<T>) : KoinTest {

    private val mockServer = MockWebServer()
    private val idlingResource = OkHttp3IdlingResource.create("OkHttp", get<MarvelServiceImpl>().okHttpClient)

    val abominationUltimateId = 1016823

    @get:Rule
    val activityTestRule = ActivityTestRule(activityClass, false, false)

    @Before
    fun setUp() {
        mockServer.dispatcher = dispatcher(ApplicationProvider.getApplicationContext())
        mockServer.start()
        loadKoinModules(module{
            single(named(NamesDI.baseUrlMarvel), override = true) { askMockServerUrlOnAnotherThread() }
        })
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
        IdlingRegistry.getInstance().unregister(idlingResource)
        activityTestRule.finishActivity()
    }

    private fun askMockServerUrlOnAnotherThread(): String {
        /*
        This needs to be done immediately, but the App will crash with
        "NetworkOnMainThreadException" if this is not extracted from the main thread. So this is
        a "hack" to prevent it. We don't care about blocking in a test, and it's fast.
        */
        var url = ""
        val t = thread {
            url = mockServer.url("/").toString()
        }
        t.join()
        return url
    }
}