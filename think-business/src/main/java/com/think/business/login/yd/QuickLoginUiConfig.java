package com.think.business.login.yd;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nis.quicklogin.helper.UnifyUiConfig;
import com.netease.nis.quicklogin.utils.LoginUiHelper;
import com.xinji.sdk.constant.XJConfig;
import com.xinji.sdk.resource.ReflectResource;
import com.xinji.sdk.util.common.ScreenUtil;
import com.xinji.sdk.util.common.ToastUtil;
import com.xinji.sdk.widget.BannerLayout;

/**
 * Created by hzhuqi on 2019/12/31
 */
public class QuickLoginUiConfig {

    /**
     * Banner视图的宽高
     */
    public static final int BANNER_WIDTH = 160;
    public static final int BANNER_HEIGHT = 270;
    /**
     * 横屏宽高
     */
    private static final int LAND_SCOPE_DIALOG_WIDTH = 520;
    private static final int LAND_SCOPE_DIALOG_HEIGHT = 270;

    /**
     * 竖屏宽高
     */
    private static final int PORTRAIT_DIALOG_WIDTH = 360;
    private static final int PORTRAIT_DIALOG_HEIGHT = 270;

    private static int grayColor = Color.DKGRAY;

    public static UnifyUiConfig getDialogUiConfig(final Activity context, final UIClickCallback callback, boolean isLandScope) {
        if (isLandScope) {
            return getLandScopeUniConfig(context, callback);
        }
        return getPortraitUniConfig(context, callback);
    }

    private static UnifyUiConfig getPortraitUniConfig(final Context context, final UIClickCallback callback) {
        UnifyUiConfig.Builder builder = new UnifyUiConfig.Builder();
        setNavigation(builder);
        setLogo(builder, PORTRAIT_DIALOG_WIDTH / 2 - 22, 20);
        setMaskNumber(builder, PORTRAIT_DIALOG_WIDTH / 2 - 62, 70);
        setSlogan(builder, PORTRAIT_DIALOG_WIDTH / 2 - 67, 100);
        int btnWidth = 280;
        int btnHeight = 40;
        setLoginButton(builder, (PORTRAIT_DIALOG_WIDTH / 2) - (btnWidth / 2), 130, btnWidth, btnHeight);
        setPrivacy(builder, (PORTRAIT_DIALOG_WIDTH / 2) - (btnWidth / 2), 10);
        TextView textView = getOtherTextView(context, 40, 190);
        return builder
                .addCustomView(getMaskTextView(context, 5, 0), "tv_mask", UnifyUiConfig.POSITION_IN_BODY, null)
                .addCustomView(textView,
                        "tv_other", UnifyUiConfig.POSITION_IN_BODY, new LoginUiHelper.CustomViewListener() {
                            @Override
                            public void onClick(Context context, View view) {
                                callback.onOther();
                            }
                        })
                .setDialogMode(true, PORTRAIT_DIALOG_WIDTH, PORTRAIT_DIALOG_HEIGHT, 0, 0, false)
                .setLandscape(false)
                .setBackgroundImage("border_shape_bg_color")
                .build(context.getApplicationContext());
    }

    private static UnifyUiConfig getLandScopeUniConfig(final Context context, final UIClickCallback callback) {
        UnifyUiConfig.Builder builder = new UnifyUiConfig.Builder();
        setNavigation(builder);
        // setLogo(builder, BANNER_WIDTH + (PORTRAIT_DIALOG_WIDTH / 2) - 20, 20);
        setMaskNumber(builder, BANNER_WIDTH + (PORTRAIT_DIALOG_WIDTH / 2) - 62, 40);
        setSlogan(builder, BANNER_WIDTH + (PORTRAIT_DIALOG_WIDTH / 2) - 67, 75);
        int btnWidth = 280;
        int btnHeight = 40;
        setLoginButton(builder, BANNER_WIDTH + (PORTRAIT_DIALOG_WIDTH / 2) - (btnWidth / 2), 110, btnWidth, btnHeight);
        setPrivacy(builder, BANNER_WIDTH + (PORTRAIT_DIALOG_WIDTH / 2) - (btnWidth / 2), 10);

        TextView textView = getOtherTextView(context, BANNER_WIDTH + 40, 180);

        return builder
                .addCustomView(getBannerView(context), "layout_banner",
                        UnifyUiConfig.POSITION_IN_BODY, null)
                .addCustomView(textView,
                        "tv_other", UnifyUiConfig.POSITION_IN_BODY, new LoginUiHelper.CustomViewListener() {
                            @Override
                            public void onClick(Context context, View view) {
                                callback.onOther();
                            }
                        })
                .setDialogMode(true, LAND_SCOPE_DIALOG_WIDTH, LAND_SCOPE_DIALOG_HEIGHT,
                        0, 0, false)
                .setLandscape(true)
                .setBackgroundImage("border_shape_bg_color")
                .build(context.getApplicationContext());
    }


    private static void setNavigation(final UnifyUiConfig.Builder builder) {
        /* 设置导航栏 */
        builder
//                .setNavigationTitle("登录授权")
//                .setNavigationTitleColor(Color.RED)
//                .setNavigationBackgroundColor(Color.WHITE)
//                .setNavigationIcon("icon_back")
                .setNavigationHeight(1)
                // 是否隐藏导航栏
                .setHideNavigation(true);
    }

    private static void setMaskNumber(final UnifyUiConfig.Builder builder, int xOffset, int yTopOffset) {
        /* 手机掩码 */
        builder.setMaskNumberColor(Color.BLACK)
                .setMaskNumberSize(24)
                .setMaskNumberTopYOffset(yTopOffset)
                .setMaskNumberXOffset(xOffset);
    }

    private static void setSlogan(final UnifyUiConfig.Builder builder, int xOffset, int yTopOffset) {
        /* 认证品牌 */
        builder.setSloganSize(15)
                .setSloganColor(Color.DKGRAY)
                .setSloganTopYOffset(yTopOffset)
                .setSloganXOffset(xOffset);
    }

    private static void setLoginButton(final UnifyUiConfig.Builder builder,
                                       int xOffset,
                                       int yOffset,
                                       int btnWidth,
                                       int btnHeight) {
        /* 登录按钮 */
        builder.setLoginBtnText("一键登录")
                // 设置登录按钮颜色
                .setLoginBtnTextColor(Color.WHITE)
                // 按钮背景
                .setLoginBtnBackgroundRes("border_shape_txv_solid_pink")
                // 按钮宽度
                .setLoginBtnWidth(btnWidth)
                // 按钮高度
                .setLoginBtnHeight(btnHeight)
                // 按钮字体大小
                .setLoginBtnTextSize(15)
                // 按钮X轴偏移
                .setLoginBtnXOffset(xOffset)
                // 按钮Y轴偏移
                .setLoginBtnTopYOffset(yOffset);
    }

    private static void setPrivacy(final UnifyUiConfig.Builder builder, int xOffset, int yBottomOffset) {
        /* 隐私栏 */
        builder.setPrivacyTextStart("登录即同意")
                // 协议1
                .setProtocolText("用户协议")
                .setProtocolLink(XJConfig.AGREEMENT_STATUS_URL)
                // 协议2
                .setProtocol2Text("隐私政策")
                .setProtocol2Link(XJConfig.AGREEMENT_STATUS_PRIVACY_POLICY_URL)
                // 隐私栏文本颜色
//                .setPrivacyTextColor(Color.DKGRAY)
                .setPrivacyTextColor(Color.DKGRAY)
                // 协议文本颜色
                .setPrivacyProtocolColor(Color.DKGRAY)
                // 是否隐藏checkbox
                .setHidePrivacyCheckBox(true)
                // 隐私栏x偏移
                .setPrivacyXOffset(xOffset)
                // 协议确认状态
                .setPrivacyState(true)
                // 隐私栏字体大小
                .setPrivacySize(12)
                .setPrivacyMarginRight(40)
                // 隐私栏底部的y偏移
                .setPrivacyBottomYOffset(yBottomOffset)
                // 隐私栏是否文本居中显示
                .setPrivacyTextGravityCenter(true)
                // 选中和未选中图片
                .setCheckedImageName("yd_checkbox_checked")
                .setUnCheckedImageName("yd_checkbox_unchecked")
                // 协议详情页导航栏
                .setProtocolPageNavTitle("一键登录SDK服务条款")
                .setProtocolPageNavBackIcon("icon_back")
                .setProtocolPageNavColor(Color.WHITE);
    }

    private static ImageView closeBtnView(Context context) {
        ReflectResource resource = ReflectResource.getInstance(context);
        // 关闭图片设置
        ImageView closeBtn = new ImageView(context);
        closeBtn.setImageResource(resource.getDrawableId("_xj_icon_close"));
        closeBtn.setScaleType(ImageView.ScaleType.FIT_XY);
        closeBtn.setBackgroundColor(Color.TRANSPARENT);
        // 设置宽高
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(60, 60);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
                RelativeLayout.CENTER_VERTICAL);
        layoutParams.topMargin = 30;
        layoutParams.rightMargin = 50;
        closeBtn.setLayoutParams(layoutParams);
        return closeBtn;
    }

    private static BannerLayout getBannerView(Context context) {
        BannerLayout bannerLayout = (BannerLayout) ReflectResource
                .getInstance(context).getLayoutView("layout_banner");

        RelativeLayout.LayoutParams bannerLayoutParam = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        bannerLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        bannerLayoutParam.addRule(RelativeLayout.CENTER_VERTICAL);
        bannerLayout.setLayoutParams(bannerLayoutParam);
        return bannerLayout;
    }

    private static void setLogo(UnifyUiConfig.Builder builder, int xOffset, int yTopOffset) {
        /* 设置logo */
        builder.setLogoIconName("floatview_logo")
                .setLogoWidth(50)
                .setLogoHeight(50)
                .setLogoXOffset(xOffset)
                .setLogoTopYOffset(yTopOffset)
//                .setLogoBottomYOffset(300)
                // 是否隐藏logo
                .setHideLogo(false);
    }

    private static LinearLayout getOtherLayout(final Context context, int leftMargin, int topMargin) {
        ReflectResource resource = ReflectResource.getInstance(context);
        LinearLayout otherLayout = (LinearLayout) resource
                .getLayoutView("layout_home_other_mode");

        LinearLayout.LayoutParams layoutParamsOther = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParamsOther.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT | Gravity.BOTTOM;
        layoutParamsOther.width = ScreenUtil.dip2px(context, 280);
        layoutParamsOther.leftMargin = ScreenUtil.dip2px(context, leftMargin);
        layoutParamsOther.topMargin = ScreenUtil.dip2px(context, topMargin);
        otherLayout.setLayoutParams(layoutParamsOther);

        LinearLayout quickLoginLayout = (LinearLayout) resource
                .getWidgetView(otherLayout, "ll_quick_login");
        LinearLayout customerLayout = (LinearLayout) resource
                .getWidgetView(otherLayout, "ll_customer");

        LinearLayout findPwdLayout = (LinearLayout) resource
                .getWidgetView(otherLayout, "ll_find_pwd");

        quickLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 快速登录
                ToastUtil.showToast("快速登录", context);
            }
        });

        customerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 联系客服
                ToastUtil.showToast("联系客服", context);
            }
        });

        findPwdLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 找回密码
                ToastUtil.showToast("找回密码", context);
            }
        });
        return findPwdLayout;
    }

    private static TextView getOtherTextView(Context context, int xOffset, int yTopOffset) {
        TextView textView = new TextView(context);
        textView.setText("其他方式登录");
        textView.setTextColor(ReflectResource.getInstance(context).getColor("weak_red"));
        textView.setTextSize(18);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
//        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
//        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        layoutParams.width = ScreenUtil.dip2px(context, 280);
        layoutParams.topMargin = ScreenUtil.dip2px(context, yTopOffset);
        layoutParams.leftMargin = ScreenUtil.dip2px(context, xOffset);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private static TextView getMaskTextView(Context context, int xOffset, int yTopOffset) {
        TextView textView = new TextView(context);
        textView.setBackgroundColor(Color.WHITE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setWidth(ScreenUtil.dip2px(context, 20));
        textView.setHeight(ScreenUtil.dip2px(context, 20));
        layoutParams.topMargin = ScreenUtil.dip2px(context, yTopOffset);
        layoutParams.leftMargin = ScreenUtil.dip2px(context, xOffset);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

}
