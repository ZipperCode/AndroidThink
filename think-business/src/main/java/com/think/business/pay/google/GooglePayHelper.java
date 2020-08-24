package com.think.business.pay.google;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.think.core.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;
import static com.think.core.util.LogUtils.debug;
import static com.think.core.util.LogUtils.debugM;

/**
 * 支付窗口
 *
 * @author zzp
 */
public class GooglePayHelper implements
        IabHelper.OnIabSetupFinishedListener,
        IabHelper.OnIabPurchaseFinishedListener,
        IabHelper.OnConsumeFinishedListener,
        IabHelper.QueryInventoryFinishedListener {

    private static final String TAG = GooglePayHelper.class.getSimpleName();
    //
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    /**
     * 购买请求回调requestcode
     */
    static final int RC_REQUEST = 1001;

    private IabHelper mHelper;
    /**
     * Google支付需要的
     * 购买产品的id
     */
    private static String mPurchaseId = "com.zhifu.6";
    /**
     * 谷歌Play控制台，的公钥
     */
    String base64EncodedPublicKey = "";

    /**
     * 传给服务端用于验证订单号的准确性
     */
    private String googleOrderId = "";
    private String googleProductId = "";
    private String purchaseToken = "";
    private String packageName = "";
    private String purchaseTime = "";
    private String purchaseState = "";

    private Activity mActivity;
    /**
     * 是否支持谷歌支付
     */
    private boolean isSupportGooglePay = true;

    public GooglePayHelper(Activity context) {
        this.mActivity = context;
        initData();
    }

    /**
     * 初始化数据
     */

    protected void initData() {
        mHelper = new IabHelper(mActivity, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(this);
    }


    /**
     * 去购买Google产品
     * purchaseId  Google产品id
     * <p>
     * 点击购买的时候，才去初始化产品，看看是否有这个产品，是否消耗
     */
    private void toGooglePay() {
        debugM(TAG, "Google Pay start PurchaseId = " + mPurchaseId);
        //这个payload是要给Google发送的备注信息，自定义参数，购买完成之后的订单中也有该字段
        String payload = "";
        try {
            mHelper.launchPurchaseFlow(mActivity, mPurchaseId,
                    RC_REQUEST, this, payload);
        } catch (Exception e) {
            LogUtils.error("toBuyGooglePay", "无法完成谷歌支付");
        }
    }

    /**
     * 查询库存信息
     */
    private void queryInventory() {
        debugM(TAG, "Query inventory start");
        try {
            mHelper.queryInventoryAsync(this);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
            LogUtils.error(TAG, "query inventory error");
        }
    }

    /**
     * 消费库存
     *
     * @param purchase
     */
    private void consume(Purchase purchase) {
        debugM(TAG, "consume start");
        try {
            //购买完成之后去消耗产品
            mHelper.consumeAsync(purchase, this);
        } catch (Exception e) {
            LogUtils.error(TAG, "Error consuming gas. Another async operation in progress.");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) {
            return;
        }
        try {
            if (mHelper.handleActivityResult(requestCode, resultCode, data)) {
                LogUtils.error(TAG, "onActivityResult handled by IABUtil.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (requestCode == RC_REQUEST) {
            // 响应信息
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            //订单信息
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            debug(TAG, "responseCode：： " + responseCode);
            debug(TAG, "purchaseData：： " + purchaseData);
            debug(TAG, "dataSignature：： " + dataSignature);

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    /**
                     * {
                     * 	"orderId": "GPA.3388-4593-6156-74787",
                     * 	"packageName": "com.sdk.hw",
                     * 	"productId": "com.zhifu.6",
                     * 	"purchaseTime": 1553161977386,
                     * 	"purchaseState": 0,
                     * 	"purchaseToken": "bhnhakagaibdokbnpdbblgbf.AO-J1OwvT7XXHiNDDuxkTN0hhSa8A01pzscVHLq750WxWg2Fx9B8drjVHcBeiNdgQWj5dI8jEFPDAobIQEErigoZUEg5PeaZwXVUyaiVIFvixmH_vzsP4JA"
                     * }
                     */
                    googleOrderId = jo.getString("orderId");
                    purchaseToken = jo.getString("purchaseToken");
                    googleProductId = jo.getString("productId");
                    packageName = jo.getString("packageName");
                    purchaseTime = jo.getString("purchaseTime");
                    purchaseState = jo.getString("purchaseState");

                    //商品Id
                    String sku = jo.getString("productId");
                    System.out.println("You have bought the " + sku + ". Excellent choice,adventurer!");
                } catch (JSONException e) {
                    LogUtils.error(TAG, "Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 初始化完成回调
     *
     * @param result The result of the setup process.
     */
    @Override
    public void onIabSetupFinished(IabResult result) {
        debugM(TAG, "初始化完成.");
        if (mHelper == null) {
            return;
        }
        if (result.isFailure()) {
            LogUtils.error("Problem setting up in-app billing:初始化失败 " + result);
            return;
        }
        Log.e(TAG, "Google初始化成功.");
        // 查询是否有库存信息
        queryInventory();
    }


    /**
     * 支付完成回调
     *
     * @param result   The result of the purchase.
     * @param purchase 支付数据
     */
    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        debugM(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
        if (mHelper == null) {
            return;
        }

        if (result.isFailure()) {
            LogUtils.error("Error purchasing: " + result);
            return;
        }
        Log.e(TAG, "购买完成.");
        //购买完成时候就能获取到订单的详细信息：purchase.getOriginalJson(),要是想要什么就去purchase中get
        //根据获取到产品的Id去判断是哪一项产品
        if (purchase.getSku().equals(mPurchaseId)) {
            Log.e(TAG, "购买的是" + purchase.getSku());
            consume(purchase);
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        debugM(TAG, "查询库存完成.");
        if (mHelper == null) {
            return;
        }
        if (result.isFailure()) {
            LogUtils.error("查询库存失败: " + result);
            return;
        }
        Log.e(TAG, "查询库存成功.");

        // Check for gas delivery -- if we own gas, we should fill up the tank immediately
        //查询你的产品是否存在没有消耗的，要是没有消耗，先去消耗，再购买
        Purchase gasPurchase = inventory.getPurchase(mPurchaseId);
        if (gasPurchase != null) {
            consume(gasPurchase);
            return;
        }
        LogUtils.debug(TAG, "初始库存查询完成；启用主用户界面.");
    }

    /**
     * 消费完成回调
     *
     * @param purchase The purchase that was (or was to be) consumed.
     * @param result   The result of the consumption operation.
     */
    @Override
    public void onConsumeFinished(Purchase purchase, IabResult result) {
        Log.e(TAG, "消耗完。购买（Purchase）： " + purchase + ", result: " + result);
        // if we were disposed of in the meantime, quit.
        if (mHelper == null) {
            return;
        }
        if (result.isSuccess()) {
            Log.e(TAG, "消费成功。Provisioning.");
            callBackSuccess();
        } else {
            callBackFailure();
            LogUtils.error("Error while consuming: " + result);
        }
    }

    private void callBackSuccess() {
        // TODO callback Success
    }

    private void callBackFailure() {
        // TODO callback failure
    }

}
