package com.purang.financial_ledger.screen.edit

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.financial_ledger.MainActivity
import com.purang.financial_ledger.room_db.category.CategoryEntity
import com.purang.financial_ledger.ui.theme.blueP2
import com.purang.financial_ledger.ui.theme.blueP3
import com.purang.financial_ledger.view_model.CategoryViewModel
import com.purang.financial_ledger.view_model.HomeViewModel
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditFinancialScreen(
    navController: NavController,
    type : String,
    viewModel : HomeViewModel = hiltViewModel(),
    categoryViewModel : CategoryViewModel = hiltViewModel()
) {
    val categoryDataList by categoryViewModel.categoryData.observeAsState(emptyList())

    Column (
        modifier = Modifier
            .wrapContentSize()
            .padding(10.dp)
    ) {
        LazyColumn {
            item {
                EditHeader(categoryDataList)
            }
        }

        /*Button(
            onClick = {
                if (type == "default") {
                    viewModel.addFinancialData (
                        categoryId = categoryId,
                        content = content,
                        date = date,
                        expenditure = expenditure,
                        income = income
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
            Text("추가")
        }*/
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditHeader(categoryData: List<CategoryEntity>) { //제목, 내용, 카테고리
    Column (
        modifier = Modifier.padding(20.dp)
    ) {
        EditTitle()

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(blueP2))

        EditContent()

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(blueP2))

        //EditCategory(categoryData)
        LazyRow {
            item {
                Button(onClick = { /*TODO*/ }) {
                    Column (
                        modifier = Modifier
                            .padding(5.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .wrapContentSize()
                    ) {
                        Image(Icons.Default.Add, contentDescription = "AddCategory")
                    }
                }
            }
            itemsIndexed (
                items = categoryData
            ) {_, item ->
                EditCategoryItem(item) {
                    Log.e("category", it.toString())
                }
            }
        }

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(blueP2))

        //캘린더
        EditCalendar(
            onClickCancel = { /*TODO*/ }, onClickConfirm = {}
        )
    }
}

@Composable
fun EditTitle() {
    var textContent by remember { mutableStateOf("") }
    val textColor = MaterialTheme.colorScheme.onSecondary//동적으로 색상변경

    Column {
        Text(
            text = "제목",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        OutlinedTextField(
            value = textContent,
            onValueChange = {textContent = it},
            singleLine = false,
            textStyle = TextStyle (
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun EditContent() {
    var textContent by remember { mutableStateOf("") }
    val textColor = MaterialTheme.colorScheme.onSecondary//동적으로 색상변경

    Column {
        Text(
            text = "내용",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        OutlinedTextField(
            value = textContent,
            onValueChange = {textContent = it},
            singleLine = false,
            textStyle = TextStyle (
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCalendar(
    onClickCancel: () -> Unit,
    onClickConfirm: (yyyyMMdd: String) -> Unit
) { //날짜 설정 칸?
    val selectedDate by remember {
        mutableStateOf(YearMonth.now().toString())
    }

    var isShowingDialog by remember {
        mutableStateOf(false)
    }



    DatePickerDialog(
        onDismissRequest = { onClickCancel() },
        confirmButton = { onClickConfirm(selectedDate) },
        colors = DatePickerDefaults.colors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        val datePickerState = rememberDatePickerState(
            yearRange = YearMonth.now().year ..YearMonth.now().year + 1,
            initialDisplayMode = DisplayMode.Picker,
            initialSelectedDateMillis = selectedDate.let {
                val formatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).apply {
                    // initialSelectedDateMillis는 UTC 시간을 받고 있다.
                    // 아래처럼 timeZone을 추가해서 UTC 시간으로 설정해야한다.
                    // 한국에서 실행한다면 -9시간을 적용하기 때문이다.
                    //
                    // 만약에 아래 코드가 없다면 20240228을 넘겼을 때, 안드로이드는 KST 20240228000000 으로 인식할 것이고,
                    // 이를 UTC 시간으로 변환하면서 -9시간을 적용하기 때문에 결과적으로 20240228을 넘기면 2024년 2월 27일에 선택이 되있는 문제가 발생한다.
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                formatter.parse(it)?.time
                    ?: System.currentTimeMillis() // 날짜 파싱 실패 시 현재 시간을 기본값으로 사용
            } ?: System.currentTimeMillis(), // selectedDate가 null인 경우 현재 시간을 기본값으로 사용,
        )
        DatePicker(
            state = datePickerState,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White, RoundedCornerShape(12.dp)),
                onClick = {
                onClickCancel()
            }) {
                Text(text = "취소")
            }

            Spacer(modifier = Modifier.width(5.dp))

            Button(
                modifier = Modifier
                    .wrapContentSize()
                    .background(blueP3, RoundedCornerShape(12.dp)),
                onClick = {
                datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                    val yyyyMMdd = SimpleDateFormat(
                        "yyyyMMdd",
                        Locale.getDefault()
                    ).format(Date(selectedDateMillis))

                    onClickConfirm(yyyyMMdd)
                }
            }) {
                Text(text = "확인")
            }
        }
    }
}

@Composable
fun EditCategoryItem(
    item : CategoryEntity,
    onClick : (CategoryEntity) -> Unit
) {
    Column (
        modifier = Modifier
            .padding(5.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .wrapContentSize()
            .clickable {
                onClick(item)
            }
    ) {
        Text(
            text = item.categoryName,
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun EditTransaction() { //지출, 수입

}