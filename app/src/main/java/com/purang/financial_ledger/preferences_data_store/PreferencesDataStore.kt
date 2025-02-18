package com.purang.financial_ledger.preferences_data_store

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object PreferencesDataStore {
    private val Context.dataStore by preferencesDataStore(name = "settings")

    private val EntireIncome = stringPreferencesKey("EntireIncome")
    private val EntireExpenditure = stringPreferencesKey("EntireExpenditure")
    private val STATE_SET = booleanPreferencesKey("state")
    private val BUDGET_SET = stringPreferencesKey("budget")

    suspend fun saveBudget(context: Context, budget: String) {
        context.dataStore.edit { preferences ->
            preferences[BUDGET_SET] = budget
        }
    }

    fun getBudget(context: Context): Flow<String?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[BUDGET_SET] ?: "0"
            }
    }


    suspend fun saveEntireIncome(context: Context, income: String) {
        context.dataStore.edit { preferences ->
            preferences[EntireIncome] = income
        }
    }

    fun getEntireIncome(context: Context): Flow<String?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[EntireIncome] ?: "No income set"
            }
    }

    suspend fun saveEntireExpenditure(context: Context, expenditure: String) {
        context.dataStore.edit { preferences ->
            preferences[EntireIncome] = expenditure
        }
    }

    // Function to read the string from the DataStore
    fun getEntireExpenditure(context: Context): Flow<String?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[EntireIncome] ?: "No expenditure set"
            }
    }


    suspend fun saveState(context: Context, state: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[STATE_SET] = state
        }
    }

    // Function to read the string from the DataStore
    fun getState(context: Context): Flow<Boolean?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[STATE_SET] ?: false
            }
    }
}