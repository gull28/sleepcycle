package com.example.sleep_cycle

import AppNavHost
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import com.example.sleep_cycle.data.viewmodels.PreferenceViewModel
import com.example.sleep_cycle.ui.theme.AppColors
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val preferenceViewModel: PreferenceViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = (AppColors.Background.toArgb())
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

    private fun requestNotificationPermission() {
        requestPermissionLauncher.launch(POST_NOTIFICATIONS)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Stop the foreground service when the activity is destroyed, if needed
        // val serviceIntent = Intent(this, ForegroundService::class.java)
        // stopService(serviceIntent)
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
        topBar = {
            if (shouldShowTopBar) {
                CustomTopAppBar(navController = navController, showBackButton = true)
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
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
