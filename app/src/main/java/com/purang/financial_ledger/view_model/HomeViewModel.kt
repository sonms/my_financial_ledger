package com.purang.financial_ledger.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.purang.financial_ledger.repository.FinancialRepository
import com.purang.financial_ledger.roomDB.FinancialEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: FinancialRepository
) : ViewModel() {
    val financialListData: LiveData<List<FinancialEntity>> = repository.getAllFinancials()


}