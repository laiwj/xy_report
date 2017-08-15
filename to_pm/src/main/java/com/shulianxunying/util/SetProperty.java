package com.shulianxunying.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SetProperty {

    /**
     * 若obj1中有和obj2中相同的属性值，且值不为空，则将值赋给obj2中对应的属性，全部赋值完全后返回obj2
     * @param obj1
     * @param obj2
     * @return obj2
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object beanProperty2beanProperty(Object obj1, Object obj2) throws InvocationTargetException, IllegalAccessException {

        Method[] methods1 = obj1.getClass().getMethods();
        Method[] methods2 = obj2.getClass().getMethods();

        String methodName1 = "";
        String methodName2 = "";
        String fieldName1 = "";
        String fieldName2 = "";

        for (int i = 0; i < methods1.length; i++){
            methodName1 = methods1[i].getName();
            if(methodName1.startsWith("get")){
                fieldName1 = methodName1.substring(3,methodName1.length());
            }else if(methodName1.startsWith("is")){
                fieldName1 = methodName1.substring(2,methodName1.length());
            }else {
                continue;
            }
            for (int j = 0; j < methods2.length; j++){
                methodName2 = methods2[j].getName();
                if(methodName2.startsWith("set")){
                    fieldName2 = methodName2.substring(3,methodName2.length());
                    if(fieldName1.equals(fieldName2)){
                        Object target = methods1[i].invoke(obj1, new Object[0]);
                        if(target != null || !"".equals(target)){
                            Object[] objs = new Object[1];
                            objs[0] = target;
                            methods2[j].invoke(obj2,objs);
                        }
                        break;
                    }
                }
            }
        }

        return obj2;
    }

    /**
     * 使用Apache BeanUtil利用反射复制javabean
     * 如果obj1的某个get方法得出的参数不为空，则调用obj2的set方法，最后返回合并后的javabean
     *
     * @param obj1
     * @param obj2
     * @return obj2
     * @throws Exception
     */
    public static Object CopyBeanToBean(Object obj1, Object obj2)
            throws Exception {

        Method[] method1 = obj1.getClass().getMethods();

        Method[] method2 = obj2.getClass().getMethods();

        String methodName1;

        String methodFix1;

        String methodName2;

        String methodFix2;

        for (int i = 0; i < method1.length; i++) {

            methodName1 = method1[i].getName();

            methodFix1 = methodName1.substring(3, methodName1.length());

            if (methodName1.startsWith("get")) {

                for (int j = 0; j < method2.length; j++) {

                    methodName2 = method2[j].getName();

                    methodFix2 = methodName2.substring(3, methodName2.length());

                    if (methodName2.startsWith("set")) {

                        if (methodFix2.equals(methodFix1)) {

                            Object[] objs1 = new Object[0];

                            Object[] objs2 = new Object[1];
                            objs2[0] = method1[i].invoke(obj1, objs1);// 激活obj1的相应的get的方法，objs1数组存放调用该方法的参数,此例中没有参数，该数组的长度为0
                            if (objs2[0] != null && (!"".equals(objs2[0]))) {
                                method2[j].invoke(obj2, objs2);// 激活obj2的相应的set的方法，objs2数组存放调用该方法的参数
                            }
                            continue;
                        }

                    }

                }

            }

        }

        return obj2;

    }

    /**
     * 使用Apache BeanUtil利用反射复制javabean,设置所有的NULL为字符串""
     * 如果obj1的某个get方法得出的参数不为空，则调用obj2的set方法，最后返回合并后的javabean
     *
     * @param obj1
     * @param obj2
     * @return obj2
     * @throws Exception
     */
    public static Object DelBeanNull(Object obj1, Object obj2)
            throws Exception {

        Method[] method1 = obj1.getClass().getMethods();

        Method[] method2 = obj2.getClass().getMethods();

        String methodName1;

        String methodFix1;

        String methodName2;

        String methodFix2;

        for (int i = 0; i < method1.length; i++) {

            methodName1 = method1[i].getName();

            methodFix1 = methodName1.substring(3, methodName1.length());

            if (methodName1.startsWith("get")) {

                for (int j = 0; j < method2.length; j++) {

                    methodName2 = method2[j].getName();

                    methodFix2 = methodName2.substring(3, methodName2.length());

                    if (methodName2.startsWith("set")) {

                        if (methodFix2.equals(methodFix1)) {

                            Object[] objs1 = new Object[0];

                            Object[] objs2 = new Object[1];
                            objs2[0] = method1[i].invoke(obj1, objs1);// 激活obj1的相应的get的方法，objs1数组存放调用该方法的参数,此例中没有参数，该数组的长度为0
                            if (objs2[0] == null) {
                                Class[] parameterTypes = method2[j].getParameterTypes();

                                if (parameterTypes[0] == Integer.class) {
                                    method2[j].invoke(obj2, 1);
                                } else if (parameterTypes[0] == String.class) {
                                    method2[j].invoke(obj2, "");
                                } else if (parameterTypes[0] == Boolean.class) {
                                    method2[j].invoke(obj2, false);
                                } else {
                                    method2[j].invoke(obj2, "");// 激活obj2的相应的set的方法，objs2数组存放调用该方法的参数
                                }
                                System.out.println("找到类型" + parameterTypes[0]);
                            } else {
                                method2[j].invoke(obj2, objs2);
                            }
                            continue;
                        }

                    }

                }

            }

        }

        return obj2;

    }
}
