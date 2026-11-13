package com.example.gestiondearriendos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Reserva::class, Pago::class, Gastos::class],
    version = 3,
    exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun reservaDao(): ReservaDao
    abstract fun pagoDao(): PagoDao
    abstract fun gastoDao(): GastoDao
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "arriendos_db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }

}