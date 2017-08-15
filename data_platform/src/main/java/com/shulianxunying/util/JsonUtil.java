package com.shulianxunying.util;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 梁亮 on 2015/9/11 0011.
 * Cell:15884457479
 * Email:dreamto1@126.com
 * Description:
 * <p/>
 * Functions:
 * 1.
 */
public class JsonUtil {
    /**
     * json转Map
     * @param json
     * @return
     */
    public static Map<String, Object> jsonToMap(String json) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(json,
                    new TypeReference<HashMap<String, Object>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return map;
    }

    /**
     * json转Map
     * @param json
     * @return
     */
    public static HashMap<String, String> jsonToHashMap(String json) {
        HashMap<String, String> map = new HashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(json,
                    new TypeReference<HashMap<String, String>>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return map;
    }

    /**
     * Json字符串转List
     * @param jsonString
     * @param T
     * @param <T>
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> List<T> jsonToList(String jsonString,Class<?> T) throws JsonParseException, JsonMappingException, IOException{
        JavaType javaType = getCollectionType(ArrayList.class, T);
        ObjectMapper mapper = new ObjectMapper();
        List<T> lst =  (List<T>)mapper.readValue(jsonString, javaType);
        return lst;
    }

    /**
     * 获取定义的集合类型
     * @param collectionClass
     * @param elementClasses
     * @return
     */
    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 对象转json
     * @param ob
     * @return
     */
    public static String entityToJson(Object ob) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(ob);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return json;
    }

    /**
     * json转对象
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jsonToEntity(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
