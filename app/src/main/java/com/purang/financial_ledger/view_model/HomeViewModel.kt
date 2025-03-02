package com.purang.financial_ledger.view_model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.purang.financial_ledger.model.TotalIncomeExpenditure
import com.purang.financial_ledger.repository.FinancialRepository
import com.purang.financial_ledger.room_db.FinancialEntity
import com.purang.financial_ledger.utils.ColorConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: FinancialRepository
) : ViewModel() {
    private val financialRepo = repository

    val financialListData: LiveData<List<FinancialEntity>> = financialRepo.getAllFinancials()

    val getDistinctYearMonthsData : LiveData<List<String>> = financialRepo.getDistinctYearMonths()


    // LiveData to track the selected month
    private val _selectedMonth = MutableLiveData<YearMonth>()
    private val _selectedBeforeYearMonth = MutableLiveData<YearMonth>()
    private val _selectedBeforeMonth = MutableLiveData<YearMonth>()
    private val _categoryId = MutableLiveData<Long?>()
    private val _clickCalendar = MutableLiveData<String>()

    //약 데이터 이동 관찰용
    private val _dateMoveData = MutableStateFlow<String?>(null)

    // 외부에서 읽기 전용 StateFlow로 노출
    val dateMoveData: StateFlow<String?> = _dateMoveData.asStateFlow()

    // 데이터 갱신 함수
    fun updateMoveData(data: String) {
        viewModelScope.launch {
            _dateMoveData.value = data
        }
    }


    val clickCalendarData : LiveData<List<FinancialEntity>> = _clickCalendar.switchMap {date ->
        financialRepo.getClickCalendarData(date, date)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    val selectedMonthEventsWithCategory: LiveData<List<FinancialEntity>> =
        _selectedMonth.switchMap { month ->
            _categoryId.switchMap { categoryId ->
                if (month != null) {
                    val formattedMonth = String.format("%02d", month.monthValue) // Format month as two digits
                    financialRepo.getEventsByMonth(
                        year = month.year.toString(),
                        month = formattedMonth,
                        categoryId = categoryId
                    )
                } else {
                    MutableLiveData(emptyList())
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val sortedMonthEvents: LiveData<List<FinancialEntity>> = selectedMonthEventsWithCategory.map { events ->
        if (isAscending) events.sortedBy { it.date }
        else events.sortedByDescending { it.date }
    }



    private var isAscending = true //true = 오름, false = 내림

    fun toggleSortOrder(isSortCheck : Boolean) {
        isAscending = isSortCheck
        _selectedMonth.value = _selectedMonth.value // 트리거
    }

    // Function to set the selected month
    fun fetchEventsByMonth(yearMonth: YearMonth) {
        _selectedMonth.value = yearMonth
    }

    fun fetchBeforeByYearMonth(yearMonth: YearMonth) {
        _selectedBeforeYearMonth.value = yearMonth
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchBeforeByMonth(yearMonth: YearMonth) {
        _selectedBeforeMonth.value = yearMonth
    }

    fun fetchCategoryId(categoryId: Long?) {
        _categoryId.value = categoryId
    }

    fun fetchCalendarDate(date : String) {
        _clickCalendar.value = date
    }

    //카테고리 id로 데이터 가져오기
    val getFinancialDataByCategoryId : LiveData<List<FinancialEntity>> = _categoryId.switchMap {
        financialRepo.getSearchDataByCategoryId(it)
    }


    ///////////////////////////////////////////////

    // 선택된 ID를 담을 StateFlow
    private val _selectedId = MutableStateFlow<Long?>(null)

    // 선택된 ID에 해당하는 데이터를 담을 StateFlow
    private val _selectedFinancialItem = MutableStateFlow<FinancialEntity?>(null)

    // 외부에서 관찰할 수 있도록 public StateFlow
    val selectedFinancialItem: StateFlow<FinancialEntity?> = _selectedFinancialItem

    // ID에 따라 데이터를 가져오는 함수
    fun setSelectedId(id: Long?) {
        if (_selectedId.value == id) {
            refreshSelectedItem()
        } else {
            _selectedId.value = id
        }
    }

    fun refreshSelectedItem() {
        viewModelScope.launch {
            _selectedId.value?.let { id ->
                val event = financialRepo.getEventsById(id)
                _selectedFinancialItem.value = event
            }
        }
    }
    init {
        // _selectedId가 변경될 때마다 이벤트를 처리하도록 설정
        viewModelScope.launch {
            _selectedId.collect { id ->
                // ID가 변경될 때마다 해당 ID에 맞는 데이터를 가져와서 _selectedFinancialItem에 업데이트
                if (id != null) {
                    refreshSelectedItem()
                } else {
                    _selectedFinancialItem.value = null
                }
            }
        }
    }

    // LiveData that fetches events for the selected month
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedMonthTotals: LiveData<TotalIncomeExpenditure> = _selectedMonth.switchMap { month ->
        val formattedMonth = String.format("%02d", month.monthValue) // Format month as two digits
        financialRepo.getTotalIncomeExpenditure(month.year.toString(), formattedMonth)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val selectedBeforeYearMonthTotals: LiveData<TotalIncomeExpenditure> = _selectedBeforeYearMonth.switchMap { month ->
        val formattedMonth = String.format("%02d", month.monthValue) // Format month as two digits
        val year = month.year - 1
        financialRepo.getBeforeTotalIncomeExpenditureByYearMonth(year.toString(), formattedMonth)
    }

    /*@RequiresApi(Build.VERSION_CODES.O)
    val selectedBeforeMonthTotals: LiveData<TotalIncomeExpenditure> = _selectedBeforeMonth.switchMap { month ->
        if (month.monthValue - 1 == 0) {
            val formattedMonth = String.format("%02d", 12) // Format month as two digits
            Log.e("select3", "Fetching events for: ${month.year}-$formattedMonth")
            financialRepo.getBeforeTotalIncomeExpenditureByMonth((month.year-1).toString(), formattedMonth)
        } else {
            val formattedMonth = String.format("%02d", month.monthValue-1) // Format month as two digits
            Log.e("select4", "Fetching events for: ${month.year}-$formattedMonth")
            financialRepo.getBeforeTotalIncomeExpenditureByMonth(month.year.toString(), formattedMonth)
        }
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    val selectedBeforeMonthTotals: LiveData<TotalIncomeExpenditure> = _selectedBeforeMonth.switchMap { month ->
        val (year, formattedMonth) = if (month.monthValue == 1) {
            month.year - 1 to String.format("%02d", 12)
        } else {
            month.year to String.format("%02d", month.monthValue - 1)
        }
        financialRepo.getBeforeTotalIncomeExpenditureByMonth(year.toString(), formattedMonth)
    }






    @RequiresApi(Build.VERSION_CODES.O)
    fun addFinancialData(categoryId : Long?, title : String?, content : String?, date : String?, expenditure:Long?, income:Long?, selectColor : Color?) {
        /*
        *  val categoryId: Long?,
    val content: String?,
    val createDate: String = LocalDateTime.now().toString(),
    val date: String?,
    val expenditure: Long?,
    val income: Long?*/

        viewModelScope.launch {
            val newTodo = ColorConverter().fromColor(selectColor)?.let {
                FinancialEntity(
                    categoryId = categoryId,
                    title = title,
                    content = content,
                    date = date,
                    expenditure = expenditure,
                    income = income,
                    selectColor = it
                )
            }
            if (newTodo != null) {
                financialRepo.insertData(newTodo)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateFinancialData(id: Long, categoryId : Long?, title : String?, content : String?, date : String?, expenditure:Long?, income:Long?,  selectColor : Color?) {
        /*
        *  val categoryId: Long?,
    val content: String?,
    val createDate: String = LocalDateTime.now().toString(),
    val date: String?,
    val expenditure: Long?,
    val income: Long?*/

        viewModelScope.launch {
            val newTodo = ColorConverter().fromColor(selectColor)?.let {
                FinancialEntity(
                    id = id,
                    categoryId = categoryId,
                    title = title,
                    content = content,
                    date = date,
                    expenditure = expenditure,
                    income = income,
                    selectColor = it
                )
            }
            if (newTodo != null) {
                financialRepo.updateData(newTodo)
            }
        }
    }

    fun deleteFinancialData(financialEntity: FinancialEntity) {
        viewModelScope.launch {
            financialRepo.deleteDataById(financialEntity.id)
        }
    }
}