package com.purang.financial_ledger.repository

import androidx.lifecycle.LiveData
import com.purang.financial_ledger.model.TotalIncomeExpenditure
import com.purang.financial_ledger.room_db.FinancialDao
import com.purang.financial_ledger.room_db.FinancialEntity
import javax.inject.Inject

class FinancialRepository @Inject constructor(private val financialDao: FinancialDao) {
    // 모든 가계부 데이터 가져오기
    fun getAllFinancials(): LiveData<List<FinancialEntity>> = financialDao.getFinancialList()

    // 특정 날짜의 이벤트 가져오기
    fun getEventsByDate(date: String): LiveData<List<FinancialEntity>> = financialDao.getEventsByDate(date)

    //데이터가 존재하는 년월 가져오기
    fun getDistinctYearMonths() : LiveData<List<String>> {
        return financialDao.getDistinctYearMonths()
    }
    //
    fun getEventsByMonth(year: String, month: String, categoryId: Long?): LiveData<List<FinancialEntity>> {
        return financialDao.getEventsByMonthAndCategory(year, month, categoryId)
    }

    fun getTotalIncomeExpenditure(year: String, month: String): LiveData<TotalIncomeExpenditure> {
        return financialDao.getTotalIncomeExpenditureByMonth(year, month)
    }

    fun getBeforeTotalIncomeExpenditureByYearMonth(year: String, month: String) : LiveData<TotalIncomeExpenditure> {
        return financialDao.getTotalIncomeExpenditureByMonth(year, month)
    }

    fun getBeforeTotalIncomeExpenditureByMonth(year: String, month: String) : LiveData<TotalIncomeExpenditure> {
        return financialDao.getTotalIncomeExpenditureByMonth(year, month)
    }

    fun getSearchDataByCategoryId(categoryId : Long?) : LiveData<List<FinancialEntity>> {
        return financialDao.getFinancialByCategoryId(categoryId)
    }


    // 특정 ID에 해당하는 데이터를 가져오는 함수
    suspend fun getEventsById(id: Long?): FinancialEntity? {
        return id?.let {
            financialDao.getEventsById(id)
        }
    }

    // 할 일 삽입
    suspend fun insertData(data: FinancialEntity) {
        financialDao.insertFinancialList(data)
    }

    //데이터 수정
    suspend fun updateData(data : FinancialEntity) {
        financialDao.updateFinancialData(data)
    }

    // 할 일 삭제
    suspend fun deleteDataById(id: Long) {
        financialDao.deleteFinancialList(id)
    }
}