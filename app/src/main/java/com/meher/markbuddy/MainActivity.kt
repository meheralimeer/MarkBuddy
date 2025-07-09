package com.meher.markbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.meher.markbuddy.di.appModule
import com.meher.markbuddy.ui.theme.MarkBuddyTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }
        setContent {
            MarkBuddyTheme {
                MarkBuddyApp()
            }
        }
    }
}