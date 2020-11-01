package com.think.vpn.tunnel;

import android.util.Log;

import com.think.vpn.VpnConnection;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class RawTunnel extends AbstractTunnel {

    static final String TAG = RawTunnel.class.getSimpleName();

    public RawTunnel(VpnConnection vpnConnection, Selector selector, SocketChannel innerSocketChannel) throws Exception {
        super(vpnConnection, selector, innerSocketChannel);
    }

    @Override
    protected void onConnected() {
        Log.e(TAG,"通道连接成功");
    }

    @Override
    protected void onRead(ByteBuffer readData) {

    }

    @Override
    protected void onWrite(ByteBuffer writeData) {

    }
}
