package com.purang.financial_ledger.room_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FinancialDao {
    @Query("SELECT * FROM FinancialTable ORDER BY date DESC")
    fun getFinancialList(): LiveData<List<FinancialEntity>> // 변경: List<TodoEntity> -> LiveData<List<TodoEntity>>

    @Query("SELECT * FROM FinancialTable WHERE date = :date ORDER BY date DESC")
    fun getEventsByDate(date: String): LiveData<List<FinancialEntity>> // 특정 날짜의 이벤트 조회

    @Query("SELECT * FROM FinancialTable WHERE strftime('%Y', date) = :year AND strftime('%m', date) = :month")
    fun getEventsByMonth(year: String, month: String): LiveData<List<FinancialEntity>>


    @Insert
    suspend fun insertFinancialList(financialData: FinancialEntity) // Room과 ViewModel의 비동기 처리 일관성을 위해 suspend로 변경

    @Query("DELETE FROM FinancialTable WHERE id = :id")
    suspend fun deleteFinancialList(id: Long) // Room과 ViewModel의 비동기 처리 일관성을 위해 suspend로 변경

    // 전체 FinancialEntity를 업데이트하는 메서드 (기본 방법)
    @Update
    suspend fun updateFinancialData(financialData: FinancialEntity)

    // 특정 필드만 업데이트하는 메서드 (예: content, expenditure 및 income만)
    @Query("UPDATE FinancialTable SET content = :content, expenditure = :expenditure, income = :income WHERE id = :id")
    suspend fun updateFinancialFields(id: Long, content: String?, expenditure: Long?, income: Long?)
}