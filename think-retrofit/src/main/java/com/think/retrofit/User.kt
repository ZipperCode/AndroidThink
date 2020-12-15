package com.think.retrofit

import java.text.SimpleDateFormat
import java.util.*

class User() {
    var fid:String? = null
    set(value) {field = value}
    var id: String? = null
    set(value) {
        field = value
    }

    var username: String  ? = null
    set(value) {field = value}

    var password: String? = null
    set(value) {field = value}

    var name:String? = null
    set(value) {field = value}

    var birthday: Date? = null
    set(value) {field = value}
    var age: Int = 0
    set(value) {field = value}

    var balance:Double = 0.0
    set(value){
        field = value
    }
    var friends:List<User>? = null
    set(value) {field = value}

    override fun toString(): String {
        return "User(fid=$fid, id=$id, username=$username, name=$name, birthday=$birthday, age=$age, balance=$balance, friends=$friends)"
    }


}