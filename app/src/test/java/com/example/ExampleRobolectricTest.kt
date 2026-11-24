package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AgencyCatalog
import com.example.data.PreferencesRepository
import com.example.model.AppLanguage
import com.example.model.AppPage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WEBSHAKTI AI", appName)
    }

    @Test
    fun `verify website studio catalog completeness`() {
        assertEquals(10, AppPage.entries.size)
        assertEquals(8, AgencyCatalog.websiteTools.size)
        assertEquals(4, AgencyCatalog.websiteTeamRoles.size)
        assertEquals(3, AgencyCatalog.apiModules.size)
        assertEquals("+919569221396", PreferencesRepository.DEFAULT_WHATSAPP_NUMBER)
        assertEquals("1234AT", PreferencesRepository.ADMIN_PASSCODE)
        AppPage.entries.forEach { page ->
            assertTrue(AgencyCatalog.pageTitle(page, AppLanguage.EN).isNotBlank())
            assertTrue(AgencyCatalog.pageTitle(page, AppLanguage.HI).isNotBlank())
        }
    }
}
