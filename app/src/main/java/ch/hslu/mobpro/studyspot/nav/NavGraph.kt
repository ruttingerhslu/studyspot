package ch.hslu.mobpro.studyspot.nav

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.*

import ch.hslu.mobpro.studyspot.ui.auth.LoginScreen
import ch.hslu.mobpro.studyspot.ui.auth.RegisterScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginClick = { email, password ->
                    authViewModel.login(email, password,
                        onSuccess = { user ->
                            navController.navigate("profile")
                        },
                        onError = {
                            Log.e("Login", "Login failed")
                        }
                    )
                },
                onRegisterNavigate = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterClick = { name, email, password ->
                    authViewModel.register(name, email, password,
                        onSuccess = {
                            // Navigate back to login screen after successful registration
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onError = { error ->
                            // Show error message
                            Log.e("Register", error)
                        }
                    )
                },
                onLoginNavigate = { navController.navigate("login") }
            )
        }
    }
}