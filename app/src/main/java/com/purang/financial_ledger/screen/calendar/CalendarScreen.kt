package com.purang.financial_ledger.screen.calendar

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.financial_ledger.loading.LoadingState
import com.purang.financial_ledger.room_db.FinancialEntity
import com.purang.financial_ledger.screen.chart.numberFormat
import com.purang.financial_ledger.screen.home.DeleteItemDialog
import com.purang.financial_ledger.screen.home.HomeFinancialItem
import com.purang.financial_ledger.ui.theme.blueExDark
import com.purang.financial_ledger.ui.theme.blueExLight
import com.purang.financial_ledger.ui.theme.blueP3
import com.purang.financial_ledger.ui.theme.blueP4
import com.purang.financial_ledger.ui.theme.pink6
import com.purang.financial_ledger.ui.theme.redInDark
import com.purang.financial_ledger.ui.theme.redInLight
import com.purang.financial_ledger.view_model.HomeViewModel
import com.uuranus.schedule.calendar.compose.ScheduleCalendar
import com.uuranus.schedule.calendar.compose.ScheduleCalendarDefaults
import com.uuranus.schedule.calendar.compose.ScheduleData
import com.uuranus.schedule.calendar.compose.ScheduleDate
import com.uuranus.schedule.calendar.compose.ScheduleInfo
import kotlinx.coroutines.launch
import java.time.YearMonth

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val monthData by homeViewModel.sortedMonthEvents.observeAsState(emptyList())

    val calendarList by remember(monthData) {
        derivedStateOf {
            monthData
                .groupBy { it.date } // date 기준으로 그룹화
                .mapKeys { (date, _) ->
                    // ScheduleDate 객체로 변환 (ScheduleDate가 날짜를 기반으로 생성된다고 가정)
                    val parts = date!!.split("-")
                    ScheduleDate.create(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
                }
                .mapValues { (_, items) ->
                    // 그룹화된 데이터를 합산하여 ScheduleInfo 생성
                    val totalA = items.sumOf { it.income ?: 0L }
                    val totalB = items.sumOf { it.expenditure ?: 0L }

                    // ScheduleData 리스트 생성
                    val scheduleDataList = listOf(
                        ScheduleData(
                            title = "+${numberFormat(totalA)}",
                            color = redInLight, // 임의의 색상
                            detail = "Income: $totalA, Expenditure: $totalB"
                        ),
                        ScheduleData(
                            title = "-${numberFormat(totalB)}",
                            color = blueExLight, // 임의의 색상
                            detail = "Income: $totalA, Expenditure: $totalB"
                        )
                    )

                    // ScheduleInfo 반환
                    ScheduleInfo(
                        isCheckNeeded = false,
                        schedules = scheduleDataList
                    )
                }
        }
    }

    /*val calendarAmountData by remember(monthData) {
        derivedStateOf {
            monthData
                .groupBy { it.date } // date 기준으로 그룹화
                .map { (date, items) ->
                    // 그룹화된 데이터를 합산하여 새로운 데이터 생성
                    val totalA = items.sumOf { it.income ?: 0L }
                    val totalB = items.sumOf { it.expenditure ?: 0L }
                    //CalendarData(date = date, a = totalA, b = totalB)
                }
        }
    }*/

    val selectCalendarData by homeViewModel.clickCalendarData.observeAsState(emptyList())

    var selectDate by remember {
        mutableStateOf("")
    }

    var isDeleteDialogOpen by remember {
        mutableStateOf(false)
    }

    var deleteItem by remember {
        mutableStateOf<FinancialEntity?>(null)
    }

    val selectCategoryId by remember {
        mutableStateOf<Long?>(null)
    }

    var pageChangeDate by remember {
        mutableStateOf(YearMonth.now().toString())
    }

    LaunchedEffect(Unit) {
        val yearMonth = YearMonth.parse(pageChangeDate)
        homeViewModel.fetchEventsByMonth(yearMonth)
        homeViewModel.fetchCategoryId(selectCategoryId)
    }

    LaunchedEffect(selectDate) {
        homeViewModel.fetchCalendarDate(selectDate)
    }

    LaunchedEffect(pageChangeDate) {
        val yearMonth = YearMonth.parse(pageChangeDate)
        homeViewModel.fetchEventsByMonth(yearMonth)
        homeViewModel.fetchCategoryId(selectCategoryId)
        LoadingState.hide()
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        LazyColumn(state = listState) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight()// 명시적으로 높이를 설정합니다.
                ) {
                    ScheduleCalendar(
                        modifier = Modifier.fillMaxSize(), // 내부에서 명시적으로 채움
                        initialDate = ScheduleDate.create(YearMonth.now().year, YearMonth.now().monthValue, 1),
                        schedules = calendarList,
                        /*mapOf(
                            ScheduleDate.create(2024, 11, 1) to ScheduleInfo(
                                isCheckNeeded = false,
                                schedules = listOf(
                                    ScheduleData(
                                        title = "schedule1",
                                        color = blueP3,
                                        detail = "Schedule Info 1",
                                    )
                                )
                            ),
                        ),*/
                        isMondayFirst = false,
                        calendarColors = ScheduleCalendarDefaults.colors(
                            lightColors = ScheduleCalendarDefaults.defaultLightColors().copy(
                                dayOfWeeks = blueP4,
                                saturdayColor = blueExLight,
                                sundayColor = redInLight,
                                dateScheduleTextColor = Color.Red,
                                dateColor = blueP4,
                                todayIndicatorColor = blueP4,
                            ),
                            darkColors = ScheduleCalendarDefaults.defaultDarkColors().copy(
                                dayOfWeeks = blueP4,
                                saturdayColor = blueExLight,
                                sundayColor = redInLight,
                                todayIndicatorColor = blueP4,
                                dateScheduleTextColor = Color.White,
                                dateColor = blueP4
                            ),
                        ),
                        onDayClick = {
                            val formattedMonth = String.format("%02d", it.month)
                            val formattedDate = String.format("%02d", it.date)
                            selectDate = "${it.year}-${formattedMonth}-${formattedDate}"

                            coroutineScope.launch {
                                listState.scrollToItem(1) // stickyHeader는 두 번째 항목 (index 1)
                            }
                        },
                        onPageChanged = {
                            LoadingState.show()
                            val formattedMonth = String.format("%02d", it.month)
                            pageChangeDate = "${it.year}-${formattedMonth}"
                        }
                    )
                }
            }

            stickyHeader {
                DataHeader(selectDate)
            }

            if (selectCalendarData.isNotEmpty()) {
                itemsIndexed(
                    items = selectCalendarData
                ) { _, item ->
                    HomeFinancialItem(
                        item = item,
                        onItemClick = {
                            navController.navigate("edit_financial?type=edit&id=${item.id}")
                        },
                        onLongClick = {
                            isDeleteDialogOpen = !isDeleteDialogOpen
                            deleteItem = it
                        }
                    )
                }
            }
        }
    }

    if (isDeleteDialogOpen) {
        DeleteItemDialog(
            item = deleteItem,
            onCancelClick = {
                isDeleteDialogOpen = !isDeleteDialogOpen
            },
            onConfirmClick = {
                if (it != null) {
                    isDeleteDialogOpen = !isDeleteDialogOpen
                    homeViewModel.deleteFinancialData(it)
                }
            }
        )
    }
}

@Composable
fun DataHeader(selectData : String) {
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
        Text(
            text = "선택한 날짜 : $selectData",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}