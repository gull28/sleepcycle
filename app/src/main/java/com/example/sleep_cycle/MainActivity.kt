package com.example.sleep_cycle

import AppNavHost
import TimerService
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            showPersistentNotification()

            Surface(
                modifier = Modifier.fillMaxSize().fillMaxHeight(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                MainScreen(navController)
            }
        }
    }

    private fun showPersistentNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification builder
        val builder = NotificationCompat.Builder(this, "your_channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Your Notification Title")
            .setContentText("This is a persistent notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true) // Set to true for persistent notification

        // Show the notification
        val notificationId = 1 // Unique ID for the notification
        notificationManager.notify(notificationId, builder.build())
    }
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
