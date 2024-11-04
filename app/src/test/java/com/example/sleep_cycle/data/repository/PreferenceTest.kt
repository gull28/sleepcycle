import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.core.app.ApplicationProvider
import com.example.sleep_cycle.data.repository.Preference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
class PreferenceTest {

    private lateinit var context: Context
    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var preference: Preference

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        testDataStore = PreferenceDataStoreFactory.create(
            produceFile = { File(context.filesDir, "datastore-test.preferences_pb") },
            scope = TestCoroutineScope(UnconfinedTestDispatcher())
        )

        preference = Preference(context)
    }

    @Test
    fun testSaveMode() = runTest {
        preference.saveMode(true)

        val savedMode = preference.modeFlow.first()
        assertEquals(true, savedMode)
    }
}
