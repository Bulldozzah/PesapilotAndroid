package com.example.pesapilotandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pesapilotandroid.navigation.NavRoute
import com.example.pesapilotandroid.navigation.PesaPilotNavHost
import com.example.pesapilotandroid.ui.navigation.PesaPilotSideDrawer
import com.example.pesapilotandroid.ui.screens.auth.AuthViewModel
import com.example.pesapilotandroid.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Auth-only routes – drawer and top bar are hidden for these
private val authRoutes = setOf(
    NavRoute.Login::class,
    NavRoute.SignUp::class,
    NavRoute.ForgotPassword::class,
    NavRoute.ProfileSetup::class
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PesaPilotAndroidTheme {
                MainAppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val navController                = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState(initial = false)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val startDestination: NavRoute   = if (isAuthenticated) NavRoute.Dashboard else NavRoute.Login

    val isAuthScreen = currentDestination?.let { dest ->
        authRoutes.any { dest.hasRoute(it) }
    } ?: true

    val drawerState  = rememberDrawerState(DrawerValue.Closed)
    val scope        = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState      = drawerState,
        gesturesEnabled  = !isAuthScreen,
        drawerContent    = {
            ModalDrawerSheet(
                drawerContainerColor = SidebarBackground,
                drawerContentColor   = SidebarText,
                modifier             = Modifier.fillMaxSize(0.82f)
            ) {
                if (!isAuthScreen) {
                    PesaPilotSideDrawer(
                        currentDestination = currentDestination,
                        authViewModel      = authViewModel,
                        onNavigate         = { route ->
                            scope.launch { drawerState.close() }
                            navController.navigate(route) {
                                popUpTo(NavRoute.Dashboard) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        onSignOut = {
                            scope.launch { drawerState.close() }
                            authViewModel.signOut {
                                navController.navigate(NavRoute.Login) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                if (!isAuthScreen) {
                    TopAppBar(
                        title = {
                            Text(
                                text       = "Pilot-Pesa",
                                fontFamily = OutfitFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 18.sp,
                                color      = AppPrimaryText
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector        = Icons.Filled.Menu,
                                    contentDescription = "Open menu",
                                    tint               = AppPrimaryText
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = AppPrimary
                        )
                    )
                }
            }
        ) { innerPadding ->
            PesaPilotNavHost(
                navController    = navController,
                startDestination = startDestination,
                modifier         = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}
