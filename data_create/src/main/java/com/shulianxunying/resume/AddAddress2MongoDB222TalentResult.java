package com.shulianxunying.resume;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.shulianxunying.utils.locationrecognizeutil.RecognizeFromCountry;
import org.bson.Document;
import scala.Tuple4;

import java.util.function.Consumer;

/**
 * Created by 19866 on 2017/6/26.
 */
public class AddAddress2MongoDB222TalentResult {

    public static void main(String[] args) {

        MongoClientURI connectionString = new MongoClientURI("mongodb://10.101.1.222:27018");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("talent_data");
        MongoCollection sourceCollection = database.getCollection("talent_radar");
//        MongoCollection saveCollection = database.getCollection("result_v1");

        FindIterable<Document> mongoCursor = sourceCollection.find(new Document("flag", "7"));
        mongoCursor.forEach(new Consumer<Document>() {
            String defaultLocation = "unknown";

            @Override
            public void accept(Document document) {
                String address = document.getString("nation");
                String country;
                String province;
                String city;
                Tuple4<String, String, String, String> recognizeDAddress = RecognizeFromCountry.recognizeLocation(address.toLowerCase());
                if (recognizeDAddress.equals(new Tuple4<>(defaultLocation, defaultLocation, defaultLocation, defaultLocation))) {
                    String nation = document.getString("nation");
                    Tuple4<String, String, String, String> addressNation = RecognizeFromCountry.recognizeLocation(nation.toLowerCase());
                    city = addressNation._2().equals(defaultLocation) ? "" : recognizeDAddress._2();
                    province = addressNation._3().equals(defaultLocation) ? "" : recognizeDAddress._3();
                    country = addressNation._4().equals(defaultLocation) ? "" : recognizeDAddress._4();
                } else {
                    city = recognizeDAddress._2().equals(defaultLocation) ? "" : recognizeDAddress._2();
                    province = recognizeDAddress._3().equals(defaultLocation) ? "" : recognizeDAddress._3();
                    country = recognizeDAddress._4().equals(defaultLocation) ? "" : recognizeDAddress._4();
                }
                System.out.println(address);
                if (address.contains("USA"))
                    country = "美国";
                if (address.contains("Soviet"))
                    country = "苏联";
                if (address.contains("east Timor"))
                    country = "东帝汶民主共和国";
                if (address.contains("Burma"))
                    country = "缅甸";
                if (address.contains("Kindom"))
                    country = "英国";
                if (address.contains("Zealand"))
                    country = "新西兰";

                System.out.println(country);
                sourceCollection.updateOne(document, new Document("$set", new Document("country", country)));
            }
        });
    }
}
