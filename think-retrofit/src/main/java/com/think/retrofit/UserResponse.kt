package com.think.retrofit

class  UserResponse<User>{
    var code: Int = 0
    set(value) {
        field = value
    }
    var msg: String = ""
    set(value) {
        field = value
    }
    var data: User? = null
    set(value){
        field = value
    }

    override fun toString(): String {
        return "UserResponse(code=$code, msg='$msg', data=$data)"
    }


}