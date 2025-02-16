package com.purang.financial_ledger.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val blueP1 = validateColor(Color(0xFFEDECFF))
val blueP2 = validateColor(Color(0xFFDAD9FF))
val blueP3 = validateColor(Color(0xFFC8C7FF))
val blueP4 = validateColor(Color(0xFFB5B4FF))
val blueP5 = validateColor(Color(0xFFA3A1FE))
val blueP6 = validateColor(Color(0xFF8281CC))
val blueP7 = validateColor(Color(0xFF626199))

// Pink Colors
val pink1 = validateColor(Color(0xFFFFEBF0))  // 밝은 핑크 (기존보다 더 밝음)
val pink2 = validateColor(Color(0xFFF8D8E3))  // 중간 명도 핑크
val pink3 = validateColor(Color(0xFFF1C5D1))  // 약간 더 강렬한 핑크
val pink4 = validateColor(Color(0xFFEAB2BF))  // 중간 정도의 대비
val pink5 = validateColor(Color(0xFFD78EA2))  // 메인 핑크 대비 색
val pink6 = validateColor(Color(0xFFA05D70))  // 어두운 핑크 (텍스트 대비 가능)
val pink7 = validateColor(Color(0xFF704351))  // 매우 어두운 핑크 (배경 대비 텍스트 적합)

// Blue Colors
val blueExDark = validateColor(Color(0xFF1976D2))  // 기존 대비 향상
val blueExLight = validateColor(Color(0xFF1565C0)) // 더 높은 대비를 위해 조정

// Red Colors
val redInDark = validateColor(Color(0xFFD81B60))   // 어두운 대비 (기존 대비 향상)
val redInLight = validateColor(Color(0xFFFFA4B4))  // 밝은 대비 조정 (기존보다 대비 개선)

val redD = validateColor(Color(0xFFC7235B))
val blueD = validateColor(Color(0xFF3F51B5))

//아이템에 적용할 색상
val orange = validateColor(Color(0xFFFF9800))
val yellow = validateColor(Color(0xFFFFEB3B))
val green = validateColor(Color(0xFF4CAF50))
val blue = validateColor(Color(0xFF2196F3))
val purple = validateColor(Color(0xFF673AB7))

fun validateColor(color: Color): Color {
    return color.copy(
        alpha = color.alpha.coerceIn(0f, 1f),
        red = color.red.coerceIn(0f, 1f),
        green = color.green.coerceIn(0f, 1f),
        blue = color.blue.coerceIn(0f, 1f)
    )
}