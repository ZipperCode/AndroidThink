package com.think.business.login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * @author : zzp
 * @date : 2020/8/13
 **/
public class FaceBookLoginHelper {
    private CallbackManager callbackManager;
    private Activity context;
    private String name = "";
    private String loginId;
    private boolean isInit = false;
    private LoginCallback mLoginCallback;

    public FaceBookLoginHelper(Activity activity) {
        this.context = activity;
    }


    public void init(final LoginCallback loginCallback) {
        this.mLoginCallback = loginCallback;
        init(new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginResult.getAccessToken().getApplicationId();
                loginId = loginResult.getAccessToken().getUserId();
                GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (object != null) {
                            name = object.optString("name");
                            // TODO next custom implement
                        }
                    }
                }).executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("onCancel", "facebook登录取消");
                mLoginCallback.onFailure();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("onError", "facebook登录错误");
                mLoginCallback.onFailure();
            }
        });
    }

    public void init(FacebookCallback<LoginResult> facebookCallback) {
        if (isInit) {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager, facebookCallback);
        }
    }

    public boolean checkLogin() {
        if (isInit) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            return isLoggedIn;
        }
        return false;
    }

    public void login() {
        if (isInit) {
            LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("public_profile"));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isInit) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public interface LoginCallback {
        void onSuccess();

        void onFailure();
    }

}
