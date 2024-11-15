package com.purang.financial_ledger.room_db.category

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.purang.financial_ledger.room_db.FinancialEntity

@Dao
interface CategoryDao {

    @Query("SELECT * FROM CategoryTable")
    fun getAllCategory(): LiveData<List<CategoryEntity>>

    @Insert
    suspend fun insertCategory(category: CategoryEntity) // Room과 ViewModel의 비동기 처리 일관성을 위해 suspend로 변경

    @Delete
    suspend fun deleteCategory(category: CategoryEntity) // Category 삭제
}