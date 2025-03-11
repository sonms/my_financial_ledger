package com.purang.financial_ledger.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.purang.financial_ledger.repository.CategoryRepository
import com.purang.financial_ledger.room_db.category.CategoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    repository: CategoryRepository
) : ViewModel() {
    private val categoryRepo = repository

    private val defaultCategory = CategoryEntity(id = -1, categoryName = "미분류")

    val categoryData: LiveData<List<CategoryEntity>> = categoryRepo.getAllCategory().switchMap { list ->
        val updatedList = (list + defaultCategory).distinctBy { it.id }.sortedBy { it.id }
        MutableLiveData(updatedList)
    }

    fun getCategoryItemById(id: Long?) {
        viewModelScope.launch {
            categoryRepo.getEventsById(id)
        }
    }


    fun addCategory(name: String) {
        viewModelScope.launch {
            val newCategory = CategoryEntity(
                categoryName = name
            )
            categoryRepo.insertCategory(newCategory)
        }
    }

    fun deleteCategory(data : CategoryEntity) {
        viewModelScope.launch {
            categoryRepo.deleteCategory(data)
        }
    }
}