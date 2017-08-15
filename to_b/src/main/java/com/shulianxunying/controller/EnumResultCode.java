package com.shulianxunying.controller;


/**
 * 预设的 返回值的 code msg 对。
 */
public enum EnumResultCode {

    RESULTCODE_SUCCESS(0,"OK"),
    RESULTCODE_NULL(-1,"没有找到"),
    RESULTCODE_ERROR(-2,"fail"),
    RESULTCODE_PARAMETER_ERROR(-3,"参数错误"),
    RESULTCODE_USER_ERROR(-4,"用户名或者密码错误"),
    RESULTCODE_RETURN_EMPTY(-5,"返回结果为空"),
    RESULTCODE_AUTH_FAIL(-6, "没有权限"),;
    ;
    private Integer code;

    private String description;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    EnumResultCode(Integer code, String description){
        this.code = code;
        this.description = description;
    }

    public static EnumResultCode getEnumResultCodeByCode(Integer code){
        if (null == code) {
            return null;
        }
        for(EnumResultCode souce : values()){
            if(souce.getCode().equals(code))
                return souce;
        }
        return null;

    }
    public static String getMsgByCode(Integer code){
        if (null == code) {
            return null;
        }
        for(EnumResultCode souce : values()){
            if(souce.getCode().equals(code))
                return souce.getDescription();
        }
        return null;

    }
}
