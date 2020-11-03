package com.think.vpn;

import android.util.Log;
import android.util.SparseArray;

import com.think.core.util.IoUtils;
import com.think.core.util.LogUtils;
import com.think.vpn.tunnel.AbstractTunnel;
import com.think.vpn.tunnel.RawTunnel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 本地代理服务器，数据的中转站
 * vpn 网卡收到数据后由VpnService进行接收
 * 收到TCP数据后，修改数据网络走向，再写入Vpn网卡，
 * vpn网卡将收到的数据转发到接受的应用，也就是本地的Tcp代理服务器
 * 相当于应用Socket -> 本地TCP代理服务器，在本地TCP代理服务器响应数据的时候
 * 进行拦截（创建新的连接去请求真实数据），从而将数据由本地TCP服务器响应到真实的应用中
 * <p>方案一：在拦截到应用数据后直接创建socket进行转发，收到数据后进行网卡写入（待验证）</p>
 * <p>方案二：使用tcp本地代理转发，全部数据都经过tcp本地代理，在tcpServer中创建socket转发（当前实现）</p>
 *
 * @author zzp
 * @date 2020-10-31
 */
public class LocalTcpProxyServer implements Runnable {

    private static final String TAG = LocalTcpProxyServer.class.getSimpleName();

    /**
     * 最大session数量
     */
    private static final long MAX_SESSION_SIZE = 60;
    /**
     * session过期间隔
     */
    private static final long SESSION_TIMEOUT_NS = 60 * 1000000000L;


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
    /**
     * VPN连接管理，用于protect客户端连接
     */
    private final VpnConnection mVpnConnection;
    /**
     * 本地连接的session，使用端口号作为key
     */
    private final SparseArray<Session> mActiveSession = new SparseArray<Session>();

    public LocalTcpProxyServer(VpnConnection vpnConnection, int localServerPort) {
        this.mVpnConnection = vpnConnection;
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
            Log.i(TAG, "正在监听端口：" + (mLocalServerPort & 0xFFFF));
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "本地TCP服务器创建出现错误");
        }
    }

    public int getProxyPort(){
        return mLocalServerPort;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                if (mSelector.select(1) > 0) {
                    Iterator<SelectionKey> iterator = mSelector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isValid()) {
                            if (selectionKey.isAcceptable()) {
                                onAccept(selectionKey);
                            } else if (selectionKey.isReadable()) {
                                Log.e(TAG,"本地TCP轮询 可读事件");
                                Object attachment = selectionKey.attachment();
                                if (attachment instanceof AbstractTunnel) {
                                    Log.e(TAG,"可读事件交由隧道处理");
                                    ((AbstractTunnel) attachment).onReadable(selectionKey);
                                }
                            } else if (selectionKey.isWritable()) {
                                Log.e(TAG,"本地TCP轮询 可写事件");
                                Object attachment = selectionKey.attachment();
                                if (attachment instanceof AbstractTunnel) {
                                    Log.e(TAG,"可写事件交由隧道处理");
                                    ((AbstractTunnel) attachment).onWritable(selectionKey);
                                }
                            }
                        }
                        iterator.remove();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            LogUtils.error(TAG, "finally 方法调用");
            IoUtils.close(mServerSocketChannel, mSelector);
        }
    }

    private void onAccept(SelectionKey selectionKey) {
        try {
            // 同理 ((ServerSocketChannel)selectionKey.channel()).accept();
            SocketChannel localChannel = mServerSocketChannel.accept();
            Log.e(TAG, "本地服务器接收到通道连接事件，本地客户端：" + localChannel + "已连接");
            // 创建一个隧道用于建立本地与远程的连接
            AbstractTunnel localTunnel = new RawTunnel(mVpnConnection,mSelector,localChannel);
            localTunnel.connect(getDestAddress(localChannel));
            localChannel.register(mSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    InetSocketAddress getDestAddress(SocketChannel localChannel) {
        short portKey = (short) localChannel.socket().getPort();
        Session session = getSession(portKey);
        if (session != null) {
            return new InetSocketAddress(localChannel.socket().getInetAddress(), session.remotePort & 0xFFFF);
        }
        return null;
    }

    private void start() {

    }

    private void stop() {

    }

    public Session createSession(int localPort, int remoteIp, int remotePort) {
        if (mActiveSession.size() > MAX_SESSION_SIZE) {
            clearExpiredSessions();
        }

        Session session = new Session();
        session.remoteIP = remoteIp;
        session.remotePort = remotePort;
        session.lastNanoTime = System.nanoTime();
        mActiveSession.put(localPort, session);
        return session;
    }

    public Session getSession(int localPort) {
        return mActiveSession.get(localPort);
    }

    private void clearExpiredSessions() {
        long now = System.nanoTime();
        for (int i = mActiveSession.size() - 1; i >= 0; i--) {
            Session session = mActiveSession.valueAt(i);
            if (now - session.lastNanoTime > SESSION_TIMEOUT_NS) {
                mActiveSession.removeAt(i);
            }
        }
    }

}
