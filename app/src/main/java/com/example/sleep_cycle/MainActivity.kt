package com.example.sleep_cycle

import AppNavHost
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sleep_cycle.data.db.Seed
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import com.example.sleep_cycle.data.viewmodels.PreferenceViewModel
import com.example.sleep_cycle.ui.theme.AppColors
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sleepCycleRepository: SleepCycleRepository

    private val preferenceViewModel: PreferenceViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = (AppColors.Background.toArgb())
        }

        lifecycleScope.launch {
            preferenceViewModel.isDatabaseSeededFlow.collect { isSeeded ->
                if (!isSeeded) {
                    Seed(sleepCycleRepository).seedDatabase()
                    preferenceViewModel.setDatabaseSeeded()
                }
            }
        }

        setContent {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxHeight(),
                color = AppColors.Background
            ) {
                val navController = rememberNavController()
                MainScreen(navController)
            }
        }

        if (!isServiceRunning(this, ForegroundService::class.java)) {
            val intent = Intent(this, ForegroundService::class.java)
            ContextCompat.startForegroundService(this, intent)
        }

        // Collect the notification permission state
        lifecycleScope.launch {
            preferenceViewModel.notificationPermissionFlow.collect { isGranted ->
                if (!isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestNotificationPermission()
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        lifecycleScope.launch {
            preferenceViewModel.saveNotificationPermission(isGranted)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        requestPermissionLauncher.launch(POST_NOTIFICATIONS)
    }


    override fun onDestroy() {
        super.onDestroy()
         val serviceIntent = Intent(this, ForegroundService::class.java)
         stopService(serviceIntent)
    }
}

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    activityManager?.let {
        for (service in it.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
    }
    return false
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavHostController) {
    val topBarDeniedRoutes = setOf("home")

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val shouldShowTopBar = currentRoute !in topBarDeniedRoutes

    Scaffold(
        contentColor = AppColors.Background,
        containerColor = AppColors.Background,
        topBar = {
            if (shouldShowTopBar) {
                CustomTopAppBar(navController = navController, showBackButton = true)
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding).background(AppColors.Background)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    navController: NavController,
    showBackButton: Boolean
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background, titleContentColor = AppColors.Slate),
        title = { Text(text = "") },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_custom_back_button),
                        contentDescription = "Back",
                        tint = AppColors.Slate
                    )
                }
            }
        }
    )
}
