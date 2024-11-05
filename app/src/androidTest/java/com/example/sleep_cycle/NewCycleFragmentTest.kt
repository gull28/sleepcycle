package com.example.sleep_cycle

import android.os.Build
import android.widget.TimePicker
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sleep_cycle.mocks.modules.FakeToaster
import com.example.sleep_cycle.mocks.viewmodel.MockSleepCycleViewModel
import com.example.sleep_cycle.data.modules.Toaster
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NewCycleFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var toaster: Toaster

    @Inject
    lateinit var mockViewModel: MockSleepCycleViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        FakeToaster.toasts.clear()
        grantNotificationPermission()
    }

    private fun grantNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                "pm grant ${InstrumentationRegistry.getInstrumentation().targetContext.packageName} android.permission.POST_NOTIFICATIONS"
            ).close()
        }
    }

    private fun navigateToNewCycleScreen() {
        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.navController.navigate("newCycleScreen")
        }
    }

    private fun setTime(tag: String, hour: Int, minute: Int) {
        composeTestRule.onNodeWithTag(tag).performClick()
        onView(isAssignableFrom(TimePicker::class.java))
            .perform(PickerActions.setTime(hour, minute))
        onView(withText("OK")).perform(click())
    }

    private fun inputText(tag: String, text: String) {
        composeTestRule.onNodeWithTag(tag).performTextInput(text)
    }

    private fun checkToastMessage(expectedMessage: String) {
        assertTrue(FakeToaster.toasts.contains(expectedMessage))
    }

    @Test
    fun checkEmptyNameToastShown() {
        navigateToNewCycleScreen()
        composeTestRule.onNodeWithText("Save").performClick()
        checkToastMessage("Please enter the correct name")
    }

    @Test
    fun checkNullValuesTimeInputDialog() {
        navigateToNewCycleScreen()
        composeTestRule.onNodeWithText("Add time").performClick()
        composeTestRule.onNodeWithTag("save_time").performClick()
        checkToastMessage("Please fill all fields correctly")
    }

    @Test
    fun checkOverlapSleepTimes() {
        navigateToNewCycleScreen()

        composeTestRule.onNodeWithText("Add time").performClick()
        inputText("name_input", "Sample Sleep Time")
        setTime("time_select", 8, 30)
        composeTestRule.onNodeWithTag("time_select").assertTextContains("Start Time: 08:30")
        setTime("duration_select", 1, 30)
        composeTestRule.onNodeWithTag("duration_select").assertTextContains("Duration: 01:30")
        composeTestRule.onNodeWithTag("save_time").performClick()

        composeTestRule.onNodeWithText("Add time").performClick()
        inputText("name_input", "Sample Sleep Time 2")
        setTime("time_select", 9, 30)
        composeTestRule.onNodeWithTag("time_select").assertTextContains("Start Time: 09:30")
        setTime("duration_select", 2, 30)
        composeTestRule.onNodeWithTag("duration_select").assertTextContains("Duration: 02:30")
        composeTestRule.onNodeWithTag("save_time").performClick()

        checkToastMessage("Please don't use overlapping sleep times")
    }

    @Test
    fun openTimeInputDialogAndAddSleepTimeWithDuration() {
        navigateToNewCycleScreen()

        composeTestRule.onNodeWithText("Add time").performClick()
        inputText("name_input", "Sample Sleep Time")
        setTime("time_select", 8, 30)
        composeTestRule.onNodeWithTag("time_select").assertTextContains("Start Time: 08:30")
        setTime("duration_select", 1, 30)
        composeTestRule.onNodeWithTag("duration_select").assertTextContains("Duration: 01:30")
        composeTestRule.onNodeWithTag("save_time").performClick()

        composeTestRule.onNodeWithText("Sample Sleep Time").assertExists()
    }
}
