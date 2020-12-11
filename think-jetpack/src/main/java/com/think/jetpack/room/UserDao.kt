package com.think.jetpack.room

import androidx.room.*
import com.think.jetpack.databind.User

/**
 * 定义接口类
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM tb_user")
    fun getAll():List<User>

    @Query("SELECT * FROM tb_user WHERE id = :id")
    fun queryById(id: Int): User

    @Insert
    fun insert(user: User)

    @Insert
    fun insertAll(vararg users: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)


}