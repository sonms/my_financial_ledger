package com.purang.financial_ledger.view_model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.purang.financial_ledger.repository.FinancialRepository
import com.purang.financial_ledger.room_db.FinancialEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: FinancialRepository
) : ViewModel() {
    val financialListData: LiveData<List<FinancialEntity>> = repository.getAllFinancials()

    // 현재 선택된 날짜를 저장하는 LiveData
    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    // _selectedDate가 변경될 때마다 자동으로 업데이트되는 calendarTodoList
    val calendarFinancialList: LiveData<List<FinancialEntity>> = _selectedDate.switchMap { date ->
        repository.getEventsByDate(date)
    }

    // 현재 선택된 월의 모든 이벤트를 저장하는 LiveData
    private val _selectedMonth = MutableLiveData<YearMonth>()

    @RequiresApi(Build.VERSION_CODES.O)
    val selectedMonthEvents: LiveData<List<FinancialEntity>> = _selectedMonth.switchMap { month ->
        val formattedMonth = String.format("%02d", month.monthValue) // 월을 두 자리로 포맷
        repository.getEventsByMonth(month.year.toString(), formattedMonth)
    }


    ///////////////////////////////////////////////

    fun fetchEventsByMonth(yearMonth: YearMonth) {
        _selectedMonth.value = yearMonth
    }
}