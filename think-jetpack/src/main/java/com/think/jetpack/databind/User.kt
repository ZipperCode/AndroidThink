package com.think.jetpack.databind

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_user")
data class User(@PrimaryKey @ColumnInfo(name = "id")val id:Int,
                @ColumnInfo(name = "name")val name: String,
                @ColumnInfo(name = "age")val age: Int)