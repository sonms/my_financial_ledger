package com.purang.financial_ledger.screen.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.financial_ledger.room_db.FinancialEntity
import com.purang.financial_ledger.screen.home.HomeFinancialItem
import com.purang.financial_ledger.ui.theme.blueP3
import com.purang.financial_ledger.view_model.HomeViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    searchText : String?,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val allFinancialData by viewModel.financialListData.observeAsState(emptyList())
    // 필터링된 데이터 - derivedStateOf
    //의존 상태가 변경되지 않으면 파생 상태를 재계산하지 않도록 보장합니다.
    //이는 큰 데이터 집합의 필터링이나 복잡한 계산 로직에 특히 유용합니다.
    val filteredData by remember(searchText, allFinancialData) {
        derivedStateOf {
            if (searchText?.isEmpty() == true) {
                allFinancialData
            } else {
                allFinancialData.filter { item ->
                    item.title?.contains(searchText!!, ignoreCase = true) == true ||
                            item.content?.contains(searchText!!, ignoreCase = true) == true
                }
            }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row (
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .wrapContentSize()
                        .align(Alignment.Top),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "backScreen")
                }
            }
        }

        if (filteredData.isNotEmpty()) {
            LazyColumn {
                itemsIndexed(
                    items = filteredData
                ) { _, item ->
                    HomeFinancialItem(item = item, onItemClick = {
                        navController.navigate("edit_financial?type=edit&id=${item.id}")
                    })
                }
            }
        } else {
            Text(
                modifier = Modifier.fillMaxSize().padding(10.dp).align(Alignment.CenterHorizontally),
                text = "검색된 데이터가 없습니다.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}