package com.think.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.widget.Toast;

public class NfcActivity extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;

    /**
     * NFC
     */
    private PendingIntent mPendingIntent;

    private IntentFilter[] mFilters;

    private String[][] mTechLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        initNfc();
    }

    private void initNfc() {
        // 获取NFC适配器
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "当前手机不支持NFC", Toast.LENGTH_LONG).show();
        } else if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC功能被关闭了", Toast.LENGTH_LONG).show();
        } else {
            // 声明一个意图，用于打开当前窗口，一般情况下NFC接触之后需要一个处理的窗口，比如常见的公交卡，刷卡后会显示一个卡面
            Intent intent = new Intent(this, NfcActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            // 声明一个NFC卡片探测事件的相应动作，PendingIntent主要是用来携带Intent的
            // 某些情况下并不需要立马处理一个Intent，就可以用PendingIntent
            mPendingIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            try {
                // 定义一个过滤器（检测到NFC卡片）
                mFilters = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED, "*/*")};
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 读标签之前先确定标签类型，技术类型比如：NfcA一般用于门禁卡，Nfc一般用于身份证等
            mTechLists = new String[][]{new String[]{NfcA.class.getName()}, {IsoDep.class.getName()}};
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null && mNfcAdapter.isEnabled() && mPendingIntent != null && mFilters != null && mTechLists != null) {
            // 为本App启用NFC感应
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
    }

}