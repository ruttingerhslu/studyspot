package ch.hslu.mobpro.studyspot.nav

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.*

import ch.hslu.mobpro.studyspot.ui.auth.LoginScreen
import ch.hslu.mobpro.studyspot.ui.auth.RegisterScreen
import ch.hslu.mobpro.studyspot.ui.profile.ProfileScreen
import ch.hslu.mobpro.studyspot.ui.study.StudySearchScreen
import ch.hslu.mobpro.studyspot.ui.community.CommunityScreen
import ch.hslu.mobpro.studyspot.viewmodel.AuthViewModel
import ch.hslu.mobpro.studyspot.viewmodel.CommunityViewModel
import ch.hslu.mobpro.studyspot.viewmodel.StudySpotViewModel

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)

val bottomNavItems = listOf(
    BottomNavItem("study", Icons.Default.Search, "Search"),
    BottomNavItem("contacts", Icons.Default.Groups, "Contacts"),
    BottomNavItem("profile", Icons.Default.Person, "Profile"),
)

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val studySpotViewModel: StudySpotViewModel = hiltViewModel()
    val communityViewModel: CommunityViewModel = hiltViewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") {
                LoginScreen(
                    onLoginClick = { email, password ->
                        authViewModel.login(email, password,
                            onSuccess = { user ->
                                authViewModel.setCurrentUser(user)
                                navController.navigate("profile") {
                                    popUpTo("login") { inclusive = true }
                                }
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
                                navController.navigate("profile") {
                                    popUpTo("register") { inclusive = true }
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

            composable("study") { StudySearchScreen(studySpotViewModel) }
            composable("contacts") {CommunityScreen(authViewModel, communityViewModel) }
            composable("profile") { ProfileScreen(authViewModel, navController) }
        }
    }
}

