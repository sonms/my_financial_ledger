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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.what2c.preferences_data_store.PreferencesDataStore
import com.example.what2c.preferences_data_store.PreferencesDataStore.getEntireExpenditure
import com.example.what2c.preferences_data_store.PreferencesDataStore.getEntireIncome
import com.purang.financial_ledger.R
import com.purang.financial_ledger.room_db.FinancialEntity
import com.purang.financial_ledger.ui.theme.Financial_LedgerTheme
import com.purang.financial_ledger.ui.theme.Pink80
import com.purang.financial_ledger.ui.theme.Purple80
import com.purang.financial_ledger.ui.theme.blueP3
import com.purang.financial_ledger.ui.theme.blueP5
import com.purang.financial_ledger.ui.theme.blueP6
import com.purang.financial_ledger.ui.theme.blueP7
import com.purang.financial_ledger.view_model.HomeViewModel
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController : NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val monthFinancialData by viewModel.selectedMonthEvents.observeAsState(emptyList())
    val context = LocalContext.current
    val entireIncome by getEntireIncome(context).collectAsState(initial = "0")
    val entireExpenditure by getEntireExpenditure(context).collectAsState(initial = "0")


    var selectMonth by remember { mutableStateOf(YearMonth.now().toString()) }

    LaunchedEffect(Unit) {
        val currentMonth = YearMonth.now()
        viewModel.fetchEventsByMonth(currentMonth) // Fetch data for current year and month
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
                text = "2024년 11월",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "MonthDropDown",
                tint = blueP3,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { /* TODO: Handle search action */ }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
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
                            text = "$entireIncome",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.End)
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
                            text = "$entireExpenditure",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.End)
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
                    Text(
                        text = "전 월에 비해 10000원 더 사용하셨습니다!",
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
}

@Composable
fun HomeFinancialItem(
    item: FinancialEntity,
    onItemClick: (FinancialEntity) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Content layout, allows expand/collapse interaction
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
                // Display the financial data
                Text(
                    text = item.title ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    //color = MaterialTheme.colorScheme.onSurface
                )

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
                        style = MaterialTheme.typography.bodyMedium,
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