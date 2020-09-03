package com.example.roomdatabase.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Members::class], version = 1, exportSchema = false)
abstract class DataBase : RoomDatabase() {
    abstract val membersDao:MembersDao

    companion object{
        @Volatile
        private var INSTANCE : DataBase? =null

        fun getInstance(context: Context):DataBase{
            synchronized(this){
                var instance = INSTANCE
                if (instance==null){
                    instance=Room.databaseBuilder(
                        context.applicationContext,
                        DataBase::class.java,
                        "blood_donation"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE=instance
                }
                return instance
            }
        }
    }
}

@Database(entities = [Test::class],version = 1,exportSchema = false)
abstract class TestDatabase:RoomDatabase(){
    abstract val testDao:TestDao
    companion object{
        @Volatile
        private  var INSTANCE:TestDatabase?=null

        fun getInstance(context: Context):TestDatabase{
            synchronized(this){
                var instance= INSTANCE
                if (instance==null){
                    instance=Room.databaseBuilder(
                        context.applicationContext,
                        TestDatabase::class.java,
                        "test_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE=instance
                }
                return instance
            }
        }
    }

}