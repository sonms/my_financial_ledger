package com.purang.financial_ledger.screen.edit

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import com.purang.financial_ledger.MainActivity
import com.purang.financial_ledger.R
import com.purang.financial_ledger.loading.DialogState
import com.purang.financial_ledger.room_db.category.CategoryEntity
import com.purang.financial_ledger.ui.theme.Pink80
import com.purang.financial_ledger.ui.theme.Purple80
import com.purang.financial_ledger.ui.theme.blueP2
import com.purang.financial_ledger.ui.theme.blueP3
import com.purang.financial_ledger.ui.theme.blueP5
import com.purang.financial_ledger.ui.theme.blueP6
import com.purang.financial_ledger.view_model.CategoryViewModel
import com.purang.financial_ledger.view_model.HomeViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditFinancialScreen(
    navController: NavController,
    type: String,
    id : String?,
    viewModel: HomeViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    Log.e("Edit", "$type $id")
    val financialItem by viewModel.selectedFinancialItem.collectAsState()
    if (id != null) {
        LaunchedEffect(id) {
            viewModel.setSelectedId(id.toLongOrNull())
            Log.e("launchedItemByid",financialItem.toString())
        }
    }

    val categoryDataList by categoryViewModel.categoryData.observeAsState(emptyList())
    // ViewModel에서 데이터 관찰

    var isShowingCategoryDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(YearMonth.now().toString()) }

    var textTitle by remember {
        mutableStateOf("")
    }
    var textContent by remember {
        mutableStateOf("")
    }

    var editIncome by remember {
        mutableStateOf("")
    }
    var editExpenditure by remember {
        mutableStateOf("")
    }

    if (type != "default") {
        LaunchedEffect(financialItem) {
            // categoryViewModel에서 categoryId로 카테고리 데이터 가져오기
            categoryViewModel.getCategoryItemById(financialItem?.categoryId)

            // 상태값 설정
            textTitle = financialItem?.title ?: ""
            textContent = financialItem?.content ?: ""
            selectedCategory = categoryDataList.find { it.id == financialItem?.categoryId }
            editIncome = financialItem?.income.toString()
            editExpenditure = financialItem?.expenditure.toString()
            selectedDate = financialItem?.date
        }
    }

    /*var createFinancialData by remember {
        mutableStateOf<FinancialEntity?>(null)
    }*/

    /*if (financialItem != null) {
        createFinancialData = financialItem
        Log.e("financialSelected",createFinancialData.toString())
    }*/




    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            item {
                EditTitle(
                    textTitle = textTitle,
                    onTextChange = { newText ->
                        textTitle = newText
                        Log.e("editTest", textTitle)
                    }
                )
            }

            item {
                EditContent(
                    textContent = textContent,
                    onTextChange = { newText ->
                        textContent = newText
                        //createFinancialData?.content = textContent
                        Log.e("editTest", textContent)
                    }
                )
            }

            item {
                Text(modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold,
                    text = "카테고리", fontSize = 18.sp)
                LazyRow (
                    horizontalArrangement = Arrangement.Start, // 전체 LazyRow의 아이템을 중앙 정렬
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(10.dp) // LazyRow가 전체 너비를 차지하도록 설정
                ) {
                    item {
                       
                        IconButton(onClick = { isShowingCategoryDialog = true }) {
                            Column(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .background(blueP3, RoundedCornerShape(8.dp))
                                    .wrapContentSize()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "AddCategory")
                            }
                        }
                    }

                    itemsIndexed(
                        items = categoryDataList
                    ) { _, item ->
                        EditCategoryItem(
                            item,
                            onClick = {
                                // 클릭 시 선택된 카테고리 설정
                                selectedCategory = item
                                //createFinancialData?.categoryId = item.id
                                Log.e("Selected Category", item.toString())
                            },
                            onLongClick = {
                                // 길게 클릭 시 카테고리 수정, 삭제 등 다른 동작을 추가 가능
                                Log.e("Long Click", "Category ${item.categoryName} long clicked")
                                categoryViewModel.deleteCategory(it)
                            }
                        )
                    }
                }
            }

            item {
                // Date Picker
                EditCalendar(
                    selectDate = selectedDate,
                    onClickCancel = { /*TODO*/ }
                ) {//on click confirm
                    val parsedDate = SimpleDateFormat("yyyyMMdd", Locale.KOREAN).parse(it)
                    selectedDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).format(parsedDate ?: Date())
                }
            }

            item {
                EditTransaction(
                    textContentIncome = editIncome,
                    textContentExpenditure = editExpenditure,

                    incomeOnTextChanged = {newText ->
                        editIncome = newText
                        Log.e("editTest", editIncome)
                    },
                    expenditureOnTextChanged = {newText ->
                        editExpenditure = newText
                        Log.e("editTest", editExpenditure)
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End // Row 내에서 요소를 오른쪽 끝에 배치
                ) {
                    Button(
                        modifier = Modifier
                            .padding(top = 20.dp, end = 10.dp),
                        onClick = {
                            //추가
                            if (type == "default") {
                                viewModel.addFinancialData(
                                    categoryId = selectedCategory?.id,
                                    title = textTitle,
                                    content = textContent,
                                    date = selectedDate,
                                    expenditure = editExpenditure.toLongOrNull() ?: 0L,
                                    income = editIncome.toLongOrNull() ?: 0L
                                )
                                //Log.e("defaultAdd", createFinancialData.toString())
                                navController.navigate(
                                    MainActivity.BottomNavItem.Home.screenRoute,
                                    NavOptions.Builder()
                                        .setLaunchSingleTop(true)  // 이미 존재하는 화면을 재사용하지 않음
                                        .setPopUpTo(navController.graph.startDestinationId, true)  // 이전 화면을 스택에서 제거
                                        .build()
                                )
                            } else {
                                //수정
                                Log.e("editFinancialData", "수정")
                                viewModel.updateFinancialData(
                                    id = financialItem?.id!!,
                                    categoryId = selectedCategory?.id,
                                    title = textTitle,
                                    content = textContent,
                                    date = selectedDate,
                                    expenditure = editExpenditure.toLongOrNull() ?: 0L,
                                    income = editIncome.toLongOrNull() ?: 0L
                                )
                                navController.navigate(
                                    MainActivity.BottomNavItem.Home.screenRoute,
                                    NavOptions.Builder()
                                        .setLaunchSingleTop(true)  // 이미 존재하는 화면을 재사용하지 않음
                                        .setPopUpTo(navController.graph.startDestinationId, true)  // 이전 화면을 스택에서 제거
                                        .build()
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = blueP5, // 버튼 배경색
                            contentColor = Color.White // 텍스트 색상 설정
                        ),
                    ) {
                        Text("완료")
                    }
                }
            }
        }

        // Show category creation dialog if enabled
        if (isShowingCategoryDialog) {
            EditCategoryCreateDialog(
                onConfirmClick = { newCategory ->
                    categoryViewModel.addCategory(newCategory)
                    isShowingCategoryDialog = false
                },
                onCancelClick = { isShowingCategoryDialog = false }
            )
        }


    }
}

@Composable
fun EditTitle(
    textTitle: String,
    onTextChange: (String) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.primary // 동적으로 색상변경
    /*var isError by rememberSaveable { mutableStateOf(false) }

    fun validate(text: String?) {
        isError = text.isNullOrEmpty()
    }*/

    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "제목",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        OutlinedTextField(
            value = textTitle,
            onValueChange = {
                onTextChange(it)
                //validate(it)
            },
            singleLine = false,
            textStyle = TextStyle(
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            ),
            /*trailingIcon = {
                if (isError)
                    Icon(Icons.Filled.Info, "빈 칸을 채워주세요!", tint = MaterialTheme.colorScheme.error)
            },*/
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = blueP3
            )
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(blueP2)
        )
    }
}

@Composable
fun EditContent(
    textContent: String,
    onTextChange: (String) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.primary//동적으로 색상변경

    Column (
        modifier = Modifier
            .fillMaxWidth()
        
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "내용",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        OutlinedTextField(
            value = textContent,
            onValueChange = onTextChange,
            singleLine = false,
            textStyle = TextStyle (
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = blueP3
            )
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(blueP2)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCalendar(
    selectDate : String?,
    onClickCancel: () -> Unit,
    onClickConfirm: (yyyyMMdd: String) -> Unit
) { //날짜 설정 칸?
    var selectedDate by remember {
        mutableStateOf(selectDate ?: YearMonth.now().toString())
    }
    val isDialogShowing by DialogState.isShowing.collectAsState()


    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 10.dp),
            text = "날짜",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        /*OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            singleLine = false,
            label = { Text("Label as hint") }, // 아웃라인에 걸쳐짐
            placeholder = { Text("Placeholder hint") }, // 안에있다가 글쓰면 사라짐
            textStyle = TextStyle (
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )*/
        Row(
            modifier = Modifier
                .background(blueP3, RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            val parsedDate = SimpleDateFormat("yyyyMMdd", Locale.KOREAN).parse(selectedDate)
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).format(parsedDate ?: Date()),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.weight(1f))


            Image(
                painterResource(R.drawable.baseline_calendar_month_24), contentDescription = "AddDate",
                modifier = Modifier
                    .clickable {
                        DialogState.show()
                    }
                    .padding(end = 10.dp)
            )
        }
    }




    if (isDialogShowing) {
        DatePickerDialog(
            modifier = Modifier.padding(20.dp),
            onDismissRequest = { onClickCancel() },
            confirmButton = { },
            colors = DatePickerDefaults.colors(
                /*containerColor: 전체 배경 색
                titleContentColor: 제목의 텍스트 색
                headlineContentColor: 헤드라인 텍스트 색
                weekdayContentColor: 요일 텍스트 색
                dayContentColor: 일반 날짜 텍스트 색
                todayContentColor: 오늘 날짜 텍스트 색
                selectedDayContentColor: 선택된 날짜 텍스트 색
                disabledDayContentColor: 비활성화된 날짜 텍스트 색
                selectedDayContainerColor: 선택된 날짜 배경 색
                todayDateBorderColor: 오늘 날짜 테두리 색*/
                containerColor = Color.White,
                weekdayContentColor = Color.Black,
                titleContentColor = Color.Black,
                disabledDayContentColor = blueP2,
                dayContentColor = Color.Black, //날짜색 텍스트
                todayDateBorderColor = blueP3, //오늘날짜색
                selectedDayContainerColor = blueP3
            ),
            shape = RoundedCornerShape(6.dp)
        ) {
            // 현재 날짜를 기준으로 초기 값을 설정
            val currentDate = LocalDate.now()

            // DatePickerState 설정
            val datePickerState = rememberDatePickerState(
                yearRange = currentDate.year..currentDate.year + 1, // 현재 연도와 다음 연도를 허용
                initialDisplayMode = DisplayMode.Picker,
                initialSelectedDateMillis = runCatching {
                    val formatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC") // UTC 시간으로 설정
                    }
                    formatter.parse(currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")))?.time
                }.getOrElse { System.currentTimeMillis() } // 날짜 파싱 실패 시 현재 시간을 기본값으로 사용
            )

            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    /*containerColor: 전체 배경 색
                    titleContentColor: 제목의 텍스트 색
                    headlineContentColor: 헤드라인 텍스트 색
                    weekdayContentColor: 요일 텍스트 색
                    dayContentColor: 일반 날짜 텍스트 색
                    todayContentColor: 오늘 날짜 텍스트 색
                    selectedDayContentColor: 선택된 날짜 텍스트 색
                    disabledDayContentColor: 비활성화된 날짜 텍스트 색
                    selectedDayContainerColor: 선택된 날짜 배경 색
                    todayDateBorderColor: 오늘 날짜 테두리 색*/
                    containerColor = Color.White,
                    weekdayContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    disabledDayContentColor = blueP2,
                    dayContentColor = Color.Black, //날짜색 텍스트
                    todayDateBorderColor = blueP3, //오늘날짜색
                    selectedDayContainerColor = blueP3
                ),
            )

            // 취소 및 확인 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    modifier = Modifier
                        .wrapContentSize(),
                    onClick = {
                        onClickCancel()
                        DialogState.hide()
                     },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueP5, // 버튼 배경색
                        contentColor = Color.White     // 텍스트 색상 설정
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "취소")
                }

                Spacer(modifier = Modifier.width(5.dp))

                Button(
                    modifier = Modifier
                        .wrapContentSize(),
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                            val selectedDateFormatted = SimpleDateFormat(
                                "yyyyMMdd",
                                Locale.getDefault()
                            ).format(Date(selectedDateMillis))

                            // 날짜 확인 로그
                            Log.d("SelectedDate", selectedDateFormatted)  // 확인용 로그
                            selectedDate = selectedDateFormatted
                            onClickConfirm(selectedDateFormatted)
                            DialogState.hide()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueP5,    // 버튼 배경색
                        contentColor = Color.White  // 텍스트 색상 설정
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "확인")
                }
            }
        }
    }
}

@Composable
fun EditCategoryCreateDialog(
    onConfirmClick : (String) -> Unit,
    onCancelClick : () -> Unit,
) {
    var createCategory by remember {
        mutableStateOf("")
    }

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
                modifier = Modifier.padding(10.dp),
                text = "카테고리 생성",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = createCategory,
                onValueChange = {createCategory = it},
                singleLine = false,
                textStyle = TextStyle (
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = blueP3
                )
            )


            Row (
                modifier = Modifier.align(Alignment.End)
            ) {
                Button(
                    onClick = { onConfirmClick(createCategory) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueP5, // 버튼 배경색
                        contentColor = Color.White // 텍스트 색상 설정
                    ),
                ) {
                    Text(text = "확인", color = blueP2)
                }

                Button(
                    onClick = { onCancelClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueP5, // 버튼 배경색
                        contentColor = Color.White // 텍스트 색상 설정
                    ),
                ) {
                    Text(text = "취소")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditCategoryItem(
    item : CategoryEntity,
    onClick : (CategoryEntity) -> Unit,
    onLongClick : (CategoryEntity) -> Unit
) {
    // 클릭 상태를 추적할 mutable state
    val isClicked = remember { mutableStateOf(false) }

    // 클릭 시 상태 변경
    val backgroundColor = if (isClicked.value) blueP3 else blueP6

    Column (
        modifier = Modifier
            .padding(5.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .wrapContentSize()
            .combinedClickable(
                onClick = {
                    isClicked.value = !isClicked.value // 클릭 시 상태 반전
                    onClick(item) // 클릭된 아이템을 전달
                },
                onLongClick = {
                    onLongClick(item)
                },
            )
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = item.categoryName,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun EditTransaction(
    textContentIncome : String,
    textContentExpenditure : String,

    incomeOnTextChanged : (String) -> Unit,
    expenditureOnTextChanged : (String) -> Unit
) { //지출, 수입
    /*var textContentIncome by remember {
        mutableStateOf("")
    }

    var textContentExpenditure by remember {
        mutableStateOf("")
    }*/
    val focusManager = LocalFocusManager.current

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
    ) {
        Column (
            modifier = Modifier
                .weight(1f)
                .background(Purple80, RoundedCornerShape(12.dp))
                .padding(5.dp)
        ) {
            Text(
                modifier = Modifier.padding(5.dp),
                text = "수입",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = textContentIncome,
                onValueChange = { newText : String ->
                    // 숫자인지 검증
                    if (newText.all { it.isDigit() }) {
                        incomeOnTextChanged(newText)
                    }
                },
                singleLine = false,
                textStyle = TextStyle (
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, // 숫자 전용 키보드
                    imeAction = ImeAction.Done // 완료 버튼 표시
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple80,
                    unfocusedBorderColor = Purple80
                )
            )
        }

        Column (
            modifier = Modifier
                .weight(1f)
                .background(Pink80, RoundedCornerShape(12.dp))
                .padding(5.dp)
        ){
            Text(
                modifier = Modifier.padding(5.dp),
                text = "지출",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = textContentExpenditure,
                onValueChange = { newText : String ->
                    // 숫자인지 검증
                    if (newText.all { it.isDigit() }) {
                        expenditureOnTextChanged(newText)
                    }
                },
                singleLine = true, // 한 줄 입력만 허용
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, // 숫자 전용 키보드
                    imeAction = ImeAction.Done // 완료 버튼 표시
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Pink80,
                    unfocusedBorderColor = Pink80
                )
            )
        }
    }
}