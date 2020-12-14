package cn.think.eventbus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import butterknife.BindView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ThirdActivity : AppCompatActivity() {

    companion object{
        private val TAG = ThirdActivity::class.java.simpleName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this);
    }

    /**
     * 从MainActivity发出的粘性事件，ThirdActivity也会收到
     */
    @Subscribe(sticky = true)
    fun onMessage(messageEvent: MessageEvent){
        Log.i(TAG,"是否还会收到粘性事件")
    }
}