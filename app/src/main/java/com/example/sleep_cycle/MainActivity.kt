package com.example.sleep_cycle

import AppNavHost
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import android.app.ActivityManager
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.example.sleep_cycle.ui.theme.AppColors

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
        if (true ) {
            val intent = Intent(this, ForegroundService::class.java)
            ContextCompat.startForegroundService(this, intent)
        }

    }

    override fun onDestroy() {
        super.onDestroy()

//        // Stop the foreground service when the activity is destroyed
//        val serviceIntent = Intent(this, ForegroundService::class.java)
//        stopService(serviceIntent)
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
