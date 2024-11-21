package com.purang.financial_ledger.screen.calendar

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.purang.financial_ledger.screen.home.MonthDropDownButtonBottomSheet
import com.purang.financial_ledger.ui.theme.blueExDark
import com.purang.financial_ledger.ui.theme.blueExLight
import com.purang.financial_ledger.ui.theme.blueP3
import com.purang.financial_ledger.ui.theme.redInDark
import com.purang.financial_ledger.view_model.HomeViewModel
import kotlinx.coroutines.launch
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    //navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val monthTotalIncomeExpenditure by viewModel.selectedMonthTotals.observeAsState(
        TotalIncomeExpenditure(0, 0)
    )

    val monthBeforeTotalIncomeExpenditure by viewModel.selectedBeforeYearMonthTotals.observeAsState(
        TotalIncomeExpenditure(0, 0)
    )

    var isEmotionBottomSheetOpen by remember { mutableStateOf(false) }
    val yearMonths by viewModel.getDistinctYearMonthsData.observeAsState(emptyList())
    var selectMonth by remember { mutableStateOf(YearMonth.now().toString()) }
    var beforeYearMonthDataCheck by remember {
        mutableStateOf<Long?>(null)
    }
    LaunchedEffect(Unit) {
        val yearMonth = YearMonth.parse(selectMonth)
        viewModel.fetchBeforeByYearMonth(yearMonth)

        beforeYearMonthDataCheck = monthBeforeTotalIncomeExpenditure.totalExpenditure?.let {
            monthBeforeTotalIncomeExpenditure.totalIncome?.minus(
                it
            )
        }
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
                        isEmotionBottomSheetOpen = true
                    }
            )
        }

        if (monthTotalIncomeExpenditure.totalIncome != null || monthTotalIncomeExpenditure.totalExpenditure != null) {
            TotalGraph(
                modifier = Modifier.padding(bottom = 20.dp),
                colors = listOf(redInDark,blueExDark),
                data = listOf(
                    (monthTotalIncomeExpenditure.totalIncome!! / (monthTotalIncomeExpenditure.totalIncome!! + monthTotalIncomeExpenditure.totalExpenditure!!).toFloat()),
                    (monthTotalIncomeExpenditure.totalExpenditure!! / (monthTotalIncomeExpenditure.totalIncome!! + monthTotalIncomeExpenditure.totalExpenditure!!).toFloat())
                ),
                graphHeight = 120
            )

            GraphInfo(monthTotalIncomeExpenditure)

            if (beforeYearMonthDataCheck != null) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text (
                        text = if ((beforeYearMonthDataCheck ?: 0L) > 0L) {
                            "전년도 같은 월에 비해 ${beforeYearMonthDataCheck}이만큼 덜 썼어요"
                        } else {
                            "전년도 같은 월에 비해 ${beforeYearMonthDataCheck}이만큼 더 썼어요"
                        }
                    )
                }
            }

        } else {
            Text(text = "해당 월에 데이터가 존재하지 않습니다.")
        }


    }

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
internal fun TotalGraph(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    data: List<Float>,
    graphHeight: Int
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
    Canvas(modifier = modifier.height(graphHeight.dp)) {
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
    total : TotalIncomeExpenditure,
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
        if (total.totalIncome != 0L) {
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

        if (total.totalExpenditure != 0L) {
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