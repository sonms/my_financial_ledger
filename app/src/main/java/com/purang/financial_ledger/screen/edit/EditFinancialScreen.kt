package com.purang.financial_ledger.screen.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.purang.financial_ledger.MainActivity

@Composable
fun EditFinancialScreen(
    navController: NavController,
    type : String,

) {

    /*Button(
        onClick = {
            if (type == "default") {
                todoViewModel.addTodo(
                    title = textTitle,
                    content = textContent
                )
                navController.navigate(MainActivity.BottomNavItem.Home.screenRoute)
            } else {
                if (selectedDate != null) {
                    todoViewModel.addCalendarTodo(
                        title = textTitle,
                        content = textContent,
                        eventDate = selectedDate
                    )
                }
                navController.navigate(MainActivity.BottomNavItem.Calendar.screenRoute)
            }
        },
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 20.dp)
    ) {
        Text("+")
    }*/
}