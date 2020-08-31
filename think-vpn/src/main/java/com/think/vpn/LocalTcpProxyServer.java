package com.think.vpn;

import android.util.Log;

import com.think.core.util.LogUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 本地代理服务器，数据的中转站
 *
 * @author xj-134
 */
public class LocalTcpProxyServer implements Runnable {

    private static final String TAG = LocalTcpProxyServer.class.getSimpleName();

    /**
     * 本地代理服务器的端口
     */
    private int mLocalServerPort;
    /**
     * 本地服务的Selector
     */
    private Selector mSelector;
    /**
     * 服务端Channel
     */
    private ServerSocketChannel mServerSocketChannel;

    public LocalTcpProxyServer(int localServerPort) {
        try {
            mSelector = Selector.open();
            mServerSocketChannel = ServerSocketChannel.open();
            // 设置非阻塞
            mServerSocketChannel.configureBlocking(false);
            // 绑定本地端口
            mServerSocketChannel.socket().bind(new InetSocketAddress(localServerPort));
            // 注册OnAccept事件
            mServerSocketChannel.register(mSelector, SelectionKey.OP_ACCEPT);
            mLocalServerPort = mServerSocketChannel.socket().getLocalPort();
            Log.i(TAG,"正在监听端口：" + (mLocalServerPort & 0xFFFF));
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,"出现错误");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (mSelector.select(1) > 0) {
                    Iterator<SelectionKey> keyIterator = mSelector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey selectionKey = keyIterator.next();
                        if(selectionKey.isValid()){
                            if(selectionKey.isAcceptable()){
                                onAccept(selectionKey);
                            }else if (selectionKey.isReadable()) {
                                onReadable(selectionKey);
                            } else if (selectionKey.isWritable()) {
                                onWriteable(selectionKey);
                            } else if (selectionKey.isConnectable()) {

                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (mSelector != null && mSelector.isOpen()) {
                    mSelector.close();
                }
                if (mServerSocketChannel != null && mServerSocketChannel.isOpen()) {
                    mServerSocketChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onAccept(SelectionKey selectionKey){
        try {
            SocketChannel localChannel =mServerSocketChannel.accept();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onReadable(SelectionKey selectionKey){
        LogUtils.debug("onReadable : " + selectionKey);
    }

    private void onWriteable(SelectionKey selectionKey){
        LogUtils.debug("onWriteable : " + selectionKey);
    }

    private void start(){

    }

    private void stop(){

    }

}
