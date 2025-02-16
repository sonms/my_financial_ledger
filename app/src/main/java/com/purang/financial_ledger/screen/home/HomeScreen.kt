package com.purang.financial_ledger.screen.home

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.financial_ledger.R
import com.purang.financial_ledger.model.TotalIncomeExpenditure
import com.purang.financial_ledger.room_db.FinancialEntity
import com.purang.financial_ledger.room_db.category.CategoryEntity
import com.purang.financial_ledger.screen.chart.numberFormat
import com.purang.financial_ledger.ui.theme.Financial_LedgerTheme
import com.purang.financial_ledger.ui.theme.blueD
import com.purang.financial_ledger.ui.theme.blueExLight
import com.purang.financial_ledger.ui.theme.blueP3
import com.purang.financial_ledger.ui.theme.blueP5
import com.purang.financial_ledger.ui.theme.blueP6
import com.purang.financial_ledger.ui.theme.redD
import com.purang.financial_ledger.ui.theme.redInDark
import com.purang.financial_ledger.view_model.CategoryViewModel
import com.purang.financial_ledger.view_model.HomeViewModel
import java.net.URLEncoder
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController : NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val monthFinancialData by viewModel.sortedMonthEvents.observeAsState(emptyList())

    val monthTotalIncomeExpenditure by viewModel.selectedMonthTotals.observeAsState(
        TotalIncomeExpenditure(0, 0)
    )

    val categoryAllData by categoryViewModel.categoryData.observeAsState(emptyList())

    var isBottomSheetOpen by remember { mutableStateOf(false) }
    var isSearchOpen by remember {
        mutableStateOf(false)
    }
    var isFilterOpen by remember {
        mutableStateOf(false)
    }
    var isDeleteDialogOpen by remember {
        mutableStateOf(false)
    }
    var deleteItem by remember {
        mutableStateOf<FinancialEntity?>(null)
    }

    val yearMonths by viewModel.getDistinctYearMonthsData.observeAsState(emptyList())

    var searchText by remember {
        mutableStateOf("")
    }
    var selectCategoryId by remember {
        mutableStateOf<Long?>(null)
    }

    /*val context = LocalContext.current
    val entireIncome by getEntireIncome(context).collectAsState(initial = "0")
    val entireExpenditure by getEntireExpenditure(context).collectAsState(initial = "0")*/

    var selectMonth by rememberSaveable { mutableStateOf(YearMonth.now().toString()) }

    LaunchedEffect(Unit) {
        val currentMonth = YearMonth.now()
        viewModel.fetchEventsByMonth(currentMonth) // Fetch data for current year and month
        viewModel.fetchCategoryId(selectCategoryId)
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
            val parsedYearMonth = YearMonth.parse(selectMonth) // "2024-02"를 YearMonth로 변환
            val formattedText = "${parsedYearMonth.year}년 ${parsedYearMonth.monthValue}월" // 원하는 형식으로 변환
            Text(
                text = formattedText,
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
                isFilterOpen = !isFilterOpen
            }) {
                Icon(painterResource(id = R.drawable.baseline_filter_alt_24), contentDescription = "Filter")
            }

            IconButton(onClick = {
                isSearchOpen = !isSearchOpen
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
                    val encodedSearchText = URLEncoder.encode(searchText, "UTF-8")
                    navController.navigate("search?text=$encodedSearchText")
                }
            )
        }

        if (isFilterOpen) {
            FilterUI(
                onClick = {
                    if (it == "오름차순") {
                        viewModel.toggleSortOrder(true)
                    } else {
                        viewModel.toggleSortOrder(false)
                    }
                },
                categoryData = categoryAllData,
                onCategoryClick = {
                    selectCategoryId = it
                    viewModel.fetchCategoryId(it)
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
                            .background(blueP6, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "이번 달 총 수입",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "+${numberFormat(monthTotalIncomeExpenditure.totalIncome ?: 0)}원",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.End),
                            color = redInDark
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
                            text = "-${numberFormat(monthTotalIncomeExpenditure.totalExpenditure ?: 0)}원",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.End),
                            color = blueExLight
                        )
                    }
                }
            }

            // 전 월 대비 비교
            item {
                val entire = monthTotalIncomeExpenditure.totalExpenditure?.let {
                    monthTotalIncomeExpenditure.totalIncome?.minus(
                        it
                    )
                } ?: 0L
                val background = if (entire >= 0) {
                    redD
                } else {
                    blueD
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(background, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (entire > 0L) {
                            "현재 ${numberFormat(entire)}원 남았습니다."
                        } else {
                            "현재 ${numberFormat(entire)}원 더 사용하였습니다."
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 현재 선택된 달의 가계부 내역
            itemsIndexed(items = monthFinancialData) { _, item ->
                HomeFinancialItem(
                    item = item,
                    onItemClick = { financialItem ->
                        navController.navigate("edit_financial?type=edit&id=${financialItem.id}")
                    },
                    onLongClick = {
                        isDeleteDialogOpen = !isDeleteDialogOpen
                        deleteItem = it
                    }
                )
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
                        viewModel.deleteFinancialData(it)
                    }
                }
            )
        }
    }

    if (isBottomSheetOpen) {
        val context = LocalContext.current
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterUI(
    onClick: (String) -> Unit, // 선택된 필터 값을 전달
    categoryData : List<CategoryEntity>,
    onCategoryClick : (Long?) -> Unit
) {
    var selectedDesc by remember { mutableStateOf(false) }
    var selectedAsc by remember { mutableStateOf(false) }

    var selectedCategoryId by remember {
        mutableStateOf<Long?>(null)
    }

    val textList = listOf("오름차순", "내림차순")

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
        ) {
            Text( text = "날짜 정렬", modifier = Modifier
                .padding(end = 8.dp)
                .align(Alignment.CenterVertically))

            LazyRow {
                itemsIndexed(textList) { index, text ->
                    val isSelected = if (index == 0) selectedAsc else selectedDesc
                    FilterChip(
                        modifier = Modifier.padding(end = 10.dp),
                        selected = isSelected,
                        onClick = {
                            if (index == 0) {
                                // "오름차순" 선택 시
                                selectedAsc = true
                                selectedDesc = false
                                onClick("오름차순")
                            } else {
                                // "내림차순" 선택 시
                                selectedAsc = false
                                selectedDesc = true
                                onClick("내림차순")
                            }
                        },
                        label = { Text(text) },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Selected",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else null
                    )
                }
            }
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
        ) {
            Text(
                text = "카테고리 필터", modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically)
            )

            LazyRow {
                itemsIndexed(categoryData) { _, item ->
                    FilterChip(
                        modifier = Modifier.padding(end = 10.dp),
                        selected = item.id == selectedCategoryId,
                        onClick = {
                            selectedCategoryId = if (selectedCategoryId != item.id) {
                                item.id
                            } else {
                                null
                            }
                            onCategoryClick(selectedCategoryId)
                        },
                        label = { Text(item.categoryName) },
                        /*leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Selected",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else null*/
                    )
                }
            }
        }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeFinancialItem(
    item: FinancialEntity,
    onItemClick: (FinancialEntity) -> Unit,
    onLongClick: (FinancialEntity) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    if (item.selectColor == 0) {
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
                    .combinedClickable(
                        onClick = {
                            onItemClick(item) // handle click and pass item data
                        },
                        onLongClick = {
                            onLongClick(item)
                        },
                    )
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
                                text = "수입 +${numberFormat(item.income ?: 0L)}원",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSecondary
                                //color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "지출 -${numberFormat(item.expenditure ?: 0L)}원",
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

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Show less" else "Show more"
                    )
                }
            }
        }
    } else {
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
                .combinedClickable(
                    onClick = {
                        onItemClick(item) // handle click and pass item data
                    },
                    onLongClick = {
                        onLongClick(item)
                    },
                )
                .background(
                    color = item.color,
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

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "수입 +${numberFormat(item.income ?: 0L)}원",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSecondary
                            //color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "지출 -${numberFormat(item.expenditure ?: 0L)}원",
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

            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Show less" else "Show more"
                )
            }
        }
    }
}

@Composable
fun DeleteItemDialog(
    item : FinancialEntity?,
    onConfirmClick : (FinancialEntity?) -> Unit,
    onCancelClick : () -> Unit
) {
    Dialog(
        onDismissRequest = { onCancelClick() }
    ) {
        Card (
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .padding(10.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 10.dp),
                text = "정말 삭제하시겠습니까?",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Row (
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(20.dp)
            ) {
                Button(
                    modifier = Modifier.padding(end = 5.dp),
                    onClick = { onCancelClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueP5, // 버튼 배경색
                        contentColor = Color.White // 텍스트 색상 설정
                    ),
                ) {
                    Text(
                        text = "취소",
                        color = Color.White
                    )
                }

                Button(
                    onClick = { onConfirmClick(item) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueP5, // 버튼 배경색
                        contentColor = Color.White // 텍스트 색상 설정
                    ),
                ) {
                    Text(
                        text = "확인",
                        color = Color.White
                    )
                }
            }
        }
    }
}

/*
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
}*/
