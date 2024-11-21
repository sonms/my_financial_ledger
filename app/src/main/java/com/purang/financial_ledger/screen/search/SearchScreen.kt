package com.purang.financial_ledger.screen.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.financial_ledger.room_db.FinancialEntity
import com.purang.financial_ledger.screen.home.HomeFinancialItem
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
        LazyColumn {
            itemsIndexed(
                items = filteredData
            ) { _, item ->
                HomeFinancialItem(item = item, onItemClick = {
                    navController.navigate("edit_financial?type=edit&id=${item.id}")
                })
            }
        }
    }
}