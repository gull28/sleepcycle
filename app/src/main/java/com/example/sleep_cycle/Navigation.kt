import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sleep_cycle.data.viewmodels.SleepCycleViewModel
import com.example.sleep_cycle.ui.screens.HomeScreen
import com.example.sleep_cycle.ui.screens.NewCycleFragment
import com.example.sleep_cycle.ui.screens.SleepCycleScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(navController: NavHostController) {
    val viewModel: SleepCycleViewModel =   hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "home",
        route = "main_graph",

        ) {
        composable("home") {
            HomeScreen(navController = navController, viewModel = viewModel)
        }
        composable("detailsScreen") {
            SleepCycleScreen(navController = navController, viewModel = viewModel)
        }
        composable("newCycleScreen") {
            NewCycleFragment(navController = navController, viewModel = viewModel)
        }

    }
}