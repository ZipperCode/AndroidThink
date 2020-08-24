package com.think.business.login;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * @author : zzp
 * @date : 2020/8/13
 **/
public class GoogleLoginHelper {

    public static final int RC_SIGN_IN = 0x123;
    private Activity mContext;
    private GoogleSignInAccount account;
    private GoogleSignInClient client;
    private LoginCallback mLoginCallback;

    public GoogleLoginHelper(Activity mContext) {
        this.mContext = mContext;
    }


    public void init(LoginCallback loginCallback) {
        this.mLoginCallback = loginCallback;
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();
        this.client = GoogleSignIn.getClient(mContext, gso);
        this.account = GoogleSignIn.getLastSignedInAccount(mContext);
    }


    /**
     * 检查谷歌服务是否存在
     * @return true 表示存在
     */
    public boolean checkGooglePlay(){
        boolean googleServiceFlag = true;
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(mContext);
        if(resultCode != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(mContext, resultCode, 2404).show();
            }
            googleServiceFlag = false;
        }
        return googleServiceFlag;
    }

    /**
     * 检查用户是否登录
     * @return true 表示已经登录
     */
    public boolean checkAccount() {
        account = GoogleSignIn.getLastSignedInAccount(mContext);
        return account == null;
    }

    public void signOut() {
        client.signOut().addOnCompleteListener(mContext, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                account = null;
            }
        });
    }

    public void signIn() {
        if(checkGooglePlay()){
            Intent signInIntent = client.getSignInIntent();
            mContext.startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    public void handleSignInResult(GoogleSignInAccount account) {
        if(account != null){
            // TODO  login success
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                if(task.isSuccessful()){
                    account = task.getResult(ApiException.class);
                    handleSignInResult(account);
                    return;
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
            if(mLoginCallback != null){
                mLoginCallback.onFailure();
            }
        }
    }

    public interface LoginCallback{
        void onSuccess();

        void onFailure();
    }
}
