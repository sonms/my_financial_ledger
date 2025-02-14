package com.purang.financial_ledger.room_db

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.purang.financial_ledger.room_db.category.CategoryDao
import com.purang.financial_ledger.room_db.category.CategoryEntity
import com.purang.financial_ledger.utils.ColorConverter

@RequiresApi(Build.VERSION_CODES.O)
@Database(entities = [FinancialEntity::class, CategoryEntity::class], version = 2, exportSchema = false)
@TypeConverters(ColorConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getFinancialDao(): FinancialDao
    abstract fun getCategoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "financial-database"
            )
                .addMigrations(MIGRATION_1_2) // 기존 마이그레이션 적용
                .addMigrations(MIGRATION_2_3) // 새로운 마이그레이션 추가
                .build()

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE TodoDataTable ADD COLUMN type TEXT")
                db.execSQL("ALTER TABLE TodoDataTable ADD COLUMN eventDate TEXT")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // FinancialTable에 selectColor 컬럼 추가 (기본값 0 설정)
                db.execSQL("ALTER TABLE FinancialTable ADD COLUMN selectColor INTEGER DEFAULT 0")
            }
        }
    }
}