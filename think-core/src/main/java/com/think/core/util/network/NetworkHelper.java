package com.think.core.util.network;

import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.think.core.util.LogUtils;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkHelper extends ConnectivityManager.NetworkCallback {
    private static final String TAG = NetworkHelper.class.getSimpleName();

    private static NetworkHelper Instance = null;

    private final List<NetworkReference> mListener= new ArrayList<>();

    private final ListenerQueue mQueue;

    private NetworkHelper(){
        mQueue = new ListenerQueue();
    }

    public static NetworkHelper getInstance(){
        synchronized (NetworkHelper.class){
            if(Instance == null){
                synchronized (NetworkHelper.class){
                    Instance = new NetworkHelper();
                }
            }
        }
        return Instance;
    }

    public void addListener(NetworkStateCallback networkStateCallback){
        if(networkStateCallback == null){
            return;
        }

        this.mListener.add(new NetworkReference(networkStateCallback,mQueue));
    }

    public void removeListener(NetworkStateCallback networkStateCallback){
        if(networkStateCallback == null){
            return;
        }
        this.mListener.remove(new NetworkReference(networkStateCallback));
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        for (NetworkReference networkReference : this.mListener) {
            NetworkStateCallback networkStateCallback = networkReference.get();
            if(networkStateCallback!= null){
                networkStateCallback.stateChange(NetworkState.NETWORK_CONNECT);
            }
        }
    }


    @Override
    public void onLost(Network network) {
        super.onLost(network);
        for (NetworkReference networkReference : this.mListener) {
            NetworkStateCallback networkStateCallback = networkReference.get();
            if(networkStateCallback!= null){
                networkStateCallback.stateChange(NetworkState.NETWORK_LOSE);
            }
        }
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        if (networkCapabilities != null) {
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    LogUtils.info(TAG, "onCapabilitiesChanged: 网络类型为wifi");
                    for (NetworkReference networkReference : this.mListener) {
                        NetworkStateCallback networkStateCallback = networkReference.get();
                        if(networkStateCallback!= null){
                            networkStateCallback.stateChange(NetworkState.NETWORK_WIFI);
                        }
                    }
                }else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    LogUtils.info(TAG, "onCapabilitiesChanged: 蜂窝网络");
                    for (NetworkReference networkReference : this.mListener) {
                        NetworkStateCallback networkStateCallback = networkReference.get();
                        if(networkStateCallback!= null){
                            networkStateCallback.stateChange(NetworkState.NETWORK_MOBILE);
                        }
                    }
                }else {
                    //
                    LogUtils.info(TAG, "onCapabilitiesChanged: 未知网络");
                }
            }
        }
    }

    public class ListenerQueue extends ReferenceQueue<NetworkStateCallback>{
        @Override
        public Reference<? extends NetworkStateCallback> remove() throws InterruptedException {
            Reference<? extends NetworkStateCallback> remove = super.remove();
            if(remove != null || remove instanceof WeakReference){
                mListener.remove((WeakReference)remove);
            }
            return remove;
        }
    }

    public static class NetworkReference extends WeakReference<NetworkStateCallback>{


        public NetworkReference(NetworkStateCallback referent) {
            super(referent);
        }

        public NetworkReference(NetworkStateCallback referent, ReferenceQueue<? super NetworkStateCallback> q) {
            super(referent, q);
        }

        @Override
        public int hashCode() {
            return Objects.hash(get());
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if(obj instanceof NetworkReference){
                NetworkReference networkReference = (NetworkReference) obj;
                if(networkReference == null){
                    return false;
                }
                if(hashCode() == obj.hashCode()){
                    return true;
                }

                NetworkStateCallback networkStateCallback = networkReference.get();
                if(networkStateCallback == null){
                    return false;
                }
                if(get() == null){
                    return false;
                }
                return networkStateCallback.equals(get());
            }
            return false;
        }
    }

    public interface NetworkStateCallback{
        void stateChange(NetworkState networkState);
    }
}
