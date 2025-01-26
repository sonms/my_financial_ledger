package com.purang.financial_ledger.repository

import androidx.lifecycle.LiveData
import com.purang.financial_ledger.room_db.category.CategoryDao
import com.purang.financial_ledger.room_db.category.CategoryEntity
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val categoryDao: CategoryDao) {

    fun getAllCategory(): LiveData<List<CategoryEntity>> = categoryDao.getAllCategory()

    fun getEventsById(id : Long?) : LiveData<CategoryEntity> = categoryDao.getCategoryItemById(id)


    suspend fun insertCategory(data: CategoryEntity) {
        categoryDao.insertCategory(data)
    }

    suspend fun deleteCategory(data : CategoryEntity) {
        categoryDao.deleteCategory(data)
    }
}