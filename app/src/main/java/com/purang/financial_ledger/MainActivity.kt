package com.purang.financial_ledger

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.what2c.preferences_data_store.PreferencesDataStore
import com.purang.financial_ledger.screen.calendar.CalendarScreen
import com.purang.financial_ledger.screen.edit.EditFinancialScreen
import com.purang.financial_ledger.screen.home.HomeScreen
import com.purang.financial_ledger.screen.search.SearchScreen
import com.purang.financial_ledger.screen.setting.SettingScreen
import com.purang.financial_ledger.ui.theme.Financial_LedgerTheme
import com.purang.financial_ledger.ui.theme.blueP4
import com.purang.financial_ledger.ui.theme.blueP6
import com.purang.financial_ledger.view_model.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    sealed class BottomNavItem(
        val title: Int, val icon: Int, val screenRoute: String
    ) {
        data object Calendar : BottomNavItem(R.string.text_calendar, R.drawable.baseline_calendar_month_24, "calendar")
        data object Home : BottomNavItem(R.string.text_home, R.drawable.baseline_home_24, "home")
        data object Settings : BottomNavItem(R.string.text_settings, R.drawable.baseline_settings_24, "settings")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            //val scope = rememberCoroutineScope()

            // 다크 모드 상태를 읽어오는 flow
            val darkModeState = PreferencesDataStore.getState(context).collectAsState(initial = false)

            darkModeState.value?.let {
                Financial_LedgerTheme(darkTheme = it) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val homeViewModel: HomeViewModel = hiltViewModel()
                        MainContent(homeViewModel) // Include the MainContent directly
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainContent(homeViewModel: HomeViewModel) {
    val navController = rememberNavController()
    // 현재 라우트를 가져옵니다.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            // 특정 라우트에서는 BottomNavigation을 숨깁니다.
            if (currentRoute == MainActivity.BottomNavItem.Home.screenRoute) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("edit_financial")
                    },
                    modifier = Modifier.padding(end = 10.dp),
                ) {
                    Icon(Icons.Default.Create, contentDescription = "CreateFinancialData")
                }
            } else if (currentRoute == MainActivity.BottomNavItem.Calendar.screenRoute) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("edit_financial?type=edit")
                    },
                    modifier = Modifier.padding(end = 10.dp),
                ) {
                    Icon(Icons.Default.Create, contentDescription = "CreateFinancialData")
                }
            }
        },
        bottomBar = {
            // 특정 라우트에서는 BottomNavigation을 숨깁니다.
            if (currentRoute != "edit_todo") {
                BottomNavigation(navController = navController)
            }
        },
    ) {
        Box(Modifier.padding(it)) {
            NavigationGraph(navController = navController, homeViewModel)
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        MainActivity.BottomNavItem.Home,
        MainActivity.BottomNavItem.Calendar,
        MainActivity.BottomNavItem.Settings,
    )

    androidx.compose.material3.NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val context = LocalContext.current
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screenRoute,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = item.title.toString(),
                        modifier = Modifier.wrapContentSize(),
                    )
                },
                label = {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = context.getString(item.title),
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = blueP4,
                    //indicatorColor = MaterialTheme.colorScheme.background,
                    selectedTextColor = blueP6
                )
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(navController: NavHostController, homeViewModel: HomeViewModel) {
    NavHost(navController = navController, startDestination = MainActivity.BottomNavItem.Home.screenRoute) {
        composable(MainActivity.BottomNavItem.Calendar.screenRoute) {
            CalendarScreen(
                /*onSelectedDate = { selectedDate ->
                    // 선택된 날짜를 처리하는 코드 작성
                    Log.d("CalendarScreen", "Selected date: $selectedDate")
                    homeViewModel.fetchEventsByDate(selectedDate.toString())
                }*/
            )
        }
        composable(MainActivity.BottomNavItem.Home.screenRoute) {
            HomeScreen(navController)
        }
        composable(MainActivity.BottomNavItem.Settings.screenRoute) {
            SettingScreen()
        }

        composable(
            route = "edit_financial?type={type}&id={id}",
            arguments = listOf(
                navArgument("type") { defaultValue = "default" },
                navArgument("id") { defaultValue = "-1" }
            )
        ) { backStackEntry ->

            val type = backStackEntry.arguments?.getString("type") ?: "default"
            val id = backStackEntry.arguments?.getString("id") ?: "-1"

            EditFinancialScreen(navController, type = type, id = id)
        }

        composable(
            route = "search?text={searchText}",
            arguments = listOf(
                navArgument("searchText") { defaultValue = "" } // 키 수정
            )
        ) { backStackEntry ->
            val searchText = backStackEntry.arguments?.getString("searchText") ?: ""
            Log.d("SearchScreen", "searchText received: $searchText")
            SearchScreen(navController, searchText)
        }
    }
}