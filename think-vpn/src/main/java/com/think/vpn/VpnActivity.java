package com.think.vpn;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.think.R;
import com.think.core.util.ThreadManager;
import com.think.vpn.packet.dns.DnsFlags;
import com.think.vpn.packet.dns.DnsHeader;
import com.think.vpn.packet.dns.DnsPacket;
import com.think.vpn.packet.dns.Question;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import test.Server;

public class VpnActivity extends AppCompatActivity {

    private static final String TAG = "VpnActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpn);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "start VpnService");
                Intent intent = VpnService.prepare(VpnActivity.this);
                if (intent != null) {
                    startActivityForResult(intent, 0);
                } else {
                    onActivityResult(0, RESULT_OK, null);
                }
            }
        });

        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "stop VpnService");
                startService(getServiceIntent().setAction(LocalVpnService.ACTION_DISCONNECT));
            }
        });

        findViewById(R.id.btn_dns).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DnsPacket dnsPacket = new DnsPacket();
                            DnsHeader dnsHeader = new DnsHeader();
                            dnsHeader.mTransactionId = 1;
                            dnsHeader.mQuestionCount = 1;
                            DnsFlags dnsFlags = new DnsFlags();
                            dnsHeader.mDnsFlags = dnsFlags;
                            Question question = new Question();
                            question.mQueryDomain = "www.baidu.com";
                            question.mQueryType = 1;
                            question.mQueryClass = 1;
                            dnsPacket.mHeader = dnsHeader;
                            dnsPacket.mQuestions = new Question[]{question};
                            ByteBuffer byteBuffer = ByteBuffer.allocate(512);
                            dnsPacket.toBytes(byteBuffer);
                            DatagramSocket datagramSocket = new DatagramSocket();
                            DatagramPacket datagramPacket = new DatagramPacket(byteBuffer.array(), 0, byteBuffer.limit());
                            InetSocketAddress remoteAddress = new InetSocketAddress("114.114.114.114", 53);
                            datagramPacket.setSocketAddress(remoteAddress);
                            datagramSocket.send(datagramPacket);

                            ByteBuffer receive = ByteBuffer.allocate(512);
                            DatagramPacket receivePacket = new DatagramPacket(receive.array(), receive.capacity());
                            boolean isReceive = false;
                            while (!isReceive) {
                                datagramSocket.receive(receivePacket);
                                receive.limit(receivePacket.getLength());
                                if (receivePacket.getLength() > 1) {
                                    DnsPacket dnsPacket1 = DnsPacket.parseFromBuffer(receive);
                                    System.out.println("收到的dns包为：" + dnsPacket1);
                                    isReceive = true;
                                }
                            }
                        } catch (SocketException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_http).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient();

                            Request request = new Request.Builder()
                                    .url("http://app.yeshen.com")
                                    .build();

                            try (Response response = client.newCall(request).execute()) {
                                System.out.println("http 请求响应为：" + response.body().string());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });

        findViewById(R.id.btn_tcp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Socket sock = null; // 连接指定服务器和端口
                        try {
                            System.out.println("before write hello");
                            sock = new Socket( "localhost",8888);
                            System.err.println("localPort = " + sock.getLocalPort());
                            try (InputStream input = sock.getInputStream()) {
                                try (OutputStream output = sock.getOutputStream()) {
                                    System.out.println("write hello");
                                    output.write("hello".getBytes());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                new Server();
//            }
//        }).start();
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (result == RESULT_OK) {
            startService(getServiceIntent().setAction(LocalVpnService.ACTION_CONNECT));
        }
    }

    private Intent getServiceIntent() {
        return new Intent(this, LocalVpnService.class);
    }
}