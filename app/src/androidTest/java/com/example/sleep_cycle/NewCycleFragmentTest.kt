package com.example.sleep_cycle

import android.content.Context
import android.os.Build
import android.util.Log
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
import com.example.sleep_cycle.MainActivity
import com.example.sleep_cycle.data.modules.Toaster
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers
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

    // Hilt will inject these dependencies from TestModule
    @Inject
    lateinit var toaster: Toaster

    @Inject
    lateinit var mockViewModel: MockSleepCycleViewModel

    @Before
    fun setup() {
        hiltRule.inject() // Trigger Hilt injection

        // Clear any toasts from previous tests
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

    @Test
    fun checkEmptyNameToastShown() {
        composeTestRule.onNodeWithText("Got it!").performClick()

        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.navController.navigate("newCycleScreen")
        }

        composeTestRule.onNodeWithText("Save").performClick()

        Log.d("TestLog", "Captured toasts: ${FakeToaster.toasts}")

        assertTrue(FakeToaster.toasts.contains("Please enter the correct name"))
    }

    @Test
    fun checkNullValuesTimeInputDialog() {
        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.navController.navigate("newCycleScreen")
        }

        composeTestRule.onNodeWithText("Add time").performClick()
        composeTestRule.onNodeWithTag("save_time").performClick()
        assertTrue(FakeToaster.toasts.contains("Please fill all fields correctly"))
    }

    @Test
    fun checkOverlapSleepTimes() {
        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.navController.navigate("newCycleScreen")
        }

        composeTestRule.onNodeWithText("Add time").performClick()

        composeTestRule.onNodeWithTag("name_input").assertExists()

        composeTestRule.onNodeWithTag("name_input").performTextInput("Sample Sleep Time")

        composeTestRule.onNodeWithTag("time_select").performClick()

        onView(Matchers.instanceOf(TimePicker::class.java))
            .perform(PickerActions.setTime(8, 30))

        onView(withText("OK")).perform(click())

        composeTestRule.onNodeWithTag("time_select").assertTextContains("Start Time: 08:30")

        composeTestRule.onNodeWithTag("duration_select").performClick()

        onView(Matchers.instanceOf(TimePicker::class.java))
            .perform(PickerActions.setTime(1, 30))

        onView(withText("OK")).perform(click())

        composeTestRule.onNodeWithTag("duration_select").assertTextContains("Duration: 01:30")

        composeTestRule.onNodeWithTag("save_time").performClick()

        //

        composeTestRule.onNodeWithText("Add time").performClick()

        composeTestRule.onNodeWithTag("name_input").assertExists()

        composeTestRule.onNodeWithTag("name_input").performTextInput("Sample Sleep Time 2")

        composeTestRule.onNodeWithTag("time_select").performClick()

        onView(Matchers.instanceOf(TimePicker::class.java))
            .perform(PickerActions.setTime(9, 30))

        onView(withText("OK")).perform(click())

        composeTestRule.onNodeWithTag("time_select").assertTextContains("Start Time: 09:30")

        composeTestRule.onNodeWithTag("duration_select").performClick()

        onView(Matchers.instanceOf(TimePicker::class.java))
            .perform(PickerActions.setTime(2, 30))

        onView(withText("OK")).perform(click())

        composeTestRule.onNodeWithTag("duration_select").assertTextContains("Duration: 02:30")

        composeTestRule.onNodeWithTag("save_time").performClick()

        assertTrue(FakeToaster.toasts.contains("Please don't use overlapping sleep times"))
    }

    @Test
    fun openTimeInputDialogAndAddSleepTimeWithDuration() {
        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.navController.navigate("newCycleScreen")
        }

        composeTestRule.onNodeWithText("Add time").performClick()

        composeTestRule.onNodeWithTag("name_input").assertExists()

        composeTestRule.onNodeWithTag("name_input").performTextInput("Sample Sleep Time")

        composeTestRule.onNodeWithTag("time_select").performClick()

        onView(Matchers.instanceOf(TimePicker::class.java))
            .perform(PickerActions.setTime(8, 30))

        onView(withText("OK")).perform(click())

        composeTestRule.onNodeWithTag("time_select").assertTextContains("Start Time: 08:30")

        composeTestRule.onNodeWithTag("duration_select").performClick()

        onView(Matchers.instanceOf(TimePicker::class.java))
            .perform(PickerActions.setTime(1, 30))

        onView(withText("OK")).perform(click())

        composeTestRule.onNodeWithTag("duration_select").assertTextContains("Duration: 01:30")

        composeTestRule.onNodeWithTag("save_time").performClick()

        composeTestRule.onNodeWithText("Sample Sleep Time").assertExists()
    }
}
