package com.purang.financial_ledger.room_db

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.purang.financial_ledger.room_db.category.CategoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "financial-database"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2) // 기존 마이그레이션
            .addMigrations(AppDatabase.MIGRATION_2_3) // 새 마이그레이션 추가
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    fun provideFinancialDao(database: AppDatabase): FinancialDao {
        return database.getFinancialDao()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Singleton
    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.getCategoryDao()
    }
}