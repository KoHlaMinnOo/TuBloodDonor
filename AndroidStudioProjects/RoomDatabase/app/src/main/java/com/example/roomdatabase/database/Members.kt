package com.example.roomdatabase.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
class Members(
    @PrimaryKey(autoGenerate = true)
    var id: Int = -1,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "phone_number")
    var phoneNumber: Int = -1,

    @ColumnInfo(name = "weight")
    var weight: Int = -1,

    @ColumnInfo(name = "blood_type")
    var bloodType: String = "",

    @ColumnInfo(name = "gender")
    var gender: String = "",

    @ColumnInfo(name = "birth_date")
    var birthDate: Long = 0L

)

@Entity(tableName = "test")
class Test(
    @PrimaryKey(autoGenerate = true)
    var id: Int? =-1,

    @ColumnInfo(name = "name")
    var name: String?,

    @ColumnInfo(name = "password")
    var password: String?


)
