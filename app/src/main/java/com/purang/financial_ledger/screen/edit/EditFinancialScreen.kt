package com.purang.financial_ledger.screen.edit

import android.graphics.Color.colorToHSV
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.purang.financial_ledger.MainActivity
import com.purang.financial_ledger.R
import com.purang.financial_ledger.loading.DialogState
import com.purang.financial_ledger.room_db.category.CategoryEntity
import com.purang.financial_ledger.ui.theme.blue
import com.purang.financial_ledger.ui.theme.blueExDark
import com.purang.financial_ledger.ui.theme.blueP2
import com.purang.financial_ledger.ui.theme.blueP3
import com.purang.financial_ledger.ui.theme.blueP4
import com.purang.financial_ledger.ui.theme.blueP5
import com.purang.financial_ledger.ui.theme.blueP7
import com.purang.financial_ledger.ui.theme.green
import com.purang.financial_ledger.ui.theme.orange
import com.purang.financial_ledger.ui.theme.pink5
import com.purang.financial_ledger.ui.theme.pink7
import com.purang.financial_ledger.ui.theme.purple
import com.purang.financial_ledger.ui.theme.redInDark
import com.purang.financial_ledger.ui.theme.yellow
import com.purang.financial_ledger.view_model.CategoryViewModel
import com.purang.financial_ledger.view_model.HomeViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
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
    val financialItem by viewModel.selectedFinancialItem.collectAsState()
    val movedDateData by viewModel.dateMoveData.collectAsState()

    if (id != null) {
        LaunchedEffect(id) {
            viewModel.setSelectedId(id.toLongOrNull())
        }
    }

    var selectedColor by remember {
        mutableStateOf (
            if (financialItem?.selectColor == 0) Color.Transparent else financialItem?.color ?: Color.Transparent
        )
    }

    LaunchedEffect(financialItem) {
        if (financialItem != null) {
            viewModel.refreshSelectedItem()
            selectedColor = if (financialItem?.selectColor == 0) Color.Transparent else financialItem?.color ?: Color.Transparent
        }
    }

    val categoryDataList by categoryViewModel.categoryData.observeAsState(emptyList())
    // ViewModel에서 데이터 관찰

    var isShowingCategoryDialog by remember { mutableStateOf(false) }

    /*val selectedCategory by remember(categoryDataList, financialItem) {
        derivedStateOf {
            financialItem?.categoryId?.let { id ->
                categoryDataList.find { it.id == id }
            }
        }
    }*/
    var selectCategory by remember {
        mutableStateOf<CategoryEntity?>(null)
    }

    LaunchedEffect(financialItem?.categoryId) {
        financialItem?.categoryId?.let { categoryId ->
            selectCategory = categoryDataList.find { it.id == categoryId }
        }
    }

    val selectedCategory = selectCategory

    var selectedDate by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(movedDateData) {
        selectedDate = if (movedDateData.isNullOrEmpty()) {
            LocalDate.now().toString()
        } else {
            movedDateData.toString()
        }
    }

    var textTitle by remember {
        mutableStateOf<String?>(null)
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



    /*LaunchedEffect(selectedColor) {
        if (financialItem?.selectColor != ColorConverter().fromColor(selectedColor)) {
            viewModel.updateSelectedColor(selectedColor)
        }
    }*/

    var isError by remember {
        mutableStateOf(false)
    }

    if (type != "default") {
        LaunchedEffect(financialItem, categoryDataList) {
            // categoryViewModel에서 categoryId로 카테고리 데이터 가져오기
            categoryViewModel.getCategoryItemById(financialItem?.categoryId)

            // 상태값 설정
            textTitle = financialItem?.title ?: ""
            textContent = financialItem?.content ?: ""
            //selectedCategory = categoryDataList.find { it.id == financialItem?.categoryId }
            editIncome = financialItem?.income.toString()
            editExpenditure = financialItem?.expenditure.toString()
            selectedDate = financialItem?.date.toString()
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "backScreen")
            }

            Spacer(modifier = Modifier.weight(1f))

            EditColor(
                itemColor = selectedColor,
                onCancelClick = { },
                onClickColor = {
                    selectedColor = it
                }
            )
        }

        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            item {
                EditTitle(
                    textTitle = textTitle ?: "",
                    onTextChange = { newText ->
                        textTitle = newText
                    },
                    isError = isError
                )
            }

            item {
                EditContent(
                    textContent = textContent,
                    onTextChange = { newText ->
                        textContent = newText
                    }
                )
            }

            item {
                Text(modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold,
                    text = "카테고리", fontSize = 18.sp)
                LazyRow (
                    horizontalArrangement = Arrangement.Start, // 전체 LazyRow의 아이템을 중앙 정렬
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp) // LazyRow가 전체 너비를 차지하도록 설정
                ) {
                    item {
                        IconButton(onClick = { isShowingCategoryDialog = true }) {
                            Column(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .background(blueP4, RoundedCornerShape(8.dp))
                                    .wrapContentSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(5.dp),
                                    contentDescription = "AddCategory",
                                )
                            }
                        }
                    }

                    itemsIndexed(
                        items = categoryDataList.filter { it.id != -1L }
                    ) { _, item ->
                        EditCategoryItem(
                            item,
                            isSelected = selectedCategory?.id == item.id,
                            onClick = {
                                selectCategory = if (selectCategory?.id == item.id) {
                                    null
                                } else {
                                    item
                                }
                            },
                            onLongClick = {
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
                    isError = isError,
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
                    },
                    expenditureOnTextChanged = {newText ->
                        editExpenditure = newText
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
                                if (textTitle.isNullOrEmpty()) {
                                    isError = true
                                } else {
                                    viewModel.addFinancialData(
                                        categoryId = selectCategory?.id,
                                        title = textTitle,
                                        content = textContent,
                                        date = selectedDate,
                                        expenditure = editExpenditure.toLongOrNull() ?: 0L,
                                        income = editIncome.toLongOrNull() ?: 0L,
                                        selectColor = selectedColor
                                    )
                                    //Log.e("defaultAdd", createFinancialData.toString())
                                    navController.navigate(
                                        MainActivity.BottomNavItem.Home.screenRoute,
                                        NavOptions.Builder()
                                            .setLaunchSingleTop(true)  // 이미 존재하는 화면을 재사용하지 않음
                                            .setPopUpTo(navController.graph.startDestinationId, true)  // 이전 화면을 스택에서 제거
                                            .build()
                                    )
                                }
                            } else {
                                //수정
                                viewModel.updateFinancialData(
                                    id = financialItem?.id!!,
                                    categoryId = selectCategory?.id,
                                    title = textTitle,
                                    content = textContent,
                                    date = selectedDate,
                                    expenditure = editExpenditure.toLongOrNull() ?: 0L,
                                    income = editIncome.toLongOrNull() ?: 0L,
                                    selectColor = selectedColor
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
                    if (newCategory.isNotEmpty()) {
                        categoryViewModel.addCategory(newCategory)
                        isShowingCategoryDialog = false
                    }
                },
                onCancelClick = { isShowingCategoryDialog = false }
            )
        }


    }
}

@Composable
fun EditTitle(
    textTitle: String,
    onTextChange: (String) -> Unit,
    isError : Boolean
) {
    val textColor = MaterialTheme.colorScheme.primary // 동적으로 색상변경
    val isErrorCheck by rememberSaveable { mutableStateOf(isError) }
    /*var isError by rememberSaveable { mutableStateOf(false) }

    fun validate(text: String?) {
        isError = text.isNullOrEmpty()
    }*/

    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = textTitle,
            onValueChange = {
                onTextChange(it)
                //validate(it)
            },
            trailingIcon = {
                if (isError)
                    Icon(Icons.Default.Info,"error", tint = MaterialTheme.colorScheme.error)
            },
            singleLine = false,
            textStyle = TextStyle(
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            ),
            isError = isErrorCheck,
            /*trailingIcon = {
                if (isError)
                    Icon(Icons.Filled.Info, "빈 칸을 채워주세요!", tint = MaterialTheme.colorScheme.error)
            },*/
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = blueP3
            ),
            label = {
                Text (
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "제목",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            },
            supportingText = {
                if (isError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "제목을 입력하세요.",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
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
            ),
            label = {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "내용",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
        )

        /*Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(blueP2)
        )*/
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCalendar(
    selectDate : String?,
    isError: Boolean,
    onClickCancel: () -> Unit,
    onClickConfirm: (yyyyMMdd: String) -> Unit
) { //날짜 설정 칸?
    val isDialogShowing by DialogState.isShowing.collectAsState()
    val isErrorCheck by rememberSaveable {
        mutableStateOf(isError)
    }

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
                .background(blueP4, RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            /*val currentDate = LocalDate.now()
            val formatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            formatter.parse(currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")))?.time

            val parsedDate = try {
                SimpleDateFormat("yyyyMMdd", Locale.KOREAN).parse(selectedDate)
            } catch (e: ParseException) {
                formatter  // 날짜 파싱에 실패할 경우 현재 날짜를 사용
            }*/
            Text(
                modifier = Modifier.padding(10.dp),
                text = (selectDate ?: LocalDate.now().toString()),
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
                    .padding(10.dp)
            )
        }
    }




    if (isDialogShowing) {
        DatePickerDialog(
            modifier = Modifier.wrapContentSize(),
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
                            /*Log.d("SelectedDate", selectedDateFormatted)  // 확인용 로그
                            selectedDate = selectedDateFormatted*/
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
                    Text(
                        text = "확인",
                        fontWeight = FontWeight.Bold
                    )
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
    var isError by remember { mutableStateOf(false) }

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
                ),
                label = {
                    Text(text = "이곳을 눌러 생성")
                },
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "카테고리를 입력하세요.",
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
            )


            Row (
                modifier = Modifier.align(Alignment.End)
            ) {
                Button(
                    onClick = {
                        if (createCategory.isNotEmpty()) {
                            onConfirmClick(createCategory)
                        } else {
                            isError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueP5, // 버튼 배경색
                        contentColor = Color.White // 텍스트 색상 설정
                    ),
                ) {
                    Text(
                        text = "확인",
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(5.dp))

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
    item: CategoryEntity,
    isSelected: Boolean, // 선택 여부를 외부에서 관리
    onClick: (CategoryEntity) -> Unit,
    onLongClick: (CategoryEntity) -> Unit
) {
    // 선택 상태에 따라 배경색 결정
    val backgroundColor = if (isSelected) blueP7 else pink7

    Row(
        modifier = Modifier
            .padding(5.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .wrapContentSize()
            .combinedClickable(
                onClick = {
                    onClick(item) // 클릭된 아이템을 외부로 전달
                },
                onLongClick = {
                    onLongClick(item)
                },
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = if (isSelected) Modifier.padding(start = 10.dp, bottom = 10.dp, top = 10.dp) else Modifier.padding(10.dp),
            text = item.categoryName,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        if (isSelected) {
            Icon(Icons.Default.Done, contentDescription = "SelectedCategory", tint = Color.White, modifier = Modifier.padding(start = 5.dp, end = 10.dp))
        }
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
                .background(redInDark, RoundedCornerShape(12.dp))
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
                    focusedBorderColor = redInDark,
                    unfocusedBorderColor = redInDark
                ),
                label = {
                    Text(text = "이곳을 눌러 추가")
                }
            )
        }

        Column (
            modifier = Modifier
                .weight(1f)
                .background(blueExDark, RoundedCornerShape(12.dp))
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
                    focusedBorderColor = blueExDark,
                    unfocusedBorderColor = blueExDark
                ),
                label = {
                    Text(text = "이곳을 눌러 추가")
                }
            )
        }
    }
}

@Composable
fun EditColor (
    itemColor : Color?,
    onCancelClick: () -> Unit,
    onClickColor:(Color) -> Unit
) {
    var isClickText by remember {
        mutableStateOf(false)
    }

    val colorList = listOf(Color.Red, orange, yellow, green, blue, purple, pink5, blueP5)

    Row (
        modifier = Modifier.padding(end = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "색 설정하기",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(end = 5.dp)
                .clickable {
                    isClickText = !isClickText
                }
        )

        // 선택한 색상 표시
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(itemColor ?: Color.Transparent, shape = CircleShape)
                .border(1.dp, Color.Gray, shape = CircleShape)
        )

        if (isClickText) {
            ColorPickerDialog(
                onDismiss = {
                    onCancelClick()
                    isClickText = !isClickText
                },

                onColorSelected =  {
                    onClickColor(it)
                    isClickText = false
                }
            )
            /*Dialog(
                onDismissRequest = {
                    onCancelClick()
                    isClickText = !isClickText
                }
            ) {
                Card (
                    modifier = Modifier
                        .width(320.dp)
                        .wrapContentHeight()
                        .padding(10.dp),
                    shape = RoundedCornerShape(8.dp),
                    *//*colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),*//*
                ) {
                    LazyRow (
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(
                            items = colorList
                        ) { _, item ->
                            Box(
                                modifier = Modifier
                                    .background(item, CircleShape)
                                    .border(2.dp, Color.Gray, shape = CircleShape)
                                    .size(24.dp)
                                    .clickable {
                                        onClickColor(item)
                                        isClickText = false
                                    }
                            )
                        }
                    }
                }
            }*/
        }
    }
}

@Composable
fun ColorPickerDialog(
    initialColor: Color = Color.Red,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    var hue by remember { mutableStateOf(0f) }      // 색상 (0~360°)
    var saturation by remember { mutableStateOf(1f) } // 채도 (0~1)
    var lightness by remember { mutableStateOf(0.5f) } // 밝기 (0~1)

    // 초기 색상을 HSL 값으로 변환
    LaunchedEffect(initialColor) {
        val hsl = FloatArray(3)
        Color.run { colorToHSV(initialColor.toArgb(), hsl) }
        hue = hsl[0]
        saturation = hsl[1]
        lightness = hsl[2]
    }

    val selectedColor = Color.hsl(hue, saturation, lightness)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "색상 선택", style = MaterialTheme.typography.titleLarge)

                // 선택된 색상 미리보기
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(selectedColor, RoundedCornerShape(50.dp))
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 색상 선택 슬라이더 (Hue)
                Text(text = "색상 (Hue)")
                Slider(
                    value = hue,
                    onValueChange = { hue = it },
                    valueRange = 0f..360f
                )

                // 채도 선택 슬라이더 (Saturation)
                Text(text = "채도 (Saturation)")
                Slider(
                    value = saturation,
                    onValueChange = { saturation = it },
                    valueRange = 0f..1f
                )

                // 밝기 선택 슬라이더 (Lightness)
                Text(text = "밝기 (Lightness)")
                Slider(
                    value = lightness,
                    onValueChange = { lightness = it },
                    valueRange = 0f..1f
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("취소")
                    }
                    Button(onClick = { onColorSelected(selectedColor) }) {
                        Text("확인")
                    }
                }
            }
        }
    }
}