package com.example.wtwear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wtwear.ui.theme.WTWearTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.delay
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme


val supabase = createSupabaseClient(
    supabaseUrl = "https://lwalmarsdlzmzzuvseus.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imx3YWxtYXJzZGx6bXp6dXZzZXVzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDEwMTA2NzAsImV4cCI6MjAxNjU4NjY3MH0.pceUzcBcSClI84-1hOV9uvwwGqPTMYcU9Go6mGKaMsQ"
) {
    install(Postgrest)
}
//val url = BuildConfig.SUPABASE_URL
//val apiKey = BuildConfig.SUPABASE_ANON_KEY
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppContent()
        }
    }

    @Composable
    fun LoadingScreen(loadingState: MutableState<Boolean> = remember { mutableStateOf(true) }) {
        if (loadingState.value) {
            // Show the loading screen
            ConstraintLayout(
                modifier = Modifier.fillMaxSize(),
            ) {
                val (loadingImageView) = createRefs()

                Image(
                    painter = painterResource(id = R.drawable.load_screen),
                    contentDescription = null,
                    modifier = Modifier
                        .constrainAs(loadingImageView) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .fillMaxSize()
                )
            }
        } else {
            // Once the background task is complete, switch to the main content
            AppContent()
        }
    }

    @Composable
    @Preview
    fun AppContentPreview() {
        AppContent()
    }

    @Composable
    fun AppContent() {
        val loadingState = remember { mutableStateOf(true) }

        LaunchedEffect(true) {
            // Simulate some background task using coroutines
            delay(3000) // Simulate 3 seconds of background work
            loadingState.value =
                false // Set loadingState to false after the background work is done
        }

        LoadingScreen(loadingState)
    }
}