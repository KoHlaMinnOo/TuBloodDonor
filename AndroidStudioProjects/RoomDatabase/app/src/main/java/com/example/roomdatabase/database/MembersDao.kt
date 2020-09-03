package com.example.roomdatabase.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Query as Query

@Dao
interface MembersDao {

    @Insert
    fun memberInsert(members: Members)

    @Update
    fun memberUpdate(members: Members)

    @Query("Select * From members Where id= :id Order By id Desc")
    fun getMember(id:Int):Members?

    @Query("Select * From members Where blood_type= :bloodType Order By id Desc")
    fun getAllMembers(bloodType:String):Members

    @Query("Delete From members Where id= :key")
    fun deleteMember(key: Int)

}

@Dao
interface TestDao{
    @Insert
    fun inset(test: Test)

    @Query("select * from test")
    fun getTest():LiveData<List<Test>>
}