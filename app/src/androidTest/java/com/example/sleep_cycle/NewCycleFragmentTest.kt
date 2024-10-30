import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sleep_cycle.dao.MockSleepCycleDao
import com.example.sleep_cycle.dao.MockSleepTimeDao
import com.example.sleep_cycle.modules.FakeToaster
import com.example.sleep_cycle.repository.FakeSleepCycleRepository
import com.example.sleep_cycle.ui.screens.NewCycleFragment
import com.example.sleep_cycle.viewmodel.MockSleepCycleViewModel
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runner.manipulation.Ordering
import org.mockito.Mockito.mock


@RunWith(AndroidJUnit4::class)
class NewCycleFragmentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: MockSleepCycleViewModel


    @Before
    fun setup() {
        FakeToaster.toasts.clear()

        val mockContext = mock(Context::class.java)

        val mockSleepCycleDao = MockSleepCycleDao()
        val mockSleepTimeDao = MockSleepTimeDao()

        mockViewModel = MockSleepCycleViewModel(
            sleepCycleRepository = FakeSleepCycleRepository(mockSleepCycleDao),
            sleepTimeRepository = FakeSleepTimeRepository(mockSleepTimeDao),
            appContext = mockContext
        )
    }

    @Test
    fun checkEmptyNameToastShown() {
        composeTestRule.setContent {
            NewCycleFragment(
                navController = rememberNavController(),
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Save").performClick()

        assertTrue(FakeToaster.toasts.contains("Please enter the correct name"))
    }

    @Test
    fun checkOverlapToastShown() {
        composeTestRule.setContent {
            NewCycleFragment(
                navController = rememberNavController(),
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("Add time").performClick()

        composeTestRule.onNodeWithText("Save").performClick()

        assertTrue(FakeToaster.toasts.contains("Please don't use overlapping sleep times"))
    }
}
