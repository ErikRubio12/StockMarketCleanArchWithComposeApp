package com.eriklabs.stockmarketcleanarchwithcomposeapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eriklabs.stockmarketcleanarchwithcomposeapp.data.local.dao.StockDao
import com.eriklabs.stockmarketcleanarchwithcomposeapp.data.local.entities.CompanyListingEntity

@Database(
    entities = [
        CompanyListingEntity::class
    ],
    version = 1
)
abstract class StockDatabase : RoomDatabase() {
    abstract val stockDao: StockDao
}