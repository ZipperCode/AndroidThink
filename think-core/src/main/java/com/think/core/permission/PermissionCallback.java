package com.think.core.permission;

/**
 * 权限申请回调
 * @date : 2020-7-25
 * @author zzp
 */
@FunctionalInterface
public interface PermissionCallback {
    /**
     * 授权成功返回true 否则false
     * @param isGrant true 成功
     */
    void onGrant(boolean isGrant);
}
