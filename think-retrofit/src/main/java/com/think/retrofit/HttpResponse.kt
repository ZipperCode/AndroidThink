package com.think.retrofit

class HttpResponse {

    var args:Map<String,*>? = null

    var data: String = ""

    var files:Map<String,*>? = null

    var form:Map<String,*>? = null

    var headers: Map<String,String>? = null;

    var json:String = ""

    var origin: String = ""

    var url:String = ""
    override fun toString(): String {
        return "HttpResponse(args=$args, data='$data', files=$files, form=$form, headers=$headers, json='$json', origin='$origin', url='$url')"
    }


}