package com.think.accessibility.activity

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.think.accessibility.R

class TestWebActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_web)

        webView = findViewById(R.id.webView)
        webView.loadUrl("https://www.baidu.com")

    }
}