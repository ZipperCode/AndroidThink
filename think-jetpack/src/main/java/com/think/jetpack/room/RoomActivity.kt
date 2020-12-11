package com.think.jetpack.room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.think.jetpack.R
import com.think.jetpack.databind.User

class RoomActivity : AppCompatActivity() {
    private var index: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        val add = findViewById<Button>(R.id.add)
        val update = findViewById<Button>(R.id.update)
        val delete = findViewById<Button>(R.id.delete)
        val list = findViewById<Button>(R.id.select)
        val db = AppDaoUtils.openDatabase(applicationContext)
        add.setOnClickListener {
            val user = User(index++, "张三-${index}",0)
            Thread{
                db.getUserDao().insert(user)
            }.start()
        }

        update.setOnClickListener {
            val user = User(index,"李四${index}",100)
            Thread{
                db.getUserDao().update(user)
            }.start()
        }

        delete.setOnClickListener{
            val user = User(index,"",0)
            Thread{
                db.getUserDao().delete(user)
            }.start()
        }


        list.setOnClickListener{
            Thread{
                val all = db.getUserDao().getAll()

                Log.e("Room",all.toString())
            }.start()
        }

    }
}