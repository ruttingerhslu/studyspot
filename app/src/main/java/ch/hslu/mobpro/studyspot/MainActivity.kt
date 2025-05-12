package ch.hslu.mobpro.studyspot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import ch.hslu.mobpro.studyspot.ui.theme.StudySpotTheme
import ch.hslu.mobpro.studyspot.nav.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudySpotTheme {
                NavGraph()
            }
        }
    }
}
