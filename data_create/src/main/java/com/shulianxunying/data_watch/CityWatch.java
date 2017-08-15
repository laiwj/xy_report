package com.shulianxunying.data_watch;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.WriteConfig;
import com.shulianxunying.utils.RDDUtils;
import com.shulianxunying.utils.SparkMongoHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.catalyst.expressions.In;
import org.bson.Document;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by SuChang on 2017/5/8 10:01.
 */
public class CityWatch {

    public static void main(String[] args) {
        String databaseName = "resume_report";

        HashMap<String, String> optins = new HashMap<String, String>();
        optins.put("authSource", "admin");
        String spark_temp = SparkMongoHelper.createMongoUrl("10.101.1.171", 27017, "root", "sc123456", databaseName, null, optins);
//        String spark_temp = SparkMongoHelper.createMongoUrl("118.123.173.86", 27017, "readwrite", "betagorw", "betago", null, optins);
        SparkConf conf = new SparkConf()
                .setAppName("data_watch_citys")
//                .setMaster("local[*]")
                .setMaster("spark://10.101.1.230:7077")
                .set("spark.mongodb.output.uri", spark_temp);

        JavaSparkContext jsc = new JavaSparkContext(conf);

        // 读取文件
        JavaRDD<Document> resumes = RDDUtils.readTextToMongoRDD(args[0], jsc);
        JavaPairRDD<String, Integer> hometownRDD = resumes.flatMapToPair(new PairFlatMapFunction<Document, String, Integer>() {
            @Override
            public Iterator<Tuple2<String, Integer>> call(Document document) throws Exception {
                ArrayList<Tuple2<String, Integer>> out = new ArrayList<Tuple2<String, Integer>>();
                func(document, "hometown", out);
                func(document, "expect_city", out);
                func(document, "living", out);

                return out.iterator();
            }

            public void func(Document document, String key, ArrayList<Tuple2<String, Integer>> out) {
                String value = document.getString(key);
                if (StringUtils.isNotEmpty(value) && value.length() < 40)
                    for (String city : value.split(";|,|，| ")) {
                        city = city.trim();
                        out.add(new Tuple2<>(city, 1));
                    }
            }
        });
        hometownRDD = hometownRDD.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        hometownRDD.foreach(new VoidFunction<Tuple2<String, Integer>>() {
            @Override
            public void call(Tuple2<String, Integer> tuple2) throws Exception {
                int length = tuple2._1().length();
                if (length > 5 && length < 40)
                    System.out.println(tuple2._1() + "\t" + tuple2._2());
            }
        });
        JavaRDD<Document> out = hometownRDD.map(new Function<Tuple2<String, Integer>, Document>() {
            @Override
            public Document call(Tuple2<String, Integer> v1) throws Exception {
                return new Document("city", v1._1()).append("count", v1._2());
            }
        });

        Map<String, String> writeOverrides = new HashMap<String, String>();
        writeOverrides.put("database", databaseName);
        writeOverrides.put("collection", "resume_citys");
        writeOverrides.put("writeConcern.wTimeoutMS", "" + (1000 * 60 * 60 * 2));
        WriteConfig wc = WriteConfig.create(conf, writeOverrides);
        MongoSpark.save(out, wc);
    }
}
