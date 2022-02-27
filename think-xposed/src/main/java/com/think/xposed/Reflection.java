package com.think.xposed;

import android.os.Build;

import java.lang.ref.Reference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Reflection {

    private Method forNameMethod;

    private Method getDeclaredConstructor;

    private Method getDeclaredField;

    private Method getDeclaredMethod;

    private Class<?> internalClass;

    private Object internalObj;

    public Reflection(){
        try {
            forNameMethod = Class.class.getDeclaredMethod("forName", String.class);
            getDeclaredConstructor = Class.class.getDeclaredMethod("getDeclaredConstructor", Class[].class);
            getDeclaredField = Class.class.getDeclaredMethod("getDeclaredField", String.class);
            getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Reflection forName(String className){
        Reflection reflection = new Reflection();
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                reflection.internalClass = (Class<?>) reflection.internalForName(className);
            }else{
                reflection.internalClass = Class.forName(className);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return reflection;
    }

    public static Reflection forName(Object object){
        Reflection reflection = new Reflection();
        reflection.internalClass = object.getClass();
        reflection.internalObj = object;
        return reflection;
    }

    public static Reflection forName(Class<?> cls){
        return forName(cls.getName());
    }

    public Constructor<?> constructor(Class<?>... typeCls){
        Constructor<?> constructor = null;
        try {
            constructor = (Constructor<?>) getDeclaredConstructor.invoke(internalClass, (Object[]) typeCls);
            if (constructor !=  null){
                constructor.setAccessible(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return constructor;
    }

    public Object newInstance(Object... params){
        Class<?>[] paramClass = new Class[params.length];
        for (int i = 0; i < paramClass.length; i++) {
            paramClass[i] = params[i].getClass();
        }
        Object object = null;
        try {
            if (paramClass.length == 0){
                object = internalClass.newInstance();
            }else {
                Constructor<?> constructor = constructor(paramClass);
                if (constructor != null){
                    object = constructor.newInstance(params);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    public Method method(String methodName, Class<?>... args) {
        Method method = null;
        try {
            if (internalClass != null) {
                method = (Method) getDeclaredMethod.invoke(internalClass, methodName, args);
                if (method != null) {
                    method.setAccessible(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }

    public Reflection invoke(Object target,String methodName, Class<?>[] typeArgs, Object[] paramArgs){
        Method method = method(methodName, typeArgs);
        if (method == null){
            return null;
        }
        try {
            Object resultObj =  method.invoke(target, paramArgs);
            if (resultObj == null){
                return null;
            }
            return Reflection.forName(resultObj);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Object staticField(String fieldName){
        return field(null, fieldName);
    }

    public Object objectField(String fieldName){
        return field(internalObj, fieldName);
    }

    public Reflection field(Object targetObj, String fieldName){
        Reflection reflection = null;
        try {
            if (internalClass != null){
                Field field = (Field) getGetDeclaredField().invoke(internalClass, fieldName);
                if (field != null){
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    reflection = Reflection.forName(fieldType);
                    reflection.internalObj = field.get(targetObj);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return reflection;
    }

    public Object object(){
        return internalObj;
    }

    public Method getForNameMethod(){
        return forNameMethod;
    }

    public Method getGetDeclaredConstructor() {
        return getDeclaredConstructor;
    }

    public Method getGetDeclaredField() {
        return getDeclaredField;
    }

    private Class<?> internalForName(String className){
        Class<?> cls = null;
        try {
            cls =  (Class<?>) getForNameMethod().invoke(null, className);
        }catch (Exception e){
            e.printStackTrace();
        }
        return cls;
    }
}
