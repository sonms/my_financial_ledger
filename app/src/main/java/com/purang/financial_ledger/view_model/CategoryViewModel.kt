package com.purang.financial_ledger.view_model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.purang.financial_ledger.repository.CategoryRepository
import com.purang.financial_ledger.repository.FinancialRepository
import com.purang.financial_ledger.room_db.FinancialEntity
import com.purang.financial_ledger.room_db.category.CategoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    repository: CategoryRepository
) : ViewModel() {
    private val categoryRepo = repository
    val categoryData: LiveData<List<CategoryEntity>> = categoryRepo.getAllCategory()

    fun getCategoryItemById(id: Long?) {
        viewModelScope.launch {
            categoryRepo.getEventsById(id)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun addCategory(name: String) {
        viewModelScope.launch {
            val newCategory = CategoryEntity(
                categoryName = name
            )
            categoryRepo.insertCategory(newCategory)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteCategory(data : CategoryEntity) {
        viewModelScope.launch {
            categoryRepo.deleteCategory(data)
        }
    }
}