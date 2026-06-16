package com.example.otnet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.otnet.ui.OTNetApp
import com.example.otnet.ui.theme.OTNetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OTNetTheme(darkTheme = true) {
                OTNetApp()
            }
        }
    }
}
