package com.example.littlelemoncapstone

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

/*
* In this file, create the Database and Dao.
* Also create an entity data class representing the menu item with the same attributes as the menu item decoded from the JSON.
* */


@Database(entities = [MenuItem::class], version = 1)
abstract class MenuDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao
}

@Entity
data class MenuItem(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val image: String,
    val category: String
)

@Dao
interface MenuDao {
    @Query("SELECT * FROM MenuItem")
    fun getAll(): List<MenuItem>

    @Insert
    fun insert(menuItem: MenuItem)
}