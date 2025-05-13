package ch.hslu.mobpro.studyspot.nav

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.*

import ch.hslu.mobpro.studyspot.ui.auth.LoginScreen
import ch.hslu.mobpro.studyspot.ui.auth.RegisterScreen
import ch.hslu.mobpro.studyspot.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()

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
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onError = { error ->
                            Log.e("Register", error)
                        }
                    )
                },
                onLoginNavigate = { navController.navigate("login") }
            )
        }
    }
}