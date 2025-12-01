package cz.patrik.stanko.climbinglogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cz.patrik.stanko.climbinglogger.data.ServiceLocator
import cz.patrik.stanko.climbinglogger.ui.ClimbingLoggerApp
import cz.patrik.stanko.climbinglogger.ui.ClimbingLoggerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            ClimbingLoggerTheme {
                ClimbingLoggerApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    ClimbingLoggerTheme {
        ClimbingLoggerApp()
    }
}
