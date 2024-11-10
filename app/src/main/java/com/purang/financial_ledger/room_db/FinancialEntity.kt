package com.purang.financial_ledger.room_db

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.purang.financial_ledger.model.Category
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Entity(tableName = "FinancialTable")
data class FinancialEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,
    @ColumnInfo
    val category : Category,
    @ColumnInfo
    val content : String?, //적을 내용
    @ColumnInfo
    val createDate: String = LocalDateTime.now().toString(),
    @ColumnInfo
    val date : String?, //날짜
    @ColumnInfo
    val expenditure : Long?, //지출
    @ColumnInfo
    val income : Long?, //호득, 수입 등
)


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