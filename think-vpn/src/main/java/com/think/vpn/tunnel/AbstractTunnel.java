package com.think.vpn.tunnel;

import android.net.VpnService;
import android.util.Log;

import com.think.core.util.IoUtils;
import com.think.core.util.LogUtils;
import com.think.core.util.ThreadManager;
import com.think.vpn.LocalVpnService;
import com.think.vpn.VpnConnection;

import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * 抽象的隧道实现，用于将两条客户端连接进行相连
 * 数据经过本地TCP代理服务器后，此时形成本地服务端和远程服务端
 * TCP中两个服务端无法进行连接，一个在本地，一个在远程，只能通过客户端进行连接
 * 此时需要创建两条客户端连接，一条用于连接本地，一条用于连接远程，而两条客户端连接又需要进行
 * 数据的交换，所以需要建立一个可以让两个本地的客户端想通的数据通道 <b>LocalServer</b>--Tunnel--<b>RemoteServer</b>
 * Tunnel的内实现为，一个Tunnel始终持有另一个Tunnel，当Tunnel1收到数据后调用Tunnel进行数据写入，Tunnel1写入数据后调用Tunnel进行
 *
 * @author zzp
 * @date 2020-10-31
 */
public abstract class AbstractTunnel implements Runnable, Closeable {

    static final String TAG = AbstractTunnel.class.getSimpleName();
    /**
     * 当前对象持有的socketChannel
     */
    protected SocketChannel mInnerSocketChannel;

    protected SocketChannel mRemoteSocketChannel;

    private final Selector mSelector;

    private final Selector mLocalSelector;

    private final VpnConnection mVpnConnection;

    private boolean isDisposed = false;

    private boolean isConnected = false;

    private ConcurrentLinkedDeque<ByteBuffer> mWriteData = new ConcurrentLinkedDeque<>();

    public AbstractTunnel(VpnConnection vpnConnection, Selector selector, SocketChannel innerSocketChannel) throws Exception {
        this.mVpnConnection = vpnConnection;
        this.mSelector = Selector.open();
        this.mLocalSelector = selector;
        this.mInnerSocketChannel = innerSocketChannel;
        this.mInnerSocketChannel.configureBlocking(false);
        this.mInnerSocketChannel.register(mLocalSelector, SelectionKey.OP_READ, this);
        this.mRemoteSocketChannel = SocketChannel.open();
        this.mRemoteSocketChannel.configureBlocking(false);
    }

    protected abstract void onConnected();

    protected abstract void onRead(ByteBuffer readData);

    protected abstract void onWrite(ByteBuffer writeData);


    public void connect(InetSocketAddress inetSocketAddress) throws Exception {
        if (mVpnConnection.protect(mInnerSocketChannel.socket())
                && mVpnConnection.protect(mRemoteSocketChannel.socket())) {
            // 与本地之间的连接不走VPN
            mRemoteSocketChannel.register(mSelector, SelectionKey.OP_CONNECT);
            boolean connected = mRemoteSocketChannel.connect(inetSocketAddress);
            Log.e(TAG, "连接远程的服务器连接：" + connected);
            ThreadManager.getInstance().execPoolFuture(this);
        } else {
            throw new ConnectException("protect 连接失败");
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted() && !isDisposed) {
                if (mSelector.select() > 0) {
                    Iterator<SelectionKey> iterator = mSelector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isValid()) {
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            if (selectionKey.isReadable()) {
                                onRemoteReadable(socketChannel);
                            } else if (selectionKey.isWritable()) {
                                onRemoteWritable(socketChannel);
                            } else if (selectionKey.isConnectable()) {
                                onRemoteConnectable();
                            }
                        }
                    }
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtils.close(this);
        }

    }

    /**
     * 本地tcp服务器读取的数据
     * @param selectionKey
     * @throws IOException
     */
    public void onReadable(SelectionKey selectionKey) throws IOException {
        LogUtils.debug(TAG,"收到本地TCP服务器转发的数据，保存后转发到服务器");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(LocalVpnService.MTU_PACK_SIZE);
        socketChannel.read(buffer);
        buffer.flip();
        mWriteData.offer(buffer);
        // 将远程连接通道注册为写
        mRemoteSocketChannel.register(mSelector,SelectionKey.OP_WRITE);
    }

    public void onWritable(SelectionKey selectionKey) {

    }

    /**
     * 远程服务器连接成功
     */
    private void onRemoteConnectable() {
        try {
            if (mRemoteSocketChannel.finishConnect()) {
                LogUtils.debug(TAG,"远程服务器连接成功,注册写事件");
                mRemoteSocketChannel.register(mSelector,SelectionKey.OP_WRITE);
                onConnected();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onRemoteReadable(SocketChannel socketChannel) throws IOException{
        ByteBuffer readData = ByteBuffer.allocate(LocalVpnService.MTU_PACK_SIZE);
        int size = socketChannel.read(readData);
        readData.flip();
        if(size > 0){
            // 子类数据处理
            onRead(readData);
            // 转发到本地TCP服务器
            mInnerSocketChannel.write(readData);
        }
    }

    /**
     * 本地客户端收到写事件，将娱本地服务器连接的客户端数据通过写到服务端
     * @param socketChannel
     */
    private void onRemoteWritable(SocketChannel socketChannel) throws IOException {
        // 取出本地tcp服务器转发的内容
        ByteBuffer peek = mWriteData.peek();
        if(peek != null){
            ByteBuffer writeData = mWriteData.poll();
            onWrite(writeData);
            socketChannel.write(writeData);
            // 数据发送到服务端后，注册为读事件，读取服务器返回的数据
            mRemoteSocketChannel.register(mSelector,SelectionKey.OP_READ);
        }
    }

    @Override
    public void close() throws IOException {
        ThreadManager.getInstance().cancelSchedule(this);
        if (mInnerSocketChannel.isConnected()) {
            mInnerSocketChannel.close();
        }
        if (mRemoteSocketChannel.isConnected()) {
            mRemoteSocketChannel.close();
        }
        isDisposed = true;
    }
}
