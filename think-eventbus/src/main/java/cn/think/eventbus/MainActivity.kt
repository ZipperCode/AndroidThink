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

class MainActivity : AppCompatActivity() {
    companion object{
        private val TAG = MainActivity::class.java.simpleName
    }

    /**
     * 注入必须使用lateinit，不然会包属性私有或者静态的错误
     */
    @BindView(R.id.tv_message)
    lateinit var tvMessage: TextView
    @BindView(R.id.btn_second)
    lateinit var btnSecond: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        // 注册事件
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 反注册事件
        EventBus.getDefault().unregister(this)
    }

    @OnClick(R.id.btn_second)
    fun toSecond(){
        startActivity(Intent(this,SecondActivity::class.java))
        val msg = MessageEvent()
        msg.message = "我来自MainActivity的粘性消息"
        // 发送一个粘性事件（事件发送后不用等到）
        EventBus.getDefault().postSticky(msg)
        Log.i(TAG,"发送粘性消息到SecondActivity")
    }

    /**
     * 事件在主线程回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMessage(messageEvent: MessageEvent){
        tvMessage.text = messageEvent.message
    }

}