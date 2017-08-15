package com.shulianxunying.utils.locationrecognizeutil;

import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;
import scala.Tuple4;

import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Predicate;

import static com.shulianxunying.utils.locationrecognizeutil.utils.BehindRecognitionLocation.behindRecognitionLocation;
import static com.shulianxunying.utils.locationrecognizeutil.utils.PriorRecognitionLocation.priorRecognize;

/**
 * Created by 19866 on 2017/6/15.
 * 主要用于识别简历中只含一两个关键词的地域
 */
public class LocationRecognize {
    public static void main(String[] args) {
        Iterator<Tuple4<String, String, String, String>> a = locationRecognize("天津");
        while (a.hasNext()) {
            System.out.println(a.next().toString());
        }
    }

    public static Iterator<Tuple4<String, String, String, String>> locationRecognize(String location) {
        HashSet<Tuple4<String, String, String, String>> resultSet = new HashSet<>();
        Tuple2<HashSet<Tuple4<String, String, String, String>>, String> priorTuple = priorRecognize(location);
        resultSet.addAll(priorTuple._1);
        location = priorTuple._2;
        if (StringUtils.isNotEmpty(location))
            resultSet.addAll(behindRecognitionLocation(location));
        if (resultSet.size() >= 2) {
            resultSet.removeIf(new Predicate<Tuple4<String, String, String, String>>() {
                @Override
                public boolean test(Tuple4<String, String, String, String> stringStringStringTuple3) {
                    HashSet<Tuple4<String, String, String, String>> removeTupleSet = getRemoveTupleSet();
                    Boolean flag = removeTupleSet.contains(stringStringStringTuple3);
                    return flag;
                }

                private HashSet<Tuple4<String, String, String, String>> getRemoveTupleSet() {
                    String defaultLocation = "unknown";
                    HashSet<Tuple4<String, String, String, String>> set = new HashSet<>();
                    for (Tuple4<String, String, String, String> tuple4 : resultSet) {
                        //在size 大于2 的set 中，有国家为 unknown 剔除
                        if (tuple4._4().equals(defaultLocation)) {
                            set.add(tuple4);
                        } else if (!tuple4._3().equals(defaultLocation)) {
                            //country 相同province 为unknown  剔除
                            for (Tuple4<String, String, String, String> getProvinceRemoveTuple : resultSet) {
                                if (getProvinceRemoveTuple._4().equals(tuple4._4()) && getProvinceRemoveTuple._3().equals(defaultLocation))
                                    set.add(getProvinceRemoveTuple);
                            }
                        } else if (!tuple4._2().equals(defaultLocation)) {
                            //province 相同city 为unknown  剔除
                            for (Tuple4<String, String, String, String> getCityRemoveTuple : resultSet) {
                                if (getCityRemoveTuple._3().equals(tuple4._3()) && getCityRemoveTuple._2().equals(defaultLocation))
                                    set.add(getCityRemoveTuple);
                            }
                        } else if (!tuple4._1().equals(defaultLocation)) {
                            for (Tuple4<String, String, String, String> getDistinguishRemoveTuple : resultSet) {
                                if (getDistinguishRemoveTuple._2().equals(tuple4._2()) && getDistinguishRemoveTuple._1().equals(defaultLocation))
                                    set.add(getDistinguishRemoveTuple);
                            }
                        }
                    }
                    return set;
                }
            });
            return resultSet.iterator();
        }
        return resultSet.iterator();
    }

}
