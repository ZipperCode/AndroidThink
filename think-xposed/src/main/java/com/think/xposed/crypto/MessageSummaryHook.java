package com.think.xposed.crypto;

import com.think.xposed.BaseHook;
import com.think.xposed.Utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class MessageSummaryHook extends BaseHook {

    public static final String TAG = "MessageSummaryHook";
    Class<?> mdClass;

    public MessageSummaryHook(ClassLoader targetClassLoader,boolean debug) {
        super(targetClassLoader, debug);
        mdClass = findClassIfExists("java.security.MessageDigest");
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public void hook(){
        hookMessageDigest();
        hookHmac();
    }

    public void hookMessageDigest() {

        if (mdClass == null) {
            log(targetClassLoader + " 中未找到 java.security.MessageDigest 类，可能被混淆了");
            return;
        }

        XposedHelpers.findAndHookMethod(
                mdClass,
                "getInstance",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log(String.format("(%s)MessageDigest#getInstance(String) ====> 摘要的md5算法为 ：%s", param.thisObject, param.args[0]));
                    }
                });

        hookUpdateMethod();
        hookDigest();

    }

    private void hookHmac() {
        Class<?> macCls = XposedHelpers.findClassIfExists("javax.crypto.Mac", targetClassLoader);

        if (macCls == null) {
            log(targetClassLoader + " 中未找到 java.security.Mac 类，可能被混淆了");
            return;
        }

        XposedHelpers.findAndHookMethod(macCls, "getInstance", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                log("Mac#getInstance(String) ====> 摘要的md5算法为 ：" + param.args[0]);
            }
        });


//        try {
//            KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
//            SecretKey key = keyGen.generateKey();
//            // 打印随机生成的key:
//            byte[] skey = key.getEncoded();
//            Mac mac = Mac.getInstance("HmacMD5");
//            mac.init(key);
//            mac.update("HelloWorld".getBytes("UTF-8"));
//            MessageDigest messageDigest;
//            byte[] result = mac.doFinal();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        XposedHelpers.findAndHookMethod(macCls, "init", byte[].class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("("+param.thisObject+")Mac#init(byte[]) ====> mac摘要的初始化 key 为 ： " + Utils.byteHexToString((byte[]) param.args[0]));
                    }
                }
        );

        XposedHelpers.findAndHookMethod(
                macCls,
                "update",
                byte[].class,
                new Update1Hook(macCls.getSimpleName())
        );

        XposedHelpers.findAndHookMethod(macCls,
                "update",
                byte[].class,
                int.class,
                int.class,
                new Update2Hook(macCls.getSimpleName())
        );

        XposedHelpers.findAndHookMethod(
                macCls,
                "update",
                ByteBuffer.class,
                new Update3Hook(macCls.getSimpleName())
        );

        XposedHelpers.findAndHookMethod(macCls, "doFinal", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                byte[] result = (byte[]) param.getResult();
                log("("+param.thisObject+")Mac#doFinal ====> 摘要的md5密文为 ： " + Utils.byteHexToString(result));
            }
        });
    }

    private void hookUpdateMethod() {
        XposedHelpers.findAndHookMethod(
                mdClass,
                "update",
                byte[].class,
                new Update1Hook(mdClass.getSimpleName()));

        XposedHelpers.findAndHookMethod(
                mdClass,
                "update",
                ByteBuffer.class,
                new Update3Hook(mdClass.getSimpleName()));

        XposedHelpers.findAndHookMethod(
                mdClass,
                "update",
                byte[].class, int.class, int.class,
                new Update2Hook(mdClass.getSimpleName()));
    }

    class Update1Hook extends XC_MethodHook {

        String name = "";

        public Update1Hook(String name) {
            this.name = name;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            catExp(() -> {
                if (param.args[0] != null) {
                    ByteBuffer arg = (ByteBuffer) param.args[0];
                    String plainText = Utils.byteHexToString(arg.array());
                    log(String.format("(%s)%s#update(byte[]) ====> 摘要的md5原文字符为(Hex)：%s",
                            param.thisObject, name, plainText));
                    String textUtf8 = new String(arg.array(), StandardCharsets.UTF_8);
                    log(String.format("(%s)%s#update(byte[]) ====> 摘要的md5原文字符为(UTF-8)：[%s]",
                            param.thisObject, name, textUtf8));
                }
            });
        }
    }

    class Update2Hook extends XC_MethodHook {
        String name = "";

        public Update2Hook(String name) {
            this.name = name;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            catExp(() -> {
                int offset = (int) param.args[1];
                int len = (int) param.args[2];
                String textUtf8 = new String((byte[]) param.args[0], offset, len, StandardCharsets.UTF_8);
                String plainTextHex = Utils.byteHexToString((byte[]) param.args[0]);

                log(String.format("(%s)%s#update(byte[],int, int) ====> 摘要的md5原文字符为(Hex)：%s",
                        param.thisObject, name, plainTextHex));
                log(String.format("(%s)%s#update(byte[],int, int) ====> 摘要的md5原文字符为(UTF-8)：[%s]",
                        param.thisObject, name, textUtf8));
            });
        }
    }

    /**
     * update(ByteBuffer)
     */
    class Update3Hook extends XC_MethodHook {

        String name = "";

        public Update3Hook(String name) {
            this.name = name;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            catExp(() -> {
                if (param.args[0] != null) {
                    String plainText = Utils.byteHexToString((byte[]) param.args[0]);
                    log(String.format("(%s)%s#update(byte[]) ====> 摘要的md5原文字符为(Hex)：%s",
                            param.thisObject, name, plainText));
                    String textUtf8 = new String((byte[]) param.args[0], StandardCharsets.UTF_8);
                    log(String.format("(%s)%s#update(byte[]) ====> 摘要的md5原文字符为(UTF-8)：[%s]",
                            param.thisObject, name, textUtf8));
                }
            });
        }
    }

    private void hookDigest() {
        XposedHelpers.findAndHookMethod(
                mdClass,
                "digest",
                new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        catExp(() -> {
                            byte[] result = (byte[]) param.getResult();
                            String md5Hex = Utils.byteHexToString(result);
                            log(String.format("(%s)MessageDigest[digest(void)] ====> 摘要的md5密文为(Hex) ：%s",
                                    param.thisObject,
                                    md5Hex));
                        });
                    }
                });


        XposedHelpers.findAndHookMethod(
                mdClass,
                "digest",
                byte[].class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        catExp(() -> {
                            String plainHex = Utils.byteHexToString((byte[]) param.args[0]);
                            String plain = new String((byte[]) param.args[0], StandardCharsets.UTF_8);
                            byte[] result = (byte[]) param.getResult();
                            String md5Hex = Utils.byteHexToString(result);

                            log(String.format("(%s)MessageDigest[digest(byte[])] ===> 摘要的md5原文字符为(Hex): %s", param.thisObject, plainHex));
                            log(String.format("(%s)MessageDigest[digest(byte[])] ===> 摘要的md5原文字符为(UTF-8): %s", param.thisObject, plain));
                            log(String.format("(%s)MessageDigest[digest(byte[])] ====> 摘要的md5密文为(Hex) ：%s",
                                    param.thisObject,
                                    md5Hex));
                        });
                    }
                });
    }

}
