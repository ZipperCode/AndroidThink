package com.think.xposed.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author zhangzhipeng
 * @date 2022/2/21
 **/
public class ReflectPrintUtil {

    public static void printFields(Class<?> c1) {
        System.out.println("\n " + c1.getName() + ":\n");
        Field[] fields= c1.getDeclaredFields();
        for (Field field : fields) {
            Class<?> type= field.getType();
            String name=field.getName();
            System.out.print("  ");
            String modifiers= Modifier.toString(field.getModifiers());
            if (modifiers.length()>0) {
                System.out.print(modifiers+" ");
            }
            System.out.println(type.getName()+" "+name+";");
        }
    }


    public static void printMethods(Class<?> c1) {
        System.out.println("\n " + c1.getName() + ":\n");
        //获取当前类的所有方法
        Method[] methods= c1.getDeclaredMethods();
        for (Method m : methods) {
            Class<?> returnType= m.getReturnType();
            String methodName=m.getName();
            String modifiers=Modifier.toString(m.getModifiers());
            if (modifiers.length()>0) {
                System.out.print("  "+modifiers+" ");
            }
            System.out.print(returnType.getName()+" "+methodName+"(");

            //打印方法参数
            Class<?>[] paramTypes= m.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                if (i>0) {
                    System.out.print(",");
                }
                System.out.print(paramTypes[i].getName());
            }
            System.out.println(");");
        }
    }


    public static void printConstructors(Class<?> c1) {
        System.out.println("\n " + c1.getName() + ":\n");
        //得到当前类的所有构造器，包括受保护的和私有的，不包含超类的
        Constructor<?>[] constructors= c1.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.print("  ");
            String name=constructor.getName();
            String modifiers= Modifier.toString(constructor.getModifiers());
            if (modifiers.length()>0) {
                System.out.print(modifiers+" ");
            }
            System.out.print(name+"(");

            //打印构造器的参数
            Class<?>[] paramsTypes= constructor.getParameterTypes();
            for (int i = 0; i < paramsTypes.length; i++) {
                if (i>0) {
                    System.out.print(",");
                }
                System.out.print(paramsTypes[i].getName());
            }
            System.out.println(");");
        }
    }
}
