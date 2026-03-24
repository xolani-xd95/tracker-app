package co.za.xdcodes.trackerconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import co.za.xdcodes.trackerconnect.core.ui.theme.TrackerConnectTheme
import co.za.xdcodes.trackerconnect.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TrackerConnectTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}