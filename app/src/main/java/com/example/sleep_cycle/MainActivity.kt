package com.example.sleep_cycle

import AppNavHost
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sleep_cycle.data.SleepTimeDatabaseHelper
import com.example.sleep_cycle.data.db.Seed
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndSeedDatabase()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize().fillMaxHeight(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                MainScreen(navController)
            }
        }
    }

    private fun checkAndSeedDatabase() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val lastSeedVersion = sharedPreferences.getInt("last_seed_version", 0)

        if (lastSeedVersion < SleepTimeDatabaseHelper.SEED_VERSION) {
            CoroutineScope(Dispatchers.IO).launch {
                val seed = Seed(SleepCycleRepository(baseContext))
                seed.seedDatabase()

                sharedPreferences.edit().putInt("last_seed_version", SleepTimeDatabaseHelper.SEED_VERSION).apply()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavHostController) {
    val topBarDeniedRoutes = setOf("home")

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val shouldShowTopBar = currentRoute !in topBarDeniedRoutes

    Scaffold(
        topBar = {
            if (shouldShowTopBar) {
                CustomTopAppBar(navController = navController, showBackButton = true)
            }
        }
    ) {
        AppNavHost(navController = navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    navController: NavController,
    showBackButton: Boolean
) {
    TopAppBar(
        modifier = Modifier.padding(bottom = 10.dp),
        title = { Text(text = "Your App Name") },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_custom_back_button),
                        contentDescription = "Back",

                        tint = Color.Black
                    )
                }
            }
        }
    )
}
