import android.os.Build
import androidx.annotation.RequiresApi
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
        modifier = modifier
        ) {
        composable("home") {
            HomeScreen(navController = navController, viewModel = viewModel, preferences = preferences)
        }
        composable("detailsScreen") {
            SleepCycleScreen(navController = navController, viewModel = viewModel)
        }
        composable("newCycleScreen") {
            NewCycleFragment(navController = navController, viewModel = viewModel)
        }
        composable("settingsScreen"){
            Settings(navController = navController, preferences = preferences)
        }

    }
}