package com.shulianxunying.utils.locationrecognizeutil;

import com.shulianxunying.utils.locationrecognizeutil.utils.IsContainsArea;
import com.shulianxunying.utils.locationrecognizeutil.utils.ReadCountryAndCityMap;
import com.shulianxunying.utils.locationrecognizeutil.utils.ReadUniqueCityName;
import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;
import scala.Tuple5;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 19866 on 2017/6/19.
 * 识别特殊城市的地域
 */
public class SpecifyCityRecognize implements Serializable {
    private final String defaultLocation = "unknown";

    private Tuple5<ArrayList<String>, HashMap<String, String>, HashMap<String, String>, HashMap<String, String>, HashMap<String, String>> uniqueCity;
    private ArrayList<String> matchLocationNameList;
    private HashMap<String, String> distinguishCityMap;
    private HashMap<String, String> cityAndProvinceMap;
    private HashMap<String, String> provinceAndCountryMap;
    private HashMap<String, String> priorEnglish2ChineseMap;

    private Tuple4<
            Tuple3<LinkedHashMap<String, String>, LinkedHashMap<String, String>, HashSet<String>>,
            Tuple2<LinkedHashMap<String, HashSet<String>>, LinkedHashMap<String, HashSet<String>>>,
            HashMap<String, String>,
            HashMap<String, HashSet<String>>> cityAndCountryTuple;
    private Tuple3<LinkedHashMap<String, String>, LinkedHashMap<String, String>, HashSet<String>> positiveSeqTuple;
    private Tuple2<LinkedHashMap<String, HashSet<String>>, LinkedHashMap<String, HashSet<String>>> invertedSeqTuple;
    private HashMap<String, String> english2Chinese;
    private HashMap<String, HashSet<String>> cityAndDistinguishMap;

    public SpecifyCityRecognize(String cities) {
        this.uniqueCity = ReadUniqueCityName.readUniqueCityName(cities);
        this.matchLocationNameList = uniqueCity._1();
        this.distinguishCityMap = uniqueCity._2();
        this.cityAndProvinceMap = uniqueCity._3();
        this.provinceAndCountryMap = uniqueCity._4();
        this.priorEnglish2ChineseMap = uniqueCity._5();

        this.cityAndCountryTuple = ReadCountryAndCityMap.readCountryAndCityMap(cities);
        this.positiveSeqTuple = cityAndCountryTuple._1();
        this.invertedSeqTuple = cityAndCountryTuple._2();
        this.english2Chinese = cityAndCountryTuple._3();
        this.cityAndDistinguishMap = cityAndCountryTuple._4();
    }

    public SpecifyCityRecognize() {
        String cities = "北京,上海,广州,深圳,成都,杭州,武汉,天津,南京,重庆,西安,长沙,青岛,沈阳,大连,厦门,苏州,宁波,无锡";
        this.uniqueCity = ReadUniqueCityName.readUniqueCityName(cities);
        this.matchLocationNameList = uniqueCity._1();
        this.distinguishCityMap = uniqueCity._2();
        this.cityAndProvinceMap = uniqueCity._3();
        this.provinceAndCountryMap = uniqueCity._4();
        this.priorEnglish2ChineseMap = uniqueCity._5();

        this.cityAndCountryTuple = ReadCountryAndCityMap.readCountryAndCityMap(cities);
        this.positiveSeqTuple = cityAndCountryTuple._1();
        this.invertedSeqTuple = cityAndCountryTuple._2();
        this.english2Chinese = cityAndCountryTuple._3();
        this.cityAndDistinguishMap = cityAndCountryTuple._4();
    }

    //    private static final String cities = "上海,临沧,丽江,保山,大理,德宏,怒江,文山,昆明,昭通,思茅,普洱,曲靖,楚雄,玉溪,红河,西双版纳,迪庆,乌兰察布,乌海,兴安盟,包头,呼伦贝尔,呼和浩特,巴彦淖尔,赤峰,通辽,鄂尔多斯,锡林郭勒盟,阿拉善盟,北京,台中,台北,台南,嘉义,基隆,新竹,高雄,吉林,四平,延边,松原,白城,白山,辽源,通化,长春,乐山,内江,凉山,南充,宜宾,巴中,广元,广安,德阳,成都,攀枝花,泸州,甘孜,眉山,绵阳,自贡,资阳,达州,遂宁,阿坝,雅安,天津,中卫,吴忠,固原,石嘴山,银川,亳州,六安,合肥,安庆,宣城,宿州,巢湖,池州,淮北,淮南,滁州,芜湖,蚌埠,铜陵,阜阳,马鞍山,黄山,东营,临沂,威海,德州,日照,枣庄,泰安,济南,济宁,淄博,滨州,潍坊,烟台,聊城,莱芜,菏泽,青岛,临汾,吕梁,大同,太原,忻州,晋中,晋城,朔州,运城,长治,阳泉,中山,东莞,云浮,佛山,广州,惠州,揭阳,梅州,汕头,汕尾,江门,河源,深圳,清远,湛江,潮州,珠海,肇庆,茂名,阳江,韶关,北海,南宁,崇左,来宾,柳州,桂林,梧州,河池,玉林,百色,贵港,贺州,钦州,防城港,乌鲁木齐,伊犁,克孜勒苏,克拉玛依,博尔塔拉,吐鲁番,和田,哈密,喀什,塔城,巴音郭楞,昌吉,阿克苏,阿勒泰,南京,南通,宿迁,常州,徐州,扬州,无锡,泰州,淮阴,淮安,盐城,苏州,连云港,镇江,上饶,九江,南昌,吉安,宜春,抚州,新余,景德镇,萍乡,赣州,鹰潭,保定,唐山,廊坊,张家口,承德,沧州,石家庄,秦皇岛,衡水,邢台,邯郸,三门峡,信阳,南阳,周口,商丘,安阳,平顶山,开封,新乡,洛阳,漯河,濮阳,焦作,许昌,郑州,驻马店,鹤壁,丽水,台州,嘉兴,宁波,杭州,温州,湖州,绍兴,舟山,衢州,金华,三亚,三沙,海口,十堰,咸宁,孝感,宜昌,恩施,武汉,荆州,荆门,襄樊,襄阳,鄂州,随州,黄冈,黄石,娄底,岳阳,常德,张家界,怀化,株洲,永州,湘潭,湘西,益阳,衡阳,邵阳,郴州,长沙,澳门,临夏,兰州,嘉峪关,天水,定西,平凉,庆阳,张掖,武威,甘南,白银,酒泉,金昌,陇南,三明,南平,厦门,宁德,泉州,漳州,福州,莆田,龙岩,山南,拉萨,日喀则,昌都,林芝,那曲,阿里,六盘水,安顺,毕节,贵阳,遵义,铜仁,黔东南,黔南,黔西南,丹东,大连,抚顺,朝阳,本溪,沈阳,盘锦,营口,葫芦岛,辽阳,铁岭,锦州,阜新,鞍山,重庆,咸阳,商洛,安康,宝鸡,延安,榆林,汉中,渭南,西安,铜川,海东,海北,海南,海西,玉树,西宁,黄南,九龙,新界,香港,七台,伊春,佳木斯,双鸭山,哈尔滨,大兴安岭,大庆,牡丹江,绥化,鸡西,鹤岗,黑河,齐齐哈尔,云南,内蒙古,北京,台湾,吉林,四川,宁夏,安徽,山东,山西,广东,广西,新疆维吾尔,江苏,江西,河北,河南,浙江,海南,湖北,湖南,澳门,甘肃,福建,西藏,贵州,辽宁,陕西,青海,黑龙江";


    private HashSet<Tuple4<String, String, String, String>> dealByPositiveSeq(String location) {

        HashSet<Tuple4<String, String, String, String>> cityHitSet = new HashSet<>();
        if (StringUtils.isEmpty(location)) {
            return cityHitSet;
        } else {
            LinkedHashMap<String, String> cityAndProvinceMap = positiveSeqTuple._1();
            LinkedHashMap<String, String> provinceAndCountryMap = positiveSeqTuple._2();
            HashSet<String> countrySet = positiveSeqTuple._3();
            HashSet<Tuple2<String, String>> provinceHitSet = new HashSet<>();
            HashSet<String> countryHitSet = new HashSet<>();

            for (Map.Entry<String, String> cityAndProvinceEntry : cityAndProvinceMap.entrySet()) {
                String key = cityAndProvinceEntry.getKey();
                if (key.equals(""))
                    continue;
                String hitWords = IsContainsArea.isContainsArea(key.toLowerCase(), location);
                if (!hitWords.equals("NotHitAnyWord")) {
                    location = location.replaceAll(hitWords, "");
                    String city = cityAndProvinceEntry.getKey();
                    String province = cityAndProvinceEntry.getValue();
                    String country = provinceAndCountryMap.get(province);
                    String distinguish = "unknown";
                    for (String distinguishStr : cityAndDistinguishMap.getOrDefault(city, new HashSet<>())) {
                        String hitDistinguishWord = IsContainsArea.isContainsArea(distinguishStr.toLowerCase(), location);
                        if (!hitDistinguishWord.equals("NotHitAnyWord")) {
                            location = location.replaceAll(hitDistinguishWord, "");
                            distinguish = distinguishStr;
                            break;
                        }
                    }
                    country = english2Chinese.getOrDefault(country, country);
                    province = english2Chinese.getOrDefault(province, province);
                    city = english2Chinese.getOrDefault(city, city);
                    distinguish = english2Chinese.getOrDefault(distinguish, distinguish);

                    cityHitSet.add(new Tuple4<>(distinguish, city, province, country));
                }
            }
            for (Map.Entry<String, String> provinceAndCountryEntry : provinceAndCountryMap.entrySet()) {
                String hitWords = IsContainsArea.isContainsArea(provinceAndCountryEntry.getKey().toLowerCase(), location);
                if (!hitWords.equals("NotHitAnyWord")) {
                    location = location.replaceAll(hitWords, "");
                    String province = provinceAndCountryEntry.getKey();
                    //天坑！！！
                    //韩国:Korea	光州:Gwangju
                    //韩国:Korea	京畿道:Gyeonggi-do	广州市:Gwangju
                    if (province.equals("Gwangju"))
                        province = "光州";
                    String country = provinceAndCountryMap.get(province);

                    province = english2Chinese.getOrDefault(province, province);
                    country = english2Chinese.getOrDefault(country, country);

                    provinceHitSet.add(new Tuple2<>(province, country));
                }
            }

            for (String country : countrySet) {
                String hitWords = IsContainsArea.isContainsArea(country.toLowerCase(), location);
                if (!hitWords.equals("NotHitAnyWord")) {
                    location = location.replaceAll(hitWords, "");
                    country = english2Chinese.getOrDefault(country, country);
                    countryHitSet.add(country);
                }
            }
            if (!provinceHitSet.isEmpty()) {
                for (Tuple2<String, String> tuple : provinceHitSet) {
                    String province = tuple._1;
                    Boolean isContainProvince = false;
                    for (Tuple4<String, String, String, String> cityString : cityHitSet) {
                        if (cityString.toString().toLowerCase().contains(province)) {
                            isContainProvince = true;
                            break;
                        }
                    }
                    if (!isContainProvince) {
                        cityHitSet.add(new Tuple4<>("unknown", "unknown", province, tuple._2));
                    }
                }
            }

            if (!countryHitSet.isEmpty()) {
                for (String country : countryHitSet) {
                    Boolean isContainCountry = false;
                    for (Tuple4<String, String, String, String> cityString : cityHitSet) {
                        if (cityString.toString().toLowerCase().contains(country)) {
                            isContainCountry = true;
                            break;
                        }
                    }
                    if (!isContainCountry) {
                        cityHitSet.add(new Tuple4<>("unknown", "unknown", "unknown", country));
                    }
                }
            }
            if (cityHitSet.isEmpty() && provinceHitSet.isEmpty() && countryHitSet.isEmpty()) {
                cityHitSet.add(new Tuple4<>("unknown", "unknown", "unknown", "unknown"));
            }
        }
//        cityHitSet.forEach(p -> {
//            String city = p._2();
//            if (otherNameOfCity.containsKey(city)) {
//                cityHitSet.remove(p);
//                cityHitSet.add(new Tuple4<>(p._1(), otherNameOfCity.get(city), p._3(), p._4()));
//            }
//        });
        return cityHitSet;
    }

    private HashSet<Tuple4<String, String, String, String>> dealByInvertedSeq(String location) {
        HashSet<Tuple4<String, String, String, String>> cityHitSet = new HashSet<>();
        if (StringUtils.isEmpty(location)) {
            return cityHitSet;
        } else {
            LinkedHashMap<String, HashSet<String>> countryAndProvinceMap = invertedSeqTuple._1();
            LinkedHashMap<String, HashSet<String>> provinceAndCityMap = invertedSeqTuple._2();
            String defaultDistinguish = "unknown";
            for (Map.Entry<String, HashSet<String>> countryAndProvinceEntry : countryAndProvinceMap.entrySet()) {
                String countryString = "unknown";
                String country = countryAndProvinceEntry.getKey();
                if (location.contains(country.toLowerCase())) {
                    countryString = country;
                    HashSet<String> provinceSet = countryAndProvinceEntry.getValue();
                    String provinceString = "unknown";
                    if (!provinceSet.isEmpty()) {
                        for (String province : provinceSet) {
                            if (location.contains(province.toLowerCase())) {
                                provinceString = province;
                                HashSet<String> citySet = provinceAndCityMap.get(province);
                                String cityString = "unknown";
                                if (!citySet.isEmpty()) {
                                    for (String c : citySet) {
                                        if (location.contains(c.toLowerCase())) {
                                            cityString = c;
                                            cityHitSet.add(new Tuple4<>(
                                                    defaultDistinguish,
                                                    english2Chinese.getOrDefault(cityString, cityString),
                                                    english2Chinese.getOrDefault(provinceString, provinceString),
                                                    english2Chinese.getOrDefault(countryString, countryString)));
                                        } else {
                                            cityHitSet.add(new Tuple4<>(
                                                    defaultDistinguish,
                                                    english2Chinese.getOrDefault(cityString, cityString),
                                                    english2Chinese.getOrDefault(provinceString, provinceString),
                                                    english2Chinese.getOrDefault(countryString, countryString)));
                                        }
                                    }
                                } else {
                                    cityHitSet.add(new Tuple4<>(
                                            defaultDistinguish,
                                            english2Chinese.getOrDefault(cityString, cityString),
                                            english2Chinese.getOrDefault(provinceString, provinceString),
                                            english2Chinese.getOrDefault(countryString, countryString)));
                                }
                            } else {
                                cityHitSet.add(new Tuple4<>(defaultDistinguish, "unknown", provinceString, english2Chinese.getOrDefault(countryString, countryString)));
                            }
                        }
                    } else {
                        cityHitSet.add(new Tuple4<>(defaultDistinguish, "unknown", provinceString, english2Chinese.getOrDefault(countryString, countryString)));
                    }
                    if (provinceString.equals("unknown")) {
                        for (String province : provinceSet) {
                            provinceString = province;
                            HashSet<String> citySet = provinceAndCityMap.get(province);
                            String cityString = "unknown";
                            if (!citySet.isEmpty()) {
                                for (String c : citySet) {
                                    if (location.contains(c.toLowerCase())) {
                                        cityString = c;
                                        cityHitSet.add(new Tuple4<>(
                                                defaultDistinguish,
                                                english2Chinese.getOrDefault(cityString, cityString),
                                                english2Chinese.getOrDefault(provinceString, provinceString),
                                                english2Chinese.getOrDefault(countryString, countryString)));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (cityHitSet.isEmpty())
                        cityHitSet.add(new Tuple4<>(defaultDistinguish, "unknown", "unknown", countryString));
                }
            }
        }

        return cityHitSet;
    }

    private Tuple2<HashSet<Tuple4<String, String, String, String>>, String> priorRecognize(String location) {
        HashSet<Tuple4<String, String, String, String>> resultSet = new HashSet<>();
        if (StringUtils.isEmpty(location)) {
            return new Tuple2<>(resultSet, location);
        }

        location = location.toLowerCase();
        for (String lastArea : matchLocationNameList) {
            Pattern pattern = Pattern.compile(lastArea.toLowerCase());
            Matcher matcher = pattern.matcher(location);
            if (matcher.find()) {
                location = location.replace(matcher.group(), "");
                lastArea = priorEnglish2ChineseMap.getOrDefault(lastArea, lastArea);
                if (distinguishCityMap.containsKey(lastArea)) {
                    String city = distinguishCityMap.getOrDefault(lastArea, defaultLocation);
                    String province = cityAndProvinceMap.getOrDefault(city, defaultLocation);
                    String country = provinceAndCountryMap.getOrDefault(province, defaultLocation);
                    resultSet.add(new Tuple4<>(lastArea, city, province, country));
                } else if (cityAndProvinceMap.containsKey(lastArea)) {
                    String province = cityAndProvinceMap.getOrDefault(lastArea, defaultLocation);
                    String country = provinceAndCountryMap.getOrDefault(province, defaultLocation);
                    resultSet.add(new Tuple4<>(defaultLocation, lastArea, province, country));
                } else if (provinceAndCountryMap.containsKey(lastArea)) {
                    String country = provinceAndCountryMap.getOrDefault(lastArea, defaultLocation);
                    resultSet.add(new Tuple4<>(defaultLocation, defaultLocation, lastArea, country));
                } else {
                    resultSet.add(new Tuple4<>(defaultLocation, defaultLocation, defaultLocation, lastArea));
                }
            }
        }
        return new Tuple2<>(resultSet, location);
    }

    private HashSet<Tuple4<String, String, String, String>> behindRecognitionLocation(String location) {
        HashSet<Tuple4<String, String, String, String>> positive = dealByPositiveSeq(location.toLowerCase());
        HashSet<Tuple4<String, String, String, String>> inverted = dealByInvertedSeq(location.toLowerCase());
        HashSet<Tuple4<String, String, String, String>> returnSet;
        if (positive.size() == 1 && inverted.size() != 1) {
            Tuple4<String, String, String, String> tuple4 = (Tuple4<String, String, String, String>) positive.toArray()[0];
            if (tuple4._3().equals("unknown")) {
                returnSet = inverted;
            } else {
                inverted.add(tuple4);
                returnSet = inverted;
            }
        } else if (positive.size() != 1 && inverted.size() == 1) {
            Tuple4<String, String, String, String> tuple4 = (Tuple4<String, String, String, String>) inverted.toArray()[0];
            if (tuple4._3().equals("unknown")) {
                returnSet = positive;
            } else {
                positive.add(tuple4);
                returnSet = positive;
            }
        } else {
            positive.addAll(inverted);
            returnSet = positive;
        }
        return returnSet;
    }

    public HashSet<Tuple4<String, String, String, String>> locationRecognize(String location) {
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
                        } else {
                            if (!tuple4._3().equals(defaultLocation)) {
                                //country 相同province 为unknown  剔除
                                for (Tuple4<String, String, String, String> getProvinceRemoveTuple : resultSet) {
                                    if (getProvinceRemoveTuple._4().equals(tuple4._4()) && getProvinceRemoveTuple._3().equals(defaultLocation))
                                        set.add(getProvinceRemoveTuple);
                                }
                            }
                            if (!tuple4._2().equals(defaultLocation)) {
                                //province 相同city 为unknown  剔除
                                for (Tuple4<String, String, String, String> getCityRemoveTuple : resultSet) {
                                    if (getCityRemoveTuple._3().equals(tuple4._3()) && getCityRemoveTuple._2().equals(defaultLocation))
                                        set.add(getCityRemoveTuple);
                                }
                            }
                            if (!tuple4._1().equals(defaultLocation)) {
                                for (Tuple4<String, String, String, String> getDistinguishRemoveTuple : resultSet) {
                                    if (getDistinguishRemoveTuple._2().equals(tuple4._2()) && getDistinguishRemoveTuple._1().equals(defaultLocation))
                                        set.add(getDistinguishRemoveTuple);
                                }
                            }
                        }
                    }
                    return set;
                }
            });
            return resultSet;
        }
        return resultSet;
    }

    public static void main(String[] args) {
        HashSet<Tuple4<String, String, String, String>> a = new SpecifyCityRecognize().locationRecognize("常熟;苏州");
        for (Tuple4<String, String, String, String> b : a) {
            System.out.println(b.toString());
        }
    }
}
