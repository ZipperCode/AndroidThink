package com.think.vpn;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class TcpProxy implements Runnable, Closeable {


    private SocketChannel mClientSocketChannel;

    private ByteBuffer mByteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);


    public TcpProxy(VpnConnection vpnConnection,InetSocketAddress inetSocketAddress){
        try {
            mClientSocketChannel = SocketChannel.open();
            mClientSocketChannel.connect(inetSocketAddress);
            vpnConnection.protect(mClientSocketChannel.socket());
            System.out.println("客户端启动成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(mClientSocketChannel != null){
                    mClientSocketChannel.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {

    }
}
