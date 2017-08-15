package com.shulianxunying.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，判断权限，只能初步判断 该接口是否权限访问
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthAnnotation {
    /**
     * 携带此权限的用户 可以访问该接口
     */
    int auth_code();

}
