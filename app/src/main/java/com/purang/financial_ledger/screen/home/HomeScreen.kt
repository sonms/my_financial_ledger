package com.purang.financial_ledger.screen.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.what2c.preferences_data_store.PreferencesDataStore
import com.purang.financial_ledger.R
import com.purang.financial_ledger.room_db.FinancialEntity
import com.purang.financial_ledger.ui.theme.blueP3
import com.purang.financial_ledger.ui.theme.blueP6
import com.purang.financial_ledger.ui.theme.blueP7
import com.purang.financial_ledger.view_model.HomeViewModel
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val monthFinancialData by viewModel.selectedMonthEvents.observeAsState(emptyList())
    val context = LocalContext.current
    val entireIncome = PreferencesDataStore.getEntireIncome(context)

    var selectMonth by remember { mutableStateOf(YearMonth.now().toString()) }

    LaunchedEffect(Unit) {
        viewModel.fetchEventsByMonth(YearMonth.now())
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
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            Text(
                text = "2024년 11월",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Image(Icons.Default.KeyboardArrowDown, contentDescription = "MonthDropDown")

            IconButton(
                onClick = { /* TODO: Handle search action */ }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }



        LazyColumn {
            //총 수입, 총 지출
            item {
                Row {
                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(blueP3, RoundedCornerShape(12.dp))
                    ) {
                        Text(text = "이번 달 총 수입", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        Text(text = "$entireIncome", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.align(Alignment.End))
                    }

                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(blueP6, RoundedCornerShape(12.dp))
                    ) {
                        Text(text = "이번 달 총 지출", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        //Todo 나중에 여기 값 변경
                        Text(text = "$entireIncome", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.align(Alignment.End))
                    }
                }
            }

            //전 달에 비해 또는 전 년도 같은 월에 비해 비교
            item {
                Row (
                    modifier = Modifier
                        .wrapContentSize()
                        .background(blueP7, RoundedCornerShape(12.dp))
                ) {
                    //Todo 여기도 text값 생각하기
                    Text(text = "전 월에 비해 10000원 더 사용하셨습니다!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            //현재 선택된 달의 총 수입, 총 지출
            item {

            }

            //현재 선택된 달의 가계부 내역
            itemsIndexed(
                items = monthFinancialData,
            ) { _, item ->
                HomeFinancialItem(
                    item = item
                ) { financialItem ->
                    Log.e("item", financialItem.toString())
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

    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ).clickable {
                onItemClick(item)
            }
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(text = "Hello, ")
//            Text(
//                text = name, style = MaterialTheme.typography.headlineMedium.copy(
//                    fontWeight = FontWeight.ExtraBold
//                )
//            )
            if (expanded) {
                Text(
                    text = ("Composem ipsum color sit lazy, " +
                            "padding theme elit, sed do bouncy. ").repeat(2),
                )
            }
        }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }
            )
        }
    }
}