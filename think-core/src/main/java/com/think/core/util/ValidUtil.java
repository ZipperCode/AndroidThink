package com.think.core.util;

import android.text.TextUtils;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidUtil {

    /**
     * 验证集合是否为空，包含list set等
     * @param collection 集合父类
     * @return
     */
    public static boolean validCollection(Collection collection){
        return collection != null && !collection.isEmpty();
    }

    /**
     * 验证map集合是否为空
     * @param map
     * @return
     */
    public static boolean validateMap(Map map){
        return map != null && !map.isEmpty();
    }

    /**
     * 检查文件是否包含中文
     * @param name 中文字符串
     * @return 中文true 否则false
     */
    public static boolean isChinese(String name){
        if (name.contains("·") || name.contains("•")){
            if (name.matches("^[\\u4e00-\\u9fa5]+[·•][\\u4e00-\\u9fa5]+$")){
                return true;
            }else {
                return false;
            }
        }else {
            if (name.matches("^[\\u4e00-\\u9fa5]+$")){
                return true;
            }else {
                return false;
            }
        }
    }

    /**
     * 验证邮箱有效性
     * @param email 邮箱字符串
     * @return
     */
    public static boolean checkEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}" +
                "\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 验证是否为手机号码
     * @param phoneNumber 手机号字符串
     * @return
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        String expression = "((^(11|12|13|14|15|16|17|18|19)[0-9]{9}$)|(^0[1,2]{1}d{1}-?d{8}$)|"
                + "(^0[3-9] {1}d{2}-?d{7,8}$)|"
                + "(^0[1,2]{1}d{1}-?d{8}-(d{1,4})$)|"
                + "(^0[3-9]{1}d{2}-? d{7,8}-(d{1,4})$))";
        return Pattern.compile(expression).matcher(phoneNumber).matches();
    }
}
