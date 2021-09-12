package com.think.xposed;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SelfService extends Service {
    public SelfService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}