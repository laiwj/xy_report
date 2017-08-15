package com.shulianxunying.utils;

import com.shulianxunying.resume.ResumeType;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.bson.Document;
import scala.Serializable;

/**
 * Created by SuChang on 2017/3/14 17:27.
 */
public class RDDUtils implements Serializable {


    public static JavaPairRDD<Document, ResumeType> unionAll(JavaPairRDD<Document, ResumeType>... rdds) {
        int length = rdds.length;
        if (length == 1)
            return rdds[0];
        JavaPairRDD<Document, ResumeType> out = null;
        if (length == 2) {
            out = rdds[0].union(rdds[1]);
            return out;
        } else {
            out = rdds[0].union(rdds[1]);
        }

        for (int i = 2; i < length; i++) {
            out = out.union(rdds[i]);
        }
        return out;
    }

    public static JavaPairRDD<Document, ResumeType> unionAll2(JavaPairRDD<Document, ResumeType>... rdds) {
        int size = rdds.length;

        if (size == 1) {
            return rdds[0];
        }
        if (size % 2 == 0) {
            JavaPairRDD<Document, ResumeType>[] list = new JavaPairRDD[size / 2];
            for (int i = 0; i < size / 2; i++) {
                JavaPairRDD<Document, ResumeType> union = rdds[i].union(rdds[size - i - 1]);
                list[i] = union;
            }
            if (list.length > 1) {
                return unionAll2(list);
            } else {
                return list[0];
            }
        } else {
            JavaPairRDD<Document, ResumeType>[] list = new JavaPairRDD[(size / 2) + 1];
            for (int i = 0; i < size / 2; i++) {
                JavaPairRDD<Document, ResumeType> union = rdds[i].union(rdds[size - i - 2]);
                list[i] = union;
            }
            list[(size / 2)] = rdds[size - 1];
            return unionAll2(list);
        }
    }


    /**
     * 读取文件 转为 mongordd
     *
     * @param path
     * @param jsc
     * @return
     */
    public static JavaRDD<Document> readTextToMongoRDD(String path, JavaSparkContext jsc) {
        // 载入文件
        if (StringUtils.isEmpty(path))
            path = "file://C:\\Users\\Su\\Desktop\\temp.json";
        final JavaRDD<String> lines = jsc.textFile("file://" + path);
        JavaRDD<Document> resumes = lines.map(new Function<String, Document>() {
            public Document call(String s) throws Exception {
                return Document.parse(s);
            }
        });

        return resumes;
    }
}
