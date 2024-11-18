package com.purang.financial_ledger.repository

import androidx.lifecycle.LiveData
import com.purang.financial_ledger.room_db.FinancialDao
import com.purang.financial_ledger.room_db.FinancialEntity
import javax.inject.Inject

class FinancialRepository @Inject constructor(private val financialDao: FinancialDao) {
    // 모든 가계부 데이터 가져오기
    fun getAllFinancials(): LiveData<List<FinancialEntity>> = financialDao.getFinancialList()

    // 특정 날짜의 이벤트 가져오기
    fun getEventsByDate(date: String): LiveData<List<FinancialEntity>> = financialDao.getEventsByDate(date)

    //fun getEventsByMonth(year : String, month : String) : LiveData<List<FinancialEntity>> = financialDao.getEventsByMonth(year, month)

    //
    fun getEventsByMonth(year: String, month: String): LiveData<List<FinancialEntity>> {
        return financialDao.getEventsByMonth(year, month)
    }

    fun getEventsById(id : Long?) : LiveData<FinancialEntity> = financialDao.getEventsById(id)

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