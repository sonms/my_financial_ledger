package com.purang.financial_ledger.room_db

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@RequiresApi(Build.VERSION_CODES.O)
@Database(entities = [FinancialEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getFinancialDao(): FinancialDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "financial-database"  // 데이터베이스 이름
            )
                //.fallbackToDestructiveMigration() // 마이그레이션 추가
                .build()

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        // 데이터베이스 버전 1에서 2로의 마이그레이션 정의
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 기존 테이블에 'type' 컬럼 추가
                db.execSQL("ALTER TABLE TodoDataTable ADD COLUMN type TEXT")
                // 'eventDate' 컬럼 추가 (기존에는 없었다고 가정)
                db.execSQL("ALTER TABLE TodoDataTable ADD COLUMN eventDate TEXT")
            }
        }
    }
}