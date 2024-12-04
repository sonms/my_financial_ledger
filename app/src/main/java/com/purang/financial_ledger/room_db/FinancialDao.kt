package com.purang.financial_ledger.room_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.purang.financial_ledger.model.TotalIncomeExpenditure

@Dao
interface FinancialDao {
    @Query("SELECT * FROM FinancialTable ORDER BY date DESC")
    fun getFinancialList(): LiveData<List<FinancialEntity>> // 변경: List<TodoEntity> -> LiveData<List<TodoEntity>>

    @Query("SELECT * FROM FinancialTable WHERE date = :date ORDER BY date DESC")
    fun getEventsByDate(date: String): LiveData<List<FinancialEntity>> // 특정 날짜의 이벤트 조회

    @Query("SELECT * FROM FinancialTable WHERE strftime('%Y', date) = :year AND strftime('%m', date) = :month ORDER BY date DESC")
    fun getEventsByMonth(year: String, month: String): LiveData<List<FinancialEntity>>

    /*@Query("SELECT * FROM FinancialTable WHERE strftime('%Y', date) = :year AND strftime('%m', date) = :month AND strftime('%d', date) = :date ORDER BY date DESC")
    fun getClickCalendarEvents(year: String, month: String, date: String): LiveData<List<FinancialEntity>>*/
    @Query("""
    SELECT * 
    FROM FinancialTable 
    WHERE date BETWEEN :startDate AND :endDate
    ORDER BY date DESC
""")
    fun getClickCalendarEvents(startDate: String, endDate: String): LiveData<List<FinancialEntity>>

    @Query("SELECT * FROM FinancialTable WHERE id = :id")
    suspend fun getEventsById(id: Long?): FinancialEntity?

    @Query("""
    SELECT 
        SUM(income) AS totalIncome, 
        SUM(expenditure) AS totalExpenditure 
    FROM FinancialTable 
    WHERE strftime('%Y', date) = :year 
    AND strftime('%m', date) = :month
""")
    fun getTotalIncomeExpenditureByMonth(year: String, month: String): LiveData<TotalIncomeExpenditure>

    @Query("SELECT DISTINCT strftime('%Y-%m', date) AS yearMonth FROM FinancialTable ORDER BY yearMonth")
    fun getDistinctYearMonths(): LiveData<List<String>>

    //전년도 같은 월 비교
    @Query("""
    SELECT 
        SUM(income) AS totalIncome, 
        SUM(expenditure) AS totalExpenditure 
    FROM FinancialTable 
    WHERE strftime('%Y', date) = :year 
    AND strftime('%m', date) = :month
""")
    fun getBeforeTotalIncomeExpenditureByYearMonth(year: String, month: String): LiveData<TotalIncomeExpenditure>

    /*@Query("""
    SELECT * FROM FinancialTable 
    WHERE strftime('%Y', date) = :year 
    AND strftime('%m', date) = :month 
    AND (categoryId = :categoryId OR (:categoryId IS NULL AND categoryId IS NULL)) 
    ORDER BY date DESC
""")*/ // 이건 categoryId = null일 때도 적용되게

    //categoryd = null이면 해당달의 데이터만
    @Query("""
    SELECT * FROM FinancialTable
    WHERE strftime('%Y', date) = :year 
      AND strftime('%m', date) = :month
      AND (:categoryId IS NULL OR categoryId = :categoryId)
    ORDER BY date DESC
""")
    fun getEventsByMonthAndCategory(year: String, month: String, categoryId: Long?): LiveData<List<FinancialEntity>>


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





    ///////////////////////////////////////
    //category 카테고리
    @Query("""
    SELECT * 
    FROM FinancialTable 
    WHERE (:categoryId IS NULL AND categoryId IS NULL) 
       OR (categoryId = :categoryId)
""")
    fun getFinancialByCategoryId(categoryId: Long?): LiveData<List<FinancialEntity>>

    // 예를 들어, Category가 삭제될 때 ForeignKey.SET_NULL로 인해 categoryId가 null인 데이터를 조회하는 쿼리도 작성할 수 있습니다.
    @Query("SELECT * FROM FinancialTable WHERE categoryId IS NULL")
    fun getFinancialWithoutCategory(): LiveData<List<FinancialEntity>>
}