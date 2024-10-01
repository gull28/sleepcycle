import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sleep_cycle.data.viewmodels.PreferenceViewModel
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.screens.HomeScreen
import com.example.sleep_cycle.ui.screens.NewCycleFragment
import com.example.sleep_cycle.ui.screens.Settings
import com.example.sleep_cycle.ui.screens.SleepCycleScreen
import com.example.sleep_cycle.ui.theme.AppColors

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val viewModel: SleepCycleViewModel = hiltViewModel()
    val preferences: PreferenceViewModel = hiltViewModel()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),

                color = AppColors.Background

    ) {
        NavHost(
            navController = navController,
            startDestination = "home",
            route = "main_graph",
            modifier = modifier,
        ) {
            composable(
                "home",
                enterTransition = { fadeIn(animationSpec = tween(durationMillis = 300)) },
                exitTransition = { fadeOut(animationSpec = tween(durationMillis = 300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = 300)) },
                popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 300)) },
            ) {
                HomeScreen(navController = navController, viewModel = viewModel, preferences = preferences)
            }
            composable(
                "sleepCycleScreen",
                enterTransition = { fadeIn(animationSpec = tween(durationMillis = 300)) },
                exitTransition = { fadeOut(animationSpec = tween(durationMillis = 300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = 300)) },
                popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 300)) },
            ) {
                SleepCycleScreen(navController = navController, viewModel = viewModel)
            }
            composable(
                "newCycleScreen",
                enterTransition = { fadeIn(animationSpec = tween(durationMillis = 300)) },
                exitTransition = { fadeOut(animationSpec = tween(durationMillis = 300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = 300)) },
                popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 300)) },
            ) {
                NewCycleFragment(navController = navController, viewModel = viewModel)
            }
            composable(
                "settingsScreen",
                enterTransition = { fadeIn(animationSpec = tween(durationMillis = 300)) },
                exitTransition = { fadeOut(animationSpec = tween(durationMillis = 300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = 300)) },
                popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 300)) },
            ) {
                Settings(navController = navController, preferences = preferences)
            }
        }
    }
}
