package cn.think.eventbus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SecondActivity : AppCompatActivity() {
    companion object{
        private val TAG = SecondActivity::class.java.simpleName
    }

    @BindView(R.id.tv_message)
    lateinit var tvMessage: TextView
    @BindView(R.id.btn_finish)
    lateinit var btnSecond: Button
    @BindView(R.id.btn_third)
    lateinit var btnThird: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        ButterKnife.bind(this)
        EventBus.getDefault().register(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG,"finish")
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun onMessage(event: MessageEvent){
        Log.i(TAG,"收到粘性事件 evnet = ${event.message}")
        tvMessage.text = event.message
    }

    @OnClick(R.id.btn_finish)
    fun selfFinish(){
        val msg = MessageEvent()
        msg.message = "我是从SecondActivity回来的消息"
        EventBus.getDefault().post(msg)
        finish()
    }

    @OnClick(R.id.btn_third)
    fun toThird(){
        Log.i(TAG,"跳转到ThirdActivity")
        startActivity(Intent(this,ThirdActivity::class.java))
    }

}