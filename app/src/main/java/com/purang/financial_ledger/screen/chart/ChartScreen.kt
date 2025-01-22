package com.purang.financial_ledger.screen.chart

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.financial_ledger.model.TotalIncomeExpenditure
import com.purang.financial_ledger.room_db.FinancialEntity
import com.purang.financial_ledger.room_db.category.CategoryEntity
import com.purang.financial_ledger.screen.home.MonthDropDownButtonBottomSheet
import com.purang.financial_ledger.ui.theme.blueD
import com.purang.financial_ledger.ui.theme.blueExDark
import com.purang.financial_ledger.ui.theme.blueExLight
import com.purang.financial_ledger.ui.theme.blueP3
import com.purang.financial_ledger.ui.theme.blueP4
import com.purang.financial_ledger.ui.theme.blueP5
import com.purang.financial_ledger.ui.theme.blueP7
import com.purang.financial_ledger.ui.theme.pink3
import com.purang.financial_ledger.ui.theme.pink4
import com.purang.financial_ledger.ui.theme.pink7
import com.purang.financial_ledger.ui.theme.redD
import com.purang.financial_ledger.ui.theme.redInDark
import com.purang.financial_ledger.view_model.CategoryViewModel
import com.purang.financial_ledger.view_model.HomeViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.YearMonth
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChartScreen(
    //navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    categoryViewModel : CategoryViewModel = hiltViewModel()
) {
    //현재 달
    val monthTotalIncomeExpenditure by viewModel.selectedMonthTotals.observeAsState(
        TotalIncomeExpenditure(0, 0)
    )

    //전년도 같은 월
    val monthBeforeTotalIncomeExpenditure by viewModel.selectedBeforeYearMonthTotals.observeAsState(
        TotalIncomeExpenditure(null, null)
    )

    //이전 달
    val monthBeforeTotalByMonth by viewModel.selectedBeforeMonthTotals.observeAsState(
        TotalIncomeExpenditure(0, 0)
    )

    val selectFinancialDataByCategoryId by viewModel.getFinancialDataByCategoryId.observeAsState(
        emptyList()
    )

    val categoryAllData by categoryViewModel.categoryData.observeAsState(emptyList())

    var isEmotionBottomSheetOpen by remember { mutableStateOf(false) }
    var isClickGraphInfo by remember {
        mutableStateOf(false)
    }

    val yearMonths by viewModel.getDistinctYearMonthsData.observeAsState(emptyList())
    var selectMonth by remember { mutableStateOf(YearMonth.now().toString()) }

    //전년도 같은달 = 현재 600씀 - 전년도 같은월 500씀 = 100원 더씀
    val beforeYearMonthDataCheck by remember(monthBeforeTotalIncomeExpenditure) {
        derivedStateOf {
            // 총 지출 값이 null인 경우 null을 반환
            monthBeforeTotalIncomeExpenditure.totalExpenditure?.let { expenditure ->
                monthTotalIncomeExpenditure.totalIncome?.minus(expenditure)
            }
        }
    }

    //이전달과 비교 = 이전달 18원씀 - 현재달 77원씀 = -59원만큼 더씀
    val beforeMonthDataCheck by remember(monthBeforeTotalByMonth, monthTotalIncomeExpenditure) {
        derivedStateOf {
            monthTotalIncomeExpenditure.totalExpenditure?.let { currentExpenditure ->
                monthBeforeTotalByMonth.totalExpenditure?.minus(currentExpenditure)
            }
        }
    }

    var selectCategoryId by remember {
        mutableStateOf<Long?>(null)
    }

    LaunchedEffect(Unit) {
        val yearMonth = YearMonth.parse(selectMonth)
        viewModel.fetchBeforeByYearMonth(yearMonth)
        viewModel.fetchBeforeByMonth(yearMonth)

        //카테고리
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

    LaunchedEffect(selectCategoryId) {
        viewModel.fetchCategoryId(selectCategoryId)
    }


    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
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
                        isEmotionBottomSheetOpen = true
                    }
            )
        }

        LazyColumn (
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                if (monthTotalIncomeExpenditure.totalIncome != null || monthTotalIncomeExpenditure.totalExpenditure != null) {
                    TotalGraph(
                        modifier = Modifier.padding(bottom = 20.dp),
                        colors = listOf(redInDark,blueExDark),
                        data = listOf(
                            (monthTotalIncomeExpenditure.totalIncome!! / (monthTotalIncomeExpenditure.totalIncome!! + monthTotalIncomeExpenditure.totalExpenditure!!).toFloat()),
                            (monthTotalIncomeExpenditure.totalExpenditure!! / (monthTotalIncomeExpenditure.totalIncome!! + monthTotalIncomeExpenditure.totalExpenditure!!).toFloat())
                        ),
                        graphHeight = 120,
                        onClick = {
                            isClickGraphInfo = !isClickGraphInfo
                        }
                    )

                    if (isClickGraphInfo) {
                        GraphDetailInfo(
                            dataExpenditure = monthTotalIncomeExpenditure.totalExpenditure,
                            dataIncome = monthTotalIncomeExpenditure.totalIncome
                        )
                    }

                    GraphInfo(
                        dataExpenditure = monthTotalIncomeExpenditure.totalExpenditure,
                        dataIncome = monthTotalIncomeExpenditure.totalIncome
                    )

                    //전년도 같은 월
                    CompareBeforeYearMonthTotalAmount(beforeYearMonthDataCheck)
                    //이전 달
                    CompareBeforeMonthTotalAmount(beforeMonthDataCheck)

                } else {
                    Text(text = "해당 월에 데이터가 존재하지 않습니다.")
                }
            }

            /*item {
                CategoryChartScreen(
                    selectFinancialDataByCategoryId,
                    categoryAllData
                ) {
                    selectCategoryId = it
                }
            }*/

            stickyHeader {
                if (monthTotalIncomeExpenditure.totalIncome != null || monthTotalIncomeExpenditure.totalExpenditure != null) {
                    CategoryStickyHeader(
                        categoryData = categoryAllData,
                        onCategoryClick = {
                            selectCategoryId = it
                        }
                    )
                }
            }

            item {
                if (selectFinancialDataByCategoryId.isNotEmpty()) {
                    GraphByCategory(selectFinancialDataByCategoryId = selectFinancialDataByCategoryId)
                } else {
                    Text(text = "해당 카테고리에 데이터가 존재하지 않습니다.")
                }
            }
        }






    }
    //

    if (isEmotionBottomSheetOpen) {
        val context = LocalContext.current
        Log.e("test3", "test3")
        MonthDropDownButtonBottomSheet(
            modifier = Modifier.height(160.dp),
            monthData = yearMonths.ifEmpty {
                Toast.makeText(context, "현재 달 이외에 데이터가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                emptyList()
            },
            closeSheet = { isEmotionBottomSheetOpen = false },
            onClick = {
                selectMonth = it
            }
        )
    }
}

@Composable
fun CompareBeforeYearMonthTotalAmount(beforeYearMonthDataCheck : Long?) {

    if (beforeYearMonthDataCheck != null) {
        val backgroundColor = if ((beforeYearMonthDataCheck.toLong()) >= 0L) {
            blueD
        } else {
            redD
        }

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
                .background(backgroundColor, RoundedCornerShape(6.dp))
        ) {
            Text (
                text = if ((beforeYearMonthDataCheck.toLong()) >= 0L) {
                    "전년도 같은 달에 비해 ${numberFormat(beforeYearMonthDataCheck)}원 만큼 더 썼어요"
                } else {
                    "전년도 같은 달에 비해 ${numberFormat(beforeYearMonthDataCheck)}원 만큼 덜 썼어요"
                },
                modifier = Modifier.padding(10.dp)
            )
        }
    } else {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text (
                text = "비교할 전 년도가 없어요"
            )
        }
    }
}

@Composable
fun CompareBeforeMonthTotalAmount(beforeMonthDataCheck : Long?) {
    if (beforeMonthDataCheck != null) {
        val backgroundColor = if ((beforeMonthDataCheck.toLong()) >= 0L) {
            redD
        } else {
            blueD
        }

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
                .background(backgroundColor, RoundedCornerShape(6.dp))
        ) {
            //-59원만큼 더씀
            Text (
                text = if ((beforeMonthDataCheck.toLong()) >= 0L) {
                    "이전 달에 비해 ${numberFormat(abs(beforeMonthDataCheck))}원 만큼 덜 썼어요"
                } else {
                    "이전 달에 비해 ${numberFormat(abs(beforeMonthDataCheck))}원 만큼 더 썼어요"
                },
                modifier = Modifier.padding(10.dp)
            )
        }
    } else {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text (
                text = "비교할 이전 달이 없어요"
            )
        }
    }
}

fun numberFormat(amount: Long?): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA) // 한국 로케일 설정
    return numberFormat.format(amount)
}

@Composable
fun GraphDetailInfo(
    dataExpenditure : Long?,
    dataIncome : Long?
) {
    Row {
        Text(
            text = "수입 ${numberFormat(dataIncome)}원",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondary
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = "지출  ${numberFormat(dataExpenditure)}원",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}


@Composable
internal fun TotalGraph(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    data: List<Float>,
    graphHeight: Int,
    onClick : () -> Unit
) {
    val total = data.sum().coerceAtLeast(0.00001f) // 데이터 리스트에 있는 값의 총합 구하기, 0으로 나누기방지
    val angles = data.map { (it / total * 360f).coerceIn(0f, 360f) } // 데이터 값의 비율을 구하고, 360도를 기준으로 한 각도로 변환하여 리스트로 저장하기, 0~360으로제한


    val angleList = remember(data) { angles.map { Animatable(0f) } }

    LaunchedEffect(data) { // data가 변경될 때마다 애니메이션을 다시 시작
        angleList.forEachIndexed { index, value ->
            launch {
                value.snapTo(0f) // 애니메이션 시작 전 각도를 0으로 설정

                val targetValue = angles[index]

                if (!targetValue.isNaN()) {
                    value.animateTo( // 각도를 목표 각도까지 애니메이션을 적용
                        targetValue = angles[index],
                        animationSpec = tween( // 애니메이션을 정의할 때 사용하는 함수로 시간에 따라 값이 변경되는 함수로 애니메이션 시작 값에서 목표 값까지 일정 속도로 변화한다.
                            durationMillis = 1000, // 애니메이션이 1초 동안 지속
                            easing = LinearOutSlowInEasing // 애니메이션의 속도 곡선을 설정
                        )
                    )
                }
            }
        }
    }

    // Canvas를 사용하여 그래프를 그리고 `graphHeight.dp`를 픽셀 단위로 변환하여 그래프의 높이를 설정
    Canvas(
        modifier = modifier
            .height(graphHeight.dp)
            .clickable { onClick() }
    ) {
        /*
         * 그래프의 선 두께를 지정
         * `strokeWidth = 4f`로 설정하면 선이 4 픽셀 두꺼워진다.
         * 값이 커질수록 더 굵은 선이 그려지며, 작아질수록 더 얇은 선이 그려진다.
         */
        val strokeWidth = graphHeight.dp.toPx() / 4
        /*
         * 원형 그래프의 반지름을 나타내는 값으로 그래프의 크기와 위치를 결정한다.
         * 원의 중심에서 외곽선까지의 거리를 `radius`로 설정한다.
         * `radius`는 그래프 높이에서 선 두께를 빼고 2로 나눈 값으로 설정한다.
         * `radius = 50f`으로 설정하면 원의 중심에서부터 50 픽셀 떨어진 위치에 그려진다.
         */
        val radius = (graphHeight.dp.toPx() - strokeWidth) / 2
        // 그래프의 중심 좌표
        val centerX = size.width / 2f
        val centerY = radius + strokeWidth / 2

        drawGraph(colors, radius, strokeWidth, centerX, centerY, angleList)
    }
}

private fun DrawScope.drawGraph(
    //angles: List<Float>, // 각 데이터 항목의 중심각을 나타내며 각도는 360도로 정규화되어 있다.
    colors: List<Color>, // 각 데이터 항목에 해당하는 색상을 지정
    radius: Float, // 원형 그래프의 반지름을 나타낸다.
    strokeWidth: Float, // 그래프의 선 두께를 지정
    centerX: Float, // 그래프의 중심 좌표
    centerY: Float, // 그래프의 중심 좌표
    angleList: List<Animatable<Float, AnimationVector1D>>
) {
    var startAngle = -90f // 원형 그래프는 12시 방향을 0도로 시작하여 반시계 방향으로 그려지므로 `-90`도를 시작 각도로 설정한다.

    // 리스트를 순회하면서 각 데이터 항목에 대한 원호를 그린다.
    angleList.forEachIndexed { index, value -> // 현재 데이터 항목의 인덱스, 현재 데이터 항목의 중심각
        val color = colors[index]
        val rangeAngle = value.value.coerceIn(0f, 360f) //0~360

        if (rangeAngle > 0) {
            drawArc(
                color = color, // 그래프 부분의 색상을 지정
                startAngle = startAngle, // 원호의 시작 각도를 설정
                sweepAngle = rangeAngle, // 원호의 중심각을 지정
                useCenter = false,
                style = Stroke(width = strokeWidth), // 선의 두께를 설정한다.
                topLeft = Offset(centerX - radius, centerY - radius), // 원호를 그릴 사각형의 왼쪽 상단 모서리의 위치
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2) // 원호를 그릴 사각형의 크기를 설정
            )
        }
        // 각 데이터 항목의 그리기가 완료된 후에 `startAngle`을 현재 `angle` 만큼 증가시켜 다음 데이터 항목의 시작 각도를 설정한다.
        startAngle += rangeAngle
    }
}

@Composable
fun GraphInfo(
    dataExpenditure : Long?,
    dataIncome : Long?
) {
    val incomeColor = MaterialTheme.colorScheme.onSecondary
    val expenditureColor = MaterialTheme.colorScheme.secondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (dataIncome != null && dataExpenditure != null) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(modifier = Modifier
                    .size(16.dp)
                    .padding(end = 5.dp)) { // Canvas 크기 설정
                    drawCircle(
                        color = incomeColor, // 원의 색상
                        radius = size.minDimension / 2, // 반지름 (Canvas의 절반 크기)
                        center = center // Canvas의 중앙에 그리기
                    )
                }

                Text(
                    text = "수입",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        if (dataIncome != null && dataExpenditure != null) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(modifier = Modifier
                    .size(16.dp)
                    .padding(end = 5.dp)) { // Canvas 크기 설정
                    drawCircle(
                        color = expenditureColor, // 원의 색상
                        radius = size.minDimension / 2, // 반지름 (Canvas의 절반 크기)
                        center = center // Canvas의 중앙에 그리기
                    )
                }

                Text(
                    text = "지출",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

/*
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryChartScreen(
    selectFinancialDataByCategoryId : List<FinancialEntity>?,
    categoryData : List<CategoryEntity>,
    onCategoryClick: (Long?) -> Unit
) {
    LazyColumn (

    ) {
        stickyHeader {
            CategoryStickyHeader(
                categoryData = categoryData,
                onCategoryClick = {
                    onCategoryClick(it)
                }
            )
        }

        item {
            if (selectFinancialDataByCategoryId?.isNotEmpty() == true) {
                GraphByCategory(selectFinancialDataByCategoryId = selectFinancialDataByCategoryId)
            } else {
                Text(text = "해당 카테고리에 데이터가 존재하지 않습니다.")
            }
        }
        //전체 차트를 init으로 설정 <-> 카테고리 선택 시 변경하도록
        */
/*items(
            selectFinancialDataByCategoryId
        ) {

        }*//*

    }
}
*/

@Composable
fun GraphByCategory(
    selectFinancialDataByCategoryId : List<FinancialEntity>
) {
    val categoryTotalIncome by derivedStateOf {
        selectFinancialDataByCategoryId.sumOf { it.income ?: 0L }
    }

    val categoryTotalExpenditure by derivedStateOf {
        selectFinancialDataByCategoryId.sumOf { it.expenditure ?: 0L }
    }

    var isClickGraphInfo by remember {
        mutableStateOf(false)
    }

    if (categoryTotalIncome != 0L || categoryTotalExpenditure != 0L) {
        TotalGraph(
            modifier = Modifier.padding(bottom = 20.dp, top = 10.dp),
            colors = listOf(redInDark,blueExDark),
            data = listOf(
                (categoryTotalIncome / (categoryTotalIncome + categoryTotalExpenditure).toFloat()),
                (categoryTotalExpenditure / (categoryTotalIncome + categoryTotalExpenditure).toFloat())
            ),
            graphHeight = 120,
            onClick = {
                isClickGraphInfo = !isClickGraphInfo
            }
        )

        if (isClickGraphInfo) {
            GraphDetailInfo(
                dataExpenditure = categoryTotalExpenditure,
                dataIncome = categoryTotalIncome
            )
        }

        GraphInfo(
            dataExpenditure = categoryTotalExpenditure,
            dataIncome = categoryTotalIncome
        )

    } else {
        Text(text = "해당 카테고리에 데이터가 존재하지 않습니다.")
    }
}

@Composable
fun CategoryStickyHeader(
    categoryData: List<CategoryEntity>,
    onCategoryClick : (Long?) -> Unit
) {
    // 선택된 카테고리를 관리하는 상태
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }

    Row (modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        Text(
            text = "카테고리",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 10.dp, start = 10.dp),
            fontWeight = FontWeight.Bold
        )

        LazyRow {
            itemsIndexed(items = categoryData) { _, item ->
                // CategoryItem에 선택 상태와 선택 이벤트 전달
                CategoryItem(
                    item = item,
                    isSelected = item.id == selectedCategoryId,
                    onItemSelected = { selectedId ->
                        selectedCategoryId = if (selectedCategoryId != selectedId) {
                            selectedId
                        } else {
                            null
                        }
                        onCategoryClick(selectedCategoryId)
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    item: CategoryEntity,
    isSelected: Boolean,
    onItemSelected: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(5.dp)
            .background(
                if (isSelected) blueP7 else pink7,
                RoundedCornerShape(8.dp)
            )
            .clickable {
                onItemSelected(item.id) // 선택된 ID를 부모에게 전달
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.categoryName,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            modifier = if (isSelected) Modifier.padding(start = 10.dp, bottom = 10.dp, top = 10.dp) else Modifier.padding(10.dp)
        )

        if (isSelected) {
            Icon(Icons.Default.Done, contentDescription = "clickCategory", tint = Color.White, modifier = Modifier.padding(start = 5.dp, end = 10.dp))
        }
    }
}
/*
@Composable
fun DataForCategory(
    item : FinancialEntity
) {

}*/
