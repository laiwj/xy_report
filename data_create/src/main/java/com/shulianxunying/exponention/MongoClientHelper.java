package com.shulianxunying.exponention;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.shulianxunying.utils.SparkMongoHelper;

import java.util.HashMap;

/**
 * Created by SuChang on 2017/5/12 14:14.
 */
public class MongoClientHelper {

    public MongoClient mongoClient;

    public MongoClient getClient(String database) {
        if (mongoClient == null) {
            HashMap<String, String> optins = new HashMap<String, String>();
            optins.put("authSource", "admin");
            String inputUri = SparkMongoHelper.createMongoUrl("10.101.1.171", 27017, "root", "sc123456", database, null, optins);
            mongoClient = new MongoClient(new MongoClientURI(inputUri));

        }
        return mongoClient;
    }
}
