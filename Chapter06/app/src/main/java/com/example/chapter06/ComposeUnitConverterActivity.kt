@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.chapter06

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chapter06.screens.ComposeUnitConverterScreen
import com.example.chapter06.screens.DistancesConverter
import com.example.chapter06.screens.TemperatureConverter
import com.example.chapter06.ui.theme.Chapter06Theme
import com.example.chapter06.viewmodels.ViewModelFactory
import kotlinx.coroutines.launch

class ComposeUnitConverterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ViewModelFactory(Repository(applicationContext))
        setContent {
            ComposeUnitConverter(factory)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeUnitConverter(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val menuItems = listOf("Item #1", "Item #2")
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarCoroutineScope = rememberCoroutineScope()
    Chapter06Theme {
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
                navController = navController, factory = factory, modifier = Modifier.padding(it)
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
        ComposeUnitConverterScreen.screens.forEach { screen ->
            NavigationBarItem(selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                },
                label = {
                    Text(text = stringResource(id = screen.label))
                },
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = stringResource(id = screen.label)
                    )
                },
                alwaysShowLabel = false
            )
        }
    }
}

@Composable
fun ComposeUnitConverterNavHost(
    navController: NavHostController, factory: ViewModelProvider.Factory?, modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ComposeUnitConverterScreen.route_temperature,
        modifier = modifier
    ) {
        composable(ComposeUnitConverterScreen.route_temperature) {
            TemperatureConverter(
                viewModel = viewModel(factory = factory)
            )
        }
        composable(ComposeUnitConverterScreen.route_distances) {
            DistancesConverter(
                viewModel = viewModel(factory = factory)
            )
        }
    }
}