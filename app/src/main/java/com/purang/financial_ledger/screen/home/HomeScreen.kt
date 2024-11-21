package com.purang.financial_ledger.screen.home

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.what2c.preferences_data_store.PreferencesDataStore
import com.example.what2c.preferences_data_store.PreferencesDataStore.getEntireExpenditure
import com.example.what2c.preferences_data_store.PreferencesDataStore.getEntireIncome
import com.purang.financial_ledger.R
import com.purang.financial_ledger.model.TotalIncomeExpenditure
import com.purang.financial_ledger.room_db.FinancialEntity
import com.purang.financial_ledger.ui.theme.Financial_LedgerTheme
import com.purang.financial_ledger.ui.theme.blueP2
import com.purang.financial_ledger.ui.theme.blueP3
import com.purang.financial_ledger.ui.theme.blueP6
import com.purang.financial_ledger.ui.theme.blueP7
import com.purang.financial_ledger.ui.theme.pink5
import com.purang.financial_ledger.view_model.HomeViewModel
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController : NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val monthFinancialData by viewModel.selectedMonthEvents.observeAsState(emptyList())
    val monthTotalIncomeExpenditure by viewModel.selectedMonthTotals.observeAsState(
        TotalIncomeExpenditure(0, 0)
    )
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    var isSearchOpen by remember {
        mutableStateOf(false)
    }
    val yearMonths by viewModel.getDistinctYearMonthsData.observeAsState(emptyList())

    var searchText by remember {
        mutableStateOf("")
    }

    /*val context = LocalContext.current
    val entireIncome by getEntireIncome(context).collectAsState(initial = "0")
    val entireExpenditure by getEntireExpenditure(context).collectAsState(initial = "0")*/

    var selectMonth by remember { mutableStateOf(YearMonth.now().toString()) }

    LaunchedEffect(Unit) {
        val currentMonth = YearMonth.now()
        viewModel.fetchEventsByMonth(currentMonth) // Fetch data for current year and month
        Log.e("monthTotal", monthTotalIncomeExpenditure.toString())
        Log.e("monthTotal2", yearMonths.toString())
    }

    LaunchedEffect(selectMonth) {
        if (selectMonth.isNotEmpty()) {
            try {
                val yearMonth = YearMonth.parse(selectMonth)
                viewModel.fetchEventsByMonth(yearMonth)
            } catch (e: Exception) {
                Log.e("HomeScreen", "Invalid month format: $selectMonth")
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // 상단 월 선택과 검색 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${YearMonth.now().year}년 ${YearMonth.now().monthValue}월",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "MonthDropDown",
                tint = blueP3,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable {
                        isBottomSheetOpen = true
                    }
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                isSearchOpen = true
            }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
        if (isSearchOpen) {
            SearchEditText(
                isSearchOpen = isSearchOpen,
                searchText = searchText,
                onTextChange = { newText ->
                    searchText = newText
                },
                onDone = {
                    isSearchOpen = it
                    navController.navigate("search?text=${searchText}")
                }
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // 총 수입, 총 지출
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(blueP3, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "이번 달 총 수입",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "+${monthTotalIncomeExpenditure.totalIncome ?: 0}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.End),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(blueP6, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "이번 달 총 지출",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "-${monthTotalIncomeExpenditure.totalExpenditure ?: 0}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.End),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // 전 월 대비 비교
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(blueP7, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    val entire = monthTotalIncomeExpenditure.totalExpenditure?.let {
                        monthTotalIncomeExpenditure.totalIncome?.minus(
                            it
                        )
                    } ?: 0L

                    Text(
                        text = if (entire > 0L) {
                            "현재 ${entire}원 남았습니다."
                        } else {
                            "현재 ${entire}원 더 사용하였습니다."
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 현재 선택된 달의 가계부 내역
            itemsIndexed(items = monthFinancialData) { _, item ->
                HomeFinancialItem(item = item) { financialItem ->
                    Log.e("item", financialItem.toString())
                    navController.navigate("edit_financial?type=edit&id=${financialItem.id}")
                }
            }
        }
    }

    if (isBottomSheetOpen) {
        val context = LocalContext.current
        Log.e("test3", "test3")
        MonthDropDownButtonBottomSheet(
            modifier = Modifier.height(160.dp),
            monthData = yearMonths.ifEmpty {
                Toast.makeText(context, "현재 달 이외에 데이터가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                emptyList()
            },
            closeSheet = { isBottomSheetOpen = false },
            onClick = {
                selectMonth = it
            }
        )
    }
}

@Composable
fun SearchEditText(
    isSearchOpen : Boolean,
    searchText: String,
    onTextChange: (String) -> Unit,
    onDone : (Boolean) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.primary//동적으로 색상변경
    val focusManager = LocalFocusManager.current
    // FocusRequester 선언
    val focusRequester = remember { FocusRequester() }

    // 가상 키보드가 나타나도록 포커스 요청
    LaunchedEffect(isSearchOpen) {
        focusRequester.requestFocus()
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onTextChange,
            singleLine = false,
            textStyle = TextStyle (
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
                .focusRequester(focusRequester),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = blueP3
            ),
            keyboardOptions = KeyboardOptions(
                //keyboardType = KeyboardType.Number, // 숫자 전용 키보드
                imeAction = ImeAction.Done // 완료 버튼 표시
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onDone(false)
                }
            ),
            label = {
                Text("제목과 내용으로 검색해주세요")
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthDropDownButtonBottomSheet(
    modifier: Modifier = Modifier,
    closeSheet: () -> Unit,
    monthData: List<String?>,
    onClick: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    ModalBottomSheet(
        onDismissRequest = closeSheet,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = null,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = modifier
                .padding(10.dp)
                .height(screenHeight / 2) // 화면 높이의 절반으로 설정
        ) {
            if (monthData.isEmpty()) {
                Text(
                    text = "데이터가 있는 월이 존재하지 않습니다.",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            } else {
                LazyColumn {
                    itemsIndexed(
                        items = monthData
                    ) { _, item ->
                        item?.ifEmpty { "Invalid Data" }?.let {
                            Text(
                                text = it,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable {
                                        onClick(item)
                                        closeSheet()
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeFinancialItem(
    item: FinancialEntity,
    onItemClick: (FinancialEntity) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Financial_LedgerTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                .clickable {
                    onItemClick(item) // handle click and pass item data
                }
                .background(
                    color = if (expanded) MaterialTheme.colorScheme.primary//열릴때
                    else MaterialTheme.colorScheme.tertiary, //기본
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Display the financial data
                    Text(
                        text = item.title ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        //color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "수입 +${item.income?.toString() ?: "0"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSecondary
                            //color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "지출 -${item.expenditure?.toString() ?: "0"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary
                            //color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Date: ${item.date}",
                    style = MaterialTheme.typography.bodySmall,
                    //color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                // Show additional content when expanded
                if (expanded) {
                    Text(
                        text = item.content ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        //color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Toggle button to expand/collapse
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Show less" else "Show more"
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun preview() {
    val create = FinancialEntity(
        title = "Monthly Rent",
        content = "Paid rent for November. Includes water and electricity bills.",
        date = "2024-11-01",
        categoryId = 1L,
        expenditure = 150000L,
        income = 0L
    )
    Financial_LedgerTheme {
        HomeFinancialItem(item = create, onItemClick =  {

        })
    }
}