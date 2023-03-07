@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.chapter06.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chapter06.R
import com.example.chapter06.Repository
import com.example.chapter06.navigation.Screens
import com.example.chapter06.ui.theme.Chapter06Theme
import com.example.chapter06.viewmodels.ViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    ComposeUnitConverter(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeUnitConverter(navController: NavHostController) {
    val menuItems = listOf("Item #1", "Item #2")

    // Material3: ScaffoldState 사용 안하고 바로 snackbarHostState 선언
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarCoroutineScope = rememberCoroutineScope()

    Chapter06Theme(dynamicColor = false) {
        Scaffold(topBar = {
            ComposeUnitConverterTopBar(menuItems) { s ->
                snackbarCoroutineScope.launch {
                    snackbarHostState.showSnackbar(s)
                }
            }
        }, bottomBar = {
            ComposeUnitConverterBottomBar(navController)
        }) {
            ComposeUnitConverterNavHost(
                navController = navController, modifier = Modifier.padding(it)
            )
        }
    }
}

@Composable
fun ComposeUnitConverterTopBar(menuItems: List<String>, onClick: (String) -> Unit) {
    var menuOpened by remember { mutableStateOf(false) }
    TopAppBar(title = {
        Text(text = stringResource(id = R.string.app_name))
    }, actions = {
        Box {
            IconButton(onClick = {
                menuOpened = true
            }) {
                Icon(Icons.Default.MoreVert, "")
            }
            DropdownMenu(expanded = menuOpened, onDismissRequest = {
                menuOpened = false
            }) {
                menuItems.forEachIndexed { index, s ->
                    if (index > 0) Divider()
                    DropdownMenuItem(onClick = {
                        menuOpened = false
                        onClick(s)
                    }, text = { Text(s) })
                }
            }
        }
    })
}

@Composable
fun ComposeUnitConverterBottomBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        Screens.homeScreens.forEach { screen ->
            NavigationBarItem(selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                },
                label = {
                    Text(text = stringResource(id = screen.label!!))
                },
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon!!),
                        contentDescription = stringResource(id = screen.label!!)
                    )
                },
                alwaysShowLabel = false
            )
        }
    }
}

@Composable
fun ComposeUnitConverterNavHost(
    navController: NavHostController, modifier: Modifier
) {
    val context = LocalContext.current
    val factory = ViewModelFactory(Repository(context))
    NavHost(
        navController = navController,
        startDestination = Screens.temperature,
        modifier = modifier
    ) {
        composable(Screens.temperature) {
            TemperatureConverter(
                viewModel = viewModel(factory = factory)
            )
        }
        composable(Screens.distances) {
            DistancesConverter(
                viewModel = viewModel(factory = factory)
            )
        }
    }
}