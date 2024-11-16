package com.purang.financial_ledger.view_model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.purang.financial_ledger.repository.FinancialRepository
import com.purang.financial_ledger.room_db.FinancialEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: FinancialRepository
) : ViewModel() {
    private val financialRepo = repository

    val financialListData: LiveData<List<FinancialEntity>> = financialRepo.getAllFinancials()

    // 현재 선택된 날짜를 저장하는 LiveData
    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    // _selectedDate가 변경될 때마다 자동으로 업데이트되는 calendarTodoList
    val calendarFinancialList: LiveData<List<FinancialEntity>> = _selectedDate.switchMap { date ->
        financialRepo.getEventsByDate(date)
    }

    private val _selectedFinancialItem = MutableLiveData<FinancialEntity?>()
    val selectedFinancialItem: LiveData<FinancialEntity?> = _selectedFinancialItem

    // 현재 선택된 월의 모든 이벤트를 저장하는 LiveData
    private val _selectedMonth = MutableLiveData<YearMonth>()

    @RequiresApi(Build.VERSION_CODES.O)
    val selectedMonthEvents: LiveData<List<FinancialEntity>> = _selectedMonth.switchMap { month ->
        val formattedMonth = String.format("%02d", month.monthValue) // 월을 두 자리로 포맷
        Log.d("selectedMonthEvents", "Fetching events for ${month.year}-${formattedMonth}")
        financialRepo.getEventsByMonth(month.year.toString(), formattedMonth)
    }


    ///////////////////////////////////////////////

    fun fetchEventsByMonth(yearMonth: YearMonth) {
        _selectedMonth.value = yearMonth
    }

    fun getFinancialItemById(id: Long?) {
        id?.let {
            financialRepo.getEventsById(it).observeForever { item ->
                _selectedFinancialItem.value = item
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun addFinancialData(categoryId : Long?, title : String?, content : String?, date : String?, expenditure:Long?, income:Long?) {
        /*
        *  val categoryId: Long?,
    val content: String?,
    val createDate: String = LocalDateTime.now().toString(),
    val date: String?,
    val expenditure: Long?,
    val income: Long?*/

        viewModelScope.launch {
            val newTodo = FinancialEntity(
                categoryId = categoryId,
                title = title,
                content = content,
                date = date,
                expenditure = expenditure,
                income = income
            )
            Log.e("addFinancialData", newTodo.toString())
            financialRepo.insertData(newTodo)
        }
    }
}