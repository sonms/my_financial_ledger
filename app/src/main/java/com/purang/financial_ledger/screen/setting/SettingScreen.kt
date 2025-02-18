package com.purang.financial_ledger.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.purang.financial_ledger.R
import com.purang.financial_ledger.preferences_data_store.PreferencesDataStore
import com.purang.financial_ledger.screen.chart.numberFormat
import com.purang.financial_ledger.ui.theme.blueP3
import com.purang.financial_ledger.ui.theme.blueP5
import kotlinx.coroutines.launch

@Composable
fun SettingScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val darkMode by PreferencesDataStore.getState(context).collectAsState(initial = false)
    val budgetData by PreferencesDataStore.getBudget(context).collectAsState(initial = "0")

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        SettingItem(
            icon = Icons.Default.Info,
            title = "다크 모드 설정",
            description = "Enable dark mode for the app",
            switchState = darkMode,
            onSwitchChange = { state ->
                scope.launch {
                    PreferencesDataStore.saveState(context, state)
                }
            }
        )

        SettingBudgetItem(
            icon = ImageVector.vectorResource(id =  R.drawable.baseline_tips_and_updates_24),
            title = "목표 예산 설정",
            description = "Set a target budget",
            budgetData = budgetData,
            onClick = {
                scope.launch {
                    PreferencesDataStore.saveBudget(context, it)
                }
            }
        )
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    switchState: Boolean? = null,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = { onClick?.invoke() })
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description, style = MaterialTheme.typography.bodyMedium)
        }



        if (switchState != null && onSwitchChange != null) {
            Switch(
                checked = switchState,
                onCheckedChange = { onSwitchChange(it) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Color.Gray,
                    checkedThumbColor = Color.LightGray,
                    checkedBorderColor = Color.DarkGray,
                    uncheckedTrackColor = Color.LightGray,
                    uncheckedThumbColor = Color.DarkGray,
                )
            )
        }
    }
}

@Composable
fun SettingBudgetItem(
    icon: ImageVector,
    title: String,
    description: String,
    budgetData : String?,
    onClick : (String) -> Unit
) {
    var isShowBudgetDialog by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = { isShowBudgetDialog = true })
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            if (budgetData == "0") {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "현재 설정된 예산 : ${numberFormat(budgetData?.toLongOrNull())}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description, style = MaterialTheme.typography.bodyMedium)
        }



        if (isShowBudgetDialog) {
            if (budgetData != null) {
                EditBudgetDialog (
                    initData = budgetData,
                    onConfirmClick = {
                        isShowBudgetDialog = false
                        onClick(it)
                    },
                    onCancelClick = {
                        isShowBudgetDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun EditBudgetDialog(
    initData : String,
    onConfirmClick : (String) -> Unit,
    onCancelClick : () -> Unit,
) {
    var budget by remember {
        mutableStateOf(initData)
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
                text = "예산 설정",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = budget,
                onValueChange = {budget = it},
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
                            text = "목표할 예산을 입력하세요.",
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
                        if (budget.isNotEmpty()) {
                            onConfirmClick(budget)
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