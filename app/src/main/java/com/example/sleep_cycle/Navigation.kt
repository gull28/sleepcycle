import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sleep_cycle.data.repository.Preference
import com.example.sleep_cycle.data.viewmodels.PreferenceViewModel
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.screens.HomeScreen
import com.example.sleep_cycle.ui.screens.NewCycleFragment
import com.example.sleep_cycle.ui.screens.Settings
import com.example.sleep_cycle.ui.screens.SleepCycleScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    val viewModel: SleepCycleViewModel = hiltViewModel()
    val preferences: PreferenceViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "home",
        route = "main_graph",
        modifier = modifier,

        ) {
        composable(
            "home",
            enterTransition = null,
            exitTransition = null,
        ) {
            HomeScreen(navController = navController, viewModel = viewModel, preferences = preferences)
        }
        composable("sleepCycleScreen",
            enterTransition = null,
            exitTransition = null,) {
            SleepCycleScreen(navController = navController, viewModel = viewModel)
        }
        composable("newCycleScreen",
            enterTransition = null,
            exitTransition = null,) {
            NewCycleFragment(navController = navController, viewModel = viewModel)
        }
        composable("settingsScreen",
            enterTransition = null,
            exitTransition = null){
            Settings(navController = navController, preferences = preferences)
        }
    }
}