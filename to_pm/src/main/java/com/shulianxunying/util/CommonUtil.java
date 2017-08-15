package com.shulianxunying.util;

import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
    static SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    static SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM");
    static Random random = new Random();

    private static final String[] codes = {
            "0","1","2","3","4","5","6","7","8","9",
            "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
            "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
    };

    /**
     * 错误信息获取
     * @param filedErrors
     * @return
     */
    public static String getErrors(List<FieldError> filedErrors){
        StringBuffer sb = new StringBuffer();
        for (FieldError error : filedErrors){
            sb.append(error.getField()).append(":").append(error.getDefaultMessage()).append(";");
        }
        return sb.toString();
    }

    public static String md5(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update((str).getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }
        return md5StrBuff.toString().replace("-", "").toLowerCase();
    }

    /**
     * 判断对象为空
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;

        } else if (obj instanceof String && (obj.toString().trim().equals(""))) {
            return true;

        } else if (obj instanceof Number && ((Number) obj).doubleValue() == 0) {
            return true;

        } else if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;

        } else if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;

        } else if (obj instanceof Object[] && ((Object[]) obj).length == 0) {
            return true;

        }
        return false;
    }



    /**
     * 判断对象不为空
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 生成唯一ID
     * @return
     */
    public static String getUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-","");
    }

    /**
     * 判断是否是邮箱
     * @param email
     * @return true:是  false:否
     */
    public static boolean isEmail(String email)
    {
        if (isEmpty(email)){
            return false;
        }
        boolean tag = true;
        final String pattern1 = "^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        final Pattern pattern = Pattern.compile(pattern1);
        final Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            tag = false;
        }
        return tag;
    }

    public static boolean isPassword(String password){
        if(isEmpty(password))
            return false;
        boolean fg = password.length() >= 8;
        return fg;
    }


    /**
     * 获取请求IP地址
     * @param request
     * @return
     */
    public static String getIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 比较时间大小 返回date1是否大于date2
     * @param date1
     * @param date2
     * @return
     */
    public static boolean before(Date date1, Date date2) {
        return date1.getTime() > date2.getTime();
    }

    /**
     * 日期格式化成:"yyyy-MM-dd HH:mm:ss"
     * @param date
     * @return
     */
    public static String getymdhms(Date date) {
        return ymdhms.format(date);
    }

    /**
     * 日期格式化成:"yyyy-MM-dd HH:mm"
     * @param date
     * @return
     */
    public static String getymdhm(Date date) {
        return ymdhm.format(date);
    }

    /**
     * String格式的日期从新格式化ymdhm的格式
     * @param date
     * @return
     */
    public static String str2ymdhm(String date){
        Date d = null;
        try {
            d = ymdhm.parse(date);
        } catch (ParseException e) {
            try {
                d = ymd.parse(date);
            } catch (ParseException e1) {
                e1.printStackTrace();
                return "";
            }
        }
        if(d != null){
            return ymdhm.format(d);
        }else {
            return "";
        }
    }

    /**
     * 日期格式化成:"yyyy-MM-dd"
     * @param date
     * @return
     */
    public static String getymd(Date date) {
        return ymd.format(date);
    }

    /**
     * 日期格式化成:"yyyy-MM"
     * @param date
     * @return
     */
    public static String getym(Date date) {
        return ym.format(date);
    }

    public static Date getDateBySecond(String dateStr){
        if (isEmpty(dateStr)){
            return null;
        }
        Date date = null;
        try {
            date = ymdhms.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取时间偏移的有效链接时间
     * @return
     */
    public static Date getValidityTime(int day, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, day);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    public static String getCheckCode(int code_num){
        StringBuilder builder = new StringBuilder("");
        for(int i = 0; i< code_num; i++){
            builder.append(codes[random.nextInt(codes.length -1)]);
        }
        return String.valueOf(builder);
    }

    public static <T> List<T> listToList(List list, Class<T> entityClass){
        List<T> data = new ArrayList<>();
        if (null != list && null != entityClass) {
            for (Object ad : list) {
                data.add(CommonUtil.objectToEntity(ad, entityClass));
            }
        }
        return data;
    }

    public static <T> T objectToEntity(Object object, Class<T> entityClass) {
        T result = null;
        if (object != null && entityClass != null) {
            try {
                result = (T) Class.forName(entityClass.getName()).newInstance();
                Field[] fields = object.getClass().getDeclaredFields();
                List<String> fieldList = getFieldNameList(entityClass);
                Field resultField = null;
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (fieldList.contains(field.getName()) && field.get(object) != null) {
                        resultField = entityClass.getDeclaredField(field.getName());
                        resultField.setAccessible(true);
                        if (resultField.getType().equals(Integer.class) || resultField.getType().equals(int.class)) {
                            resultField.set(result, Integer.valueOf(String.valueOf(field.get(object))));
                        } else if (resultField.getType().equals(Date.class)) {
                            if (field.getType().equals(Date.class)) {
                                resultField.set(result, field.get(object));
                            } else if (field.getType().equals(String.class)) {
                                resultField.set(result, CommonUtil.getDateBySecond(String.valueOf(field.get(object))));
                            }
                        } else if(resultField.getType().equals(String.class)){
                            if(field.getType().equals(Date.class))
                                resultField.set(result, getymdhm((Date) field.get(object)));
                            else
                                resultField.set(result, String.valueOf(field.get(object)));
                        }
                    }
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void objectToObject(Object object, Object objectTo) {
        if (object != null && objectTo != null) {
            try {
                Field[] fields = object.getClass().getDeclaredFields();
                List<String> fieldList = getFieldNameList(objectTo.getClass());
                Field resultField = null;
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (fieldList.contains(field.getName()) && field.get(object) != null) {
                        resultField = objectTo.getClass().getDeclaredField(field.getName());
                        resultField.setAccessible(true);
                        if (resultField.getType().equals(Integer.class) || resultField.getType().equals(int.class)) {
                            resultField.set(objectTo, Integer.valueOf(String.valueOf(field.get(object))));
                        } else if (resultField.getType().equals(Date.class)) {
                            if (field.getType().equals(Date.class)) {
                                resultField.set(objectTo, field.get(object));
                            } else if (field.getType().equals(String.class)) {
                                resultField.set(objectTo, CommonUtil.getDateBySecond(String.valueOf(field.get(object))));
                            }
                        } else {
                            resultField.set(objectTo, String.valueOf(field.get(object)));
                        }

                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static List<String> getFieldNameList(Class cla){
        if(isEmpty(cla))
            return null;
        List<String> result = new ArrayList<>();
        for(Field field:cla.getDeclaredFields()){
            result.add(field.getName());
        }
        return result;
    }

    public static Long StringDate2Long(String validity) {

        try {
            return ymdhms.parse(validity).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    public static String[] shortUrl(String url) {
        // 要使用生成 URL 的字符
        String[] chars = new String[] { "a" , "b" , "c" , "d" , "e" , "f" , "g" , "h" ,
                "i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" ,
                "u" , "v" , "w" , "x" , "y" , "z" , "0" , "1" , "2" , "3" , "4" , "5" ,
                "6" , "7" , "8" , "9" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" ,
                "I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" , "S" , "T" ,
                "U" , "V" , "W" , "X" , "Y" , "Z"

        };
        // 对传入网址进行 MD5 加密
//        String sMD5EncryptResult = md5(url);
        String hex = url;

        String[] resUrl = new String[4];
        for ( int i = 0; i < 4; i++) {

            // 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
            String sTempSubString = hex.substring(i * 8, i * 8 + 8);

            // 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用 long ，则会越界
            long lHexLong = 0x3FFFFFFF & Long.parseLong (sTempSubString, 16);
            String outChars = "" ;
            for ( int j = 0; j < 6; j++) {
                // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
                long index = 0x0000003D & lHexLong;
                // 把取得的字符相加
                outChars += chars[( int ) index];
                // 每次循环按位右移 5 位
                lHexLong = lHexLong >> 5;
            }
            // 把字符串存入对应索引的输出数组
            resUrl[i] = outChars;
        }
        return resUrl;
    }
}
