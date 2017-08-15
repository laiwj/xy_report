package com.shulianxunying.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by SuChang on 2017/3/2 10:05.
 */
public class SparkMongoHelper {

    public static String createMongoUrl(String ip, int port, String database) {
        return createBaseMongoUrl(Arrays.asList(new ServerAddress(ip, port)), null, null, database, null).toString();
    }

    public static String createMongoUrl(String ip, int port, String username, String password, String database) {
        return createBaseMongoUrl(Arrays.asList(new ServerAddress(ip, port)), username, password, database, null).toString();
    }

    public static String createMongoUrl(String ip, int port, String username, String password, String database, Map<String, String> options) {
        StringBuffer baseMongoUrl = createBaseMongoUrl(Arrays.asList(new ServerAddress(ip, port)), username, password, database, null);
        return appendOptions(baseMongoUrl, options).toString();
    }

    public static String createMongoUrl(String ip, int port, String database, String collection, Map<String, String> options) {
        StringBuffer baseMongoUrl = createBaseMongoUrl(Arrays.asList(new ServerAddress(ip, port)), null, null, database, collection);
        return appendOptions(baseMongoUrl, options).toString();
    }

    public static String createMongoUrl(String ip, int port, String database, String collection) {
        return createBaseMongoUrl(Arrays.asList(new ServerAddress(ip, port)), null, null, database, collection).toString();
    }

    public static String createMongoUrl(String ip, int port, String username, String password, String database, String collection) {
        return createBaseMongoUrl(Arrays.asList(new ServerAddress(ip, port)), username, password, database, collection).toString();
    }

    public static String createMongoUrl(String ip, int port, String username, String password, String database, String collection, Map<String, String> options) {
        StringBuffer baseMongoUrl = createBaseMongoUrl(Arrays.asList(new ServerAddress(ip, port)), username, password, database, collection);
        return appendOptions(baseMongoUrl, options).toString();
    }

    /**
     * authSource  账号验证库
     * authMechanism 验证加密方式  SCRAM-SHA-1  MONGODB-CR  MONGODB-X509  GSSAPI PLAIN
     *
     * @param baseUrl
     * @param options mongo链接可选配置参数, 内部并没有对 参数的value进行校验
     * @return
     */
    public static StringBuffer appendOptions(StringBuffer baseUrl, @NotNull Map<String, String> options) {
        if (options ==null ||options.size() == 0)
            return baseUrl;
        int i = baseUrl.indexOf("?");
        if (i < 0) {
            baseUrl.append("?");
        }
        String[] optionsStr = new String[]{
                "authSource", "authMechanism", "gssapiServiceName",
                "replicaSet",
                "ssl", "connectTimeoutMS", "socketTimeoutMS",
                "maxPoolSize", "minPoolSize", "maxIdleTimeMS", "waitQueueMultiple", "waitQueueTimeoutMS",
                "w", "wtimeoutMS", "journal",
                "readConcernLevel",
                "readPreference", "maxStalenessSeconds", "readPreferenceTags",
                "localThresholdMS", "serverSelectionTimeoutMS", "serverSelectionTryOnce", "heartbeatFrequencyMS",
                "uuidRepresentation"
        };
        boolean first = true;
        for (String option : optionsStr) {
            if (StringUtils.isNotEmpty(options.get(option))) {
                if (first) {
                    first = false;
                }else {
                    baseUrl.append("&");
                }
                baseUrl.append(option);
                baseUrl.append("=");
                baseUrl.append(options.get(option));
            }
        }
        return baseUrl;
    }

    /**
     * mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
     * 更多参数 参考 https://docs.mongodb.com/manual/reference/connection-string/
     * admin库的链接为默认 mongodb://username:password@localhost
     *
     * @param seeds
     * @param username
     * @param password
     * @param database
     * @param collection
     * @return
     */
    public static StringBuffer createBaseMongoUrl(List<ServerAddress> seeds,
                                                  String username, String password,
                                                  String database,
                                                  String collection) {
        StringBuffer sb = new StringBuffer();
        sb.append("mongodb://");
        if (StringUtils.isNotEmpty(username)) {
            sb.append(username);
            sb.append(":");
            if (StringUtils.isEmpty(password))
                throw new MongoException("password is empty");
            sb.append(password);
            sb.append("@");
        }
        if (seeds.size() < 1) {
            throw new MongoException("seeds is empty");
        }
        Iterator<ServerAddress> iterator = seeds.iterator();
        while (iterator.hasNext()) {
            ServerAddress next = iterator.next();
            sb.append(next.getHost());
            sb.append(":");
            sb.append(next.getPort());
            if (iterator.hasNext()) {
                sb.append(",");
            }
        }

        if (StringUtils.isEmpty(database)) {
            throw new MongoException("database is empty");
        }
        if (!database.equals("admin")) {
            sb.append("/");
            sb.append(database);
        }
        if (StringUtils.isNotEmpty(collection)) {
            sb.append(".");
            sb.append(collection);
        }
        return sb;
    }


    public void dropDatabase(final String connectionString) {
        MongoClientURI uri = new MongoClientURI(connectionString);
        new MongoClient(uri).dropDatabase(uri.getDatabase());
    }

    public static void dropCollection(final String connectionString, String database, String collection) {
        MongoClientURI uri = new MongoClientURI(connectionString);
        new MongoClient(uri).getDatabase(database).getCollection(collection).drop();
    }
}
