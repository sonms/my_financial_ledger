package com.purang.financial_ledger.screen.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.purang.financial_ledger.room_db.FinancialEntity
import com.purang.financial_ledger.screen.home.DeleteItemDialog
import com.purang.financial_ledger.screen.home.HomeFinancialItem
import com.purang.financial_ledger.view_model.HomeViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    searchText : String,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val allFinancialData by viewModel.financialListData.observeAsState(emptyList())


    var topSearchText by remember {
        mutableStateOf(searchText)
    }
    val focusManager = LocalFocusManager.current

    // 필터링된 데이터 - derivedStateOf
    //의존 상태(매개변수들)가 변경되지 않으면 파생 상태를 재계산하지 않도록 보장합니다.
    //이는 큰 데이터 집합의 필터링이나 복잡한 계산 로직에 특히 유용합니다.
    val filteredData by remember(topSearchText, allFinancialData) {
        derivedStateOf {
            if (searchText.isEmpty()) {
                allFinancialData
            } else {
                allFinancialData.filter { item ->
                    item.title?.contains(topSearchText, ignoreCase = true) == true ||
                            item.content?.contains(topSearchText, ignoreCase = true) == true
                }
            }
        }
    }

    var isDeleteDialogOpen by remember {
        mutableStateOf(false)
    }
    var deleteItem by remember {
        mutableStateOf<FinancialEntity?>(null)
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically, // 수직 정렬: 가운데 정렬
            horizontalArrangement = Arrangement.Start // 가로 방향 정렬: 시작 정렬
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp) // 아이콘 크기 조정
                )
            }

            Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 필드 간격

            OutlinedTextField(
                value = topSearchText,
                onValueChange = { newText ->
                    topSearchText = newText
                },
                singleLine = true, // 텍스트 필드를 한 줄로 제한
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                ),
                modifier = Modifier
                    .weight(1f) // 나머지 공간을 차지하도록 설정
                    .padding(start = 8.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done // 완료 버튼 표시
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = {
                            topSearchText = ""
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
            )
        }

        if (filteredData.isNotEmpty()) {
            LazyColumn {
                itemsIndexed(
                    items = filteredData
                ) { _, item ->
                    HomeFinancialItem(
                        item = item,
                        onItemClick = {
                            navController.navigate("edit_financial?type=edit&id=${item.id}")
                        },
                        onLongClick = {
                            isDeleteDialogOpen = !isDeleteDialogOpen
                            deleteItem = it
                        }
                    )
                }
            }
        } else {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally),
                text = "검색된 데이터가 없습니다.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (isDeleteDialogOpen) {
        DeleteItemDialog(
            item = deleteItem,
            onConfirmClick = {
                if (it != null) {
                    isDeleteDialogOpen = !isDeleteDialogOpen
                    viewModel.deleteFinancialData(it)
                }
            },
            onCancelClick = {
                isDeleteDialogOpen = !isDeleteDialogOpen
            }
        )
    }
}