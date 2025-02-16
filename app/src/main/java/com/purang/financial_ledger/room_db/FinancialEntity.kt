package com.purang.financial_ledger.room_db

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.purang.financial_ledger.room_db.category.CategoryEntity
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Entity(
    tableName = "FinancialTable",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.SET_NULL // Category가 삭제되면 관련 FinancialEntity의 카테고리를 NULL로 설정
    )],
    indices = [Index(value = ["categoryId"])] // 성능 향상을 위한 인덱스 추가
)
data class FinancialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo
    var categoryId: Long?,
    @ColumnInfo
    var title: String?, //적을 내용
    @ColumnInfo
    var content: String?, //적을 내용
    @ColumnInfo
    val createDate: String = LocalDateTime.now().toString(),
    @ColumnInfo
    var date: String?, //날짜
    @ColumnInfo
    var expenditure: Long?, //지출
    @ColumnInfo
    var income: Long?, //호득, 수입 등
    @ColumnInfo
    var selectColor: Int //not null 기본값 0
) {
    @Ignore
    val color = Color(selectColor)
}


/*data class TodoEntity (
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val content: String,
    @ColumnInfo
    val createDate: String = LocalDateTime.now().toString(),
    @ColumnInfo
    val type: String = "todo", // 추가: 일반 To-Do와 캘린더용 To-Do 구분을 위한 필드
    @ColumnInfo
    val eventDate: String? = null // 캘린더 이벤트의 날짜를 저장할 필드 (캘린더용 To-Do인 경우)
)*/