package com.shulianxunying.resume;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
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
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;
import org.bson.Document;
import scala.Tuple2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SuChang on 2017/5/8 15:09.
 */
public class ResumeReport2 {


    public static JavaRDD<Document> analysisCount(JavaPairRDD<Document, ResumeCityFunc> dimenResuem) {
        JavaPairRDD<String, Integer> tempRDD = dimenResuem.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeCityFunc>, String, Integer>() {
            @Override
            public Iterator<Tuple2<String, Integer>> call(Tuple2<Document, ResumeCityFunc> tuple2) throws Exception {
                ArrayList<Tuple2<String, Integer>> out = new ArrayList<Tuple2<String, Integer>>();
                ResumeCityFunc resumeCityFunc = tuple2._2();
                HashSet<String> city = resumeCityFunc.getLiving_city();
                city.remove("unknow");
                if (city.size() <= 0) {
                    city = resumeCityFunc.getExpect_city();
                }
                city.remove("unknow");
                if (city.size() <= 0) {
                    city = resumeCityFunc.getHometown_city();
                }
                for (String s : city) {
                    String data = s + "\t" + resumeCityFunc.getFunc() + "\t" + resumeCityFunc.getSecond_level() + "\t" + resumeCityFunc.getPosition() + "\t" + resumeCityFunc.getIndustry();
                    out.add(new Tuple2<>(data, 1));
                }
                return out.iterator();
            }
        });
        tempRDD = tempRDD.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        return tempRDD.map(new Function<Tuple2<String, Integer>, Document>() {
            @Override
            public Document call(Tuple2<String, Integer> v1) throws Exception {
                String[] split = v1._1().split("\t");
                Document out = new Document();
                out.put("city", split[0]);
                out.put("func", split[1]);
                out.put("second", split[2]);
                out.put("position", split[3]);
                out.put("industry", split[4]);
                out.put("count", v1._2());
                out.put("type", "city_position_count");
                return out;
            }
        });
    }


    public static JavaRDD<Document> analysisFlow(JavaPairRDD<Document, ResumeCityFunc> dimenResuem, Broadcast<List<PositionFunc>> broadcast, String out_citys) {
        String[] citys = out_citys.split(",");
        JavaPairRDD<String, Integer> reduceByKey = dimenResuem.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeCityFunc>, String, Integer>() {
            private static final long serialVersionUID = 1L;

            public Iterator<Tuple2<String, Integer>> call(Tuple2<Document, ResumeCityFunc> tuple2) throws Exception {
                ArrayList<Tuple2<String, Integer>> out = new ArrayList<Tuple2<String, Integer>>();
                ResumeCityFunc resumeCityFunc = tuple2._2();
                HashSet<String> living_city = resumeCityFunc.getLiving_city();
                if (living_city.size() <= 0)
                    living_city = resumeCityFunc.getHometown_city();
                ArrayList<Document> workExperienceList = (ArrayList<Document>) tuple2._1().get("workExperienceList");
                String enterprise_name = null;
                HashSet<String> preCitys = new HashSet<String>();
                if (workExperienceList != null && workExperienceList.size() > 2) {
                    String nowCompany = workExperienceList.get(0).getString("enterprise_name");
                    if (StringUtils.isNotEmpty(nowCompany)) {
                        for (String city : citys) {
                            if (nowCompany.contains(city))
                                living_city.add(city);
                        }
                    }
                    enterprise_name = workExperienceList.get(1).getString("enterprise_name");
                    if (StringUtils.isEmpty(enterprise_name) && workExperienceList.size() > 3) {
                        enterprise_name = workExperienceList.get(2).getString("enterprise_name");
                    }
                    if (StringUtils.isNotEmpty(enterprise_name)) {
                        for (String city : citys) {
                            if (enterprise_name.contains(city))
                                preCitys.add(city);
                        }
                    }
                } else if (workExperienceList != null && workExperienceList.size() == 1) {
                    String nowCompany = workExperienceList.get(0).getString("enterprise_name");
                    if (StringUtils.isNotEmpty(nowCompany)) {
                        for (String city : citys) {
                            if (nowCompany.contains(city))
                                living_city.add(city);
                        }
                    }
                    // todo 需要一份学校所在地的数据支撑
                    String college_name = tuple2._1().getString("college_name");
                    HashSet<String> hometown_city = tuple2._2().getHometown_city();
                    if (StringUtils.isNotEmpty(college_name)) {
                        for (String city : citys) {
                            if (college_name.contains(city))
                                preCitys.add(city);
                        }
                    } else {
                        for (String s : hometown_city)
                            preCitys.add(s);
                    }

                }

                living_city.remove("unknow");
                if (living_city.size() == 0)
                    living_city.add("unknow");
                if (preCitys.size() == 0)
                    preCitys.add("unknow");
                for (String living : living_city) {
                    for (String preCity : preCitys) {
                        if (!living.equals(preCity))
                            out.add(new Tuple2<>(preCity + "\t" + living + "\t" + tuple2._2().getFunc() + "\t" + tuple2._2().getSecond_level() + "\t" + tuple2._2().getPosition() + "\t" + resumeCityFunc.getIndustry(), 1));
                    }
                }
                return out.iterator();
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });

        JavaRDD<Document> out = reduceByKey.map(new Function<Tuple2<String, Integer>, Document>() {
            @Override
            public Document call(Tuple2<String, Integer> v1) throws Exception {
                String[] split = v1._1().split("\t");
                Document out = new Document();
                out.append("preCity", split[0]);
                out.append("living", split[1]);
                out.append("func", split[2]);
                out.append("second", split[3]);
                out.append("position", split[4]);
                out.append("industry", split[5]);
                out.append("count", v1._2());
                out.append("type", "flow");
                return out;
            }
        });
        return out;
    }

    /**
     * 职能流动
     *
     * @param dimenResuem
     * @param broadcast
     * @return
     */
    public static JavaRDD<Document> analysisFuncFlow(JavaPairRDD<Document, ResumeCityFunc> dimenResuem, Broadcast<List<PositionFunc>> broadcast) {
        JavaPairRDD<String, Integer> reduceByKey = dimenResuem.mapToPair(new PairFunction<Tuple2<Document, ResumeCityFunc>, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(Tuple2<Document, ResumeCityFunc> tuple2) throws Exception {
                Document document = tuple2._1();
                String last_position_name = document.getString("last_position_name");
                ArrayList<Document> workExperienceList = (ArrayList<Document>) document.get("workExperienceList");
                String[] strings1 = new String[]{"unknow", "unknow", "unknow"};
                String[] strings2 = new String[]{"unknow", "unknow", "unknow"};

                if (workExperienceList != null && workExperienceList.size() > 1) {
                    String position_name1 = workExperienceList.get(0).getString("position_name");
                    String position_name2 = workExperienceList.get(1).getString("position_name");
                    if (StringUtils.isEmpty(last_position_name))
                        last_position_name = position_name1;
                    if (StringUtils.isEmpty(position_name2) && workExperienceList.size() > 2)
                        position_name2 = workExperienceList.get(2).getString("position_name");
                    strings1 = setFlag(last_position_name);
                    strings2 = setFlag(position_name2);

                } else if (workExperienceList != null && workExperienceList.size() == 1) {
                    String position_name1 = workExperienceList.get(0).getString("position_name");
                    if (StringUtils.isEmpty(last_position_name))
                        last_position_name = position_name1;
                    strings1 = setFlag(last_position_name);

                } else {
                    if (StringUtils.isNotEmpty(last_position_name)) {
                        strings1 = setFlag(last_position_name);
                    }
                }
                return new Tuple2<String, Integer>(createStr(strings1, strings2, tuple2._2().getIndustry()), 1);
            }

            public String createStr(String[] strings1, String[] strings2, String industry) {
                StringBuilder sb = new StringBuilder();
                sb.append(industry);
                sb.append("\t");
                sb.append(strings1[0]);
                sb.append("\t");
                sb.append(strings1[1]);
                sb.append("\t");
                sb.append(strings1[2]);
                sb.append("\t");
                sb.append(strings2[0]);
                sb.append("\t");
                sb.append(strings2[1]);
                sb.append("\t");
                sb.append(strings2[2]);
                return sb.toString();
            }

            public String[] setFlag(String position_name) {
                List<PositionFunc> positionFuncs = broadcast.getValue();
                if (StringUtils.isNotEmpty(position_name)) {
                    for (PositionFunc positionFunc : positionFuncs) {
                        boolean flag = positionFunc.positionHit(position_name);
                        if (!flag)
                            continue;
                        return new String[]{positionFunc.getFunc(), positionFunc.getSecond_level(), positionFunc.getPosition()};
                    }
                } else {
                    return new String[]{"unknow", "unknow", "unknow"};
                }
                return new String[]{"unknow", "unknow", "unknow"};
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        return reduceByKey.map(new Function<Tuple2<String, Integer>, Document>() {
            @Override
            public Document call(Tuple2<String, Integer> v1) throws Exception {
                String[] split = v1._1().split("\t");
                Document out = new Document();
                out.append("industry", split[0]);
                out.append("func", split[1]);
                out.append("second", split[2]);
                out.append("position", split[3]);
                out.append("pre_func", split[4]);
                out.append("pre_second", split[5]);
                out.append("pre_position", split[6]);
                out.append("count", v1._2());
                out.append("type", "func_flow");
                return out;
            }
        });
    }

    /**
     * 生成 某年某周的数据
     *
     * @param args
     */
    public static void excute(String[] args, Date start_time, Date end_time) {
        // 生成 职能 子分类 职位 的三层映射关系
        List<PositionFunc> positionFuncList = FuncPositionMap.getList();
        // 配置spark
        String database = "resume_report";
        String collectionName = "resume241_20170321";
        HashMap<String, String> optins = new HashMap<String, String>();
        optins.put("authSource", "admin");
        String inputUri = SparkMongoHelper.createMongoUrl("10.101.1.171", 27017, "root", "sc123456", database, null, optins);
        // 根据 日期参数 设置appName
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        SparkConf conf = new SparkConf()
                .setAppName("ResumeReport" + sdf.format(start_time) + "_" + sdf.format(end_time))
//                .setMaster("local[*]")
                .setMaster("spark://10.101.1.230:7077")
                .set("spark.mongodb.output.uri", inputUri)
                .set("spark.mongodb.input.uri", inputUri);
        JavaSparkContext jsc = new JavaSparkContext(conf);

        final Broadcast<List<PositionFunc>> broadcast = jsc.broadcast(positionFuncList);
        // 根据参数 读取mongo数据
        Map<String, String> readOverWrite = new HashMap<>();
        readOverWrite.put("collection", collectionName);
        ReadConfig readConfig = ReadConfig.create(conf, readOverWrite);
        String query = "{$match : {'crawled_time' : {$gte: " + start_time.getTime() + ",$lt: " + end_time.getTime() + "} } }";
//        String query = "{$match : {'crawled_time' : {$gte: " + 1488741963000l + ",$lt: " + 1488742565000l + "} } }";
//        JavaMongoRDD<Document> resumeMongoRDD = MongoSpark.load(jsc, readConfig)
//                .withPipeline(Arrays.asList(Document.parse(query)));


        // 读取公司分类
        Map<String, String> readOverWrite2 = new HashMap<>();
        readOverWrite2.put("collection", "resume_compay_industy");
        ReadConfig readConfig2 = ReadConfig.create(conf, readOverWrite2);
        JavaMongoRDD<Document> company = MongoSpark.load(jsc, readConfig2);


        JavaRDD<CompanyKey> companyRdd = company.map(new Function<Document, CompanyKey>() {
            @Override
            public CompanyKey call(Document document) throws Exception {
                CompanyKey companyKey = new CompanyKey(document.getString("sub_name"));
                for (String s : document.getString("industy").split(",")) {
                    if (StringUtils.isNotEmpty(s))
                        companyKey.getIndustry().add(s);
                }
                for (String s : document.getString("hit_word").split(",")) {
                    if (StringUtils.isNotEmpty(s))
                        companyKey.getKeys().add(s);
                }
                return companyKey;
            }
        });

        List<CompanyKey> companyList = companyRdd.collect();
//        JavaRDD<Document> resumeMongoRDD = RDDUtils.readTextToMongoRDD(null, jsc);
        JavaRDD<Document> resumeMongoRDD = RDDUtils.readTextToMongoRDD(args[0], jsc);
        resumeMongoRDD = resumeMongoRDD.filter(new Function<Document, Boolean>() {
            @Override
            public Boolean call(Document v1) throws Exception {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String update_time = v1.getString("update_time");
                Long crawled_time = 0l;
                try {
                    crawled_time = v1.getLong("crawled_time");
                } catch (Exception e) {
                }

                Long parser_time = v1.getLong("parser_time");
                Date parse = null;
                try {
                    if (StringUtils.isNotEmpty(update_time)) {
                        parse = sdf.parse(update_time);
                        if (parse.after(start_time) && parse.before(end_time))
                            return true;
                    } else if (crawled_time != 0) {
                        if (crawled_time > start_time.getTime() && crawled_time < end_time.getTime())
                            return true;
                    } else if (parser_time != 0) {
                        if (parser_time > start_time.getTime() && parser_time < end_time.getTime())
                            return true;
                    }
                } catch (Exception e) {

                }
                return false;
            }
        });

//        System.out.println(resumeMongoRDD.count());

        // 转为 用于分类的rdd
        JavaPairRDD<Document, ResumeCityFunc> dimenResuem = resumeMongoRDD.mapToPair(new PairFunction<Document, Document, ResumeCityFunc>() {
            public Tuple2<Document, ResumeCityFunc> call(Document document) throws Exception {
                return new Tuple2<Document, ResumeCityFunc>(document, new ResumeCityFunc());
            }
        });

        String out_citys = "上海,临沧,丽江,保山,大理,德宏,怒江,文山,昆明,昭通,思茅,普洱,曲靖,楚雄,玉溪,红河,西双版纳,迪庆,乌兰察布,乌海,兴安盟,包头,呼伦贝尔,呼和浩特,巴彦淖尔,赤峰,通辽,鄂尔多斯,锡林郭勒盟,阿拉善盟,北京,台中,台北,台南,嘉义,基隆,新竹,高雄,吉林,四平,延边,松原,白城,白山,辽源,通化,长春,乐山,内江,凉山,南充,宜宾,巴中,广元,广安,德阳,成都,攀枝花,泸州,甘孜,眉山,绵阳,自贡,资阳,达州,遂宁,阿坝,雅安,天津,中卫,吴忠,固原,石嘴山,银川,亳州,六安,合肥,安庆,宣城,宿州,巢湖,池州,淮北,淮南,滁州,芜湖,蚌埠,铜陵,阜阳,马鞍山,黄山,东营,临沂,威海,德州,日照,枣庄,泰安,济南,济宁,淄博,滨州,潍坊,烟台,聊城,莱芜,菏泽,青岛,临汾,吕梁,大同,太原,忻州,晋中,晋城,朔州,运城,长治,阳泉,中山,东莞,云浮,佛山,广州,惠州,揭阳,梅州,汕头,汕尾,江门,河源,深圳,清远,湛江,潮州,珠海,肇庆,茂名,阳江,韶关,北海,南宁,崇左,来宾,柳州,桂林,梧州,河池,玉林,百色,贵港,贺州,钦州,防城港,乌鲁木齐,伊犁,克孜勒苏,克拉玛依,博尔塔拉,吐鲁番,和田,哈密,喀什,塔城,巴音郭楞,昌吉,阿克苏,阿勒泰,南京,南通,宿迁,常州,徐州,扬州,无锡,泰州,淮阴,淮安,盐城,苏州,连云港,镇江,上饶,九江,南昌,吉安,宜春,抚州,新余,景德镇,萍乡,赣州,鹰潭,保定,唐山,廊坊,张家口,承德,沧州,石家庄,秦皇岛,衡水,邢台,邯郸,三门峡,信阳,南阳,周口,商丘,安阳,平顶山,开封,新乡,洛阳,漯河,濮阳,焦作,许昌,郑州,驻马店,鹤壁,丽水,台州,嘉兴,宁波,杭州,温州,湖州,绍兴,舟山,衢州,金华,三亚,三沙,海口,十堰,咸宁,孝感,宜昌,恩施,武汉,荆州,荆门,襄樊,襄阳,鄂州,随州,黄冈,黄石,娄底,岳阳,常德,张家界,怀化,株洲,永州,湘潭,湘西,益阳,衡阳,邵阳,郴州,长沙,澳门,临夏,兰州,嘉峪关,天水,定西,平凉,庆阳,张掖,武威,甘南,白银,酒泉,金昌,陇南,三明,南平,厦门,宁德,泉州,漳州,福州,莆田,龙岩,山南,拉萨,日喀则,昌都,林芝,那曲,阿里,六盘水,安顺,毕节,贵阳,遵义,铜仁,黔东南,黔南,黔西南,丹东,大连,抚顺,朝阳,本溪,沈阳,盘锦,营口,葫芦岛,辽阳,铁岭,锦州,阜新,鞍山,重庆,咸阳,商洛,安康,宝鸡,延安,榆林,汉中,渭南,西安,铜川,海东,海北,海南,海西,玉树,西宁,黄南,九龙,新界,香港,七台,伊春,佳木斯,双鸭山,哈尔滨,大兴安岭,大庆,牡丹江,绥化,鸡西,鹤岗,黑河,齐齐哈尔,云南,内蒙古,北京,台湾,吉林,四川,宁夏,安徽,山东,山西,广东,广西,新疆维吾尔,江苏,江西,河北,河南,浙江,海南,湖北,湖南,澳门,甘肃,福建,西藏,贵州,辽宁,陕西,青海,黑龙江";
        String out_citys_exclude = "上海,临沧,丽江,保山,大理,德宏,怒江,文山,昆明,昭通,思茅,普洱,曲靖,楚雄,玉溪,红河,西双版纳,迪庆,乌兰察布,乌海,兴安盟,包头,呼伦贝尔,呼和浩特,巴彦淖尔,赤峰,通辽,鄂尔多斯,锡林郭勒盟,阿拉善盟,北京,台中,台北,台南,嘉义,基隆,新竹,高雄,吉林,四平,延边,松原,白城,白山,辽源,通化,长春,乐山,内江,凉山,南充,宜宾,巴中,广元,广安,德阳,成都,攀枝花,泸州,甘孜,眉山,绵阳,自贡,资阳,达州,遂宁,阿坝,雅安,天津,中卫,吴忠,固原,石嘴山,银川,亳州,六安,合肥,安庆,宣城,宿州,巢湖,池州,淮北,淮南,滁州,芜湖,蚌埠,铜陵,阜阳,马鞍山,黄山,东营,临沂,威海,德州,日照,枣庄,泰安,济南,济宁,淄博,滨州,潍坊,烟台,聊城,莱芜,菏泽,青岛,临汾,吕梁,大同,太原,忻州,晋中,晋城,朔州,运城,长治,阳泉,中山,东莞,云浮,佛山,广州,惠州,揭阳,梅州,汕头,汕尾,江门,河源,深圳,清远,湛江,潮州,珠海,肇庆,茂名,阳江,韶关,北海,南宁,崇左,来宾,柳州,桂林,梧州,河池,玉林,百色,贵港,贺州,钦州,防城港,乌鲁木齐,伊犁,克孜勒苏,克拉玛依,博尔塔拉,吐鲁番,和田,哈密,喀什,塔城,巴音郭楞,昌吉,阿克苏,阿勒泰,南京,南通,宿迁,常州,徐州,扬州,无锡,泰州,淮阴,淮安,盐城,苏州,连云港,镇江,上饶,九江,南昌,吉安,宜春,抚州,新余,景德镇,萍乡,赣州,鹰潭,保定,唐山,廊坊,张家口,承德,沧州,石家庄,秦皇岛,衡水,邢台,邯郸,三门峡,信阳,南阳,周口,商丘,安阳,平顶山,开封,新乡,洛阳,漯河,濮阳,焦作,许昌,郑州,驻马店,鹤壁,丽水,台州,嘉兴,宁波,杭州,温州,湖州,绍兴,舟山,衢州,金华,三亚,三沙,海口,十堰,咸宁,孝感,宜昌,恩施,武汉,荆州,荆门,襄樊,襄阳,鄂州,随州,黄冈,黄石,娄底,岳阳,常德,张家界,怀化,株洲,永州,湘潭,湘西,益阳,衡阳,邵阳,郴州,长沙,澳门,临夏,兰州,嘉峪关,天水,定西,平凉,庆阳,张掖,武威,甘南,白银,酒泉,金昌,陇南,三明,南平,厦门,宁德,泉州,漳州,福州,莆田,龙岩,山南,拉萨,日喀则,昌都,林芝,那曲,阿里,六盘水,安顺,毕节,贵阳,遵义,铜仁,黔东南,黔南,黔西南,丹东,大连,抚顺,朝阳,本溪,沈阳,盘锦,营口,葫芦岛,辽阳,铁岭,锦州,阜新,鞍山,重庆,咸阳,商洛,安康,宝鸡,延安,榆林,汉中,渭南,西安,铜川,海东,海北,海南,海西,玉树,西宁,黄南,九龙,新界,香港,七台,伊春,佳木斯,双鸭山,哈尔滨,大兴安岭,大庆,牡丹江,绥化,鸡西,鹤岗,黑河,齐齐哈尔,台湾";

        // 给简历打  城市、职位、行业 标签
        dimenResuem = dimenResuem.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeCityFunc>, Document, ResumeCityFunc>() {
            public String[] citys = out_citys.split(",");

            @Override
            public Iterator<Tuple2<Document, ResumeCityFunc>> call(Tuple2<Document, ResumeCityFunc> tuple2) throws Exception {
                ArrayList<Tuple2<Document, ResumeCityFunc>> out = new ArrayList<Tuple2<Document, ResumeCityFunc>>();
                Document document = tuple2._1();
                getValue(document, "expect_city", tuple2._2().getExpect_city());
                getValue(document, "living", tuple2._2().getLiving_city());
                getValue(document, "hometown", tuple2._2().getHometown_city());
                List<PositionFunc> positionFuncs = broadcast.getValue();

                String position_name = document.getString("last_position_name");
                ArrayList<Document> workExperienceList = (ArrayList<Document>) document.get("workExperienceList");
                if (StringUtils.isEmpty(position_name)) {
                    if (workExperienceList != null && workExperienceList.size() > 0) {
                        position_name = workExperienceList.get(0).getString("position_name");
                        if (StringUtils.isEmpty(position_name) && workExperienceList.size() > 1)
                            position_name = workExperienceList.get(1).getString("position_name");
                    }
                }
                if (StringUtils.isNotEmpty(position_name)) {
                    for (PositionFunc positionFunc : positionFuncs) {
                        boolean flag = positionFunc.positionHit(position_name);
                        if (!flag)
                            continue;
                        tuple2._2().setFunc(positionFunc.getFunc());
                        tuple2._2().setSecond_level(positionFunc.getSecond_level());
                        tuple2._2().setPosition(positionFunc.getPosition());
                    }
                } else {
                    tuple2._2().setFunc("unknow");
                    tuple2._2().setSecond_level("unknow");
                    tuple2._2().setPosition("unknow");
                }
                // 行业
                HashSet<String> companyNames = new HashSet<String>();
                String last_enterprise_name = document.getString("last_enterprise_name");
                companyNames.add(last_enterprise_name);
                if (workExperienceList != null && workExperienceList.size() > 0) {
                    for (Document document1 : workExperienceList) {
                        String enterprise_name = document1.getString("enterprise_name");
                        companyNames.add(enterprise_name);
                    }
                }
                companyNames.remove("");
                companyNames.remove(null);
                if (companyNames.size() > 0) {
                    for (String s : companyNames) {
                        for (CompanyKey companyKey : companyList) {
                            if (companyKey.getKeyword().length() <= 3) {
                                if (s.contains(companyKey.getKeyword())) {
                                    boolean flag = false;
                                    for (String key : companyKey.getKeys()) {
                                        if (s.contains(key)) {
                                            flag = true;
                                            break;
                                        }
                                    }
                                    if (flag) {
                                        for (String industry : companyKey.getIndustry()) {
                                            ResumeCityFunc clone = tuple2._2().clone();
                                            clone.setIndustry(industry);
                                            out.add(new Tuple2<>(tuple2._1(), clone));
                                        }
                                    }
                                }
                            } else {
                                if (s.contains(companyKey.getKeyword())) {
                                    for (String industry : companyKey.getIndustry()) {
                                        ResumeCityFunc clone = tuple2._2().clone();
                                        clone.setIndustry(industry);
                                        out.add(new Tuple2<>(tuple2._1(), clone));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    out.add(tuple2);
                }
                if (out.size() == 0)
                    out.add(tuple2);
                return out.iterator();
            }

            public void getValue(Document document, String key, HashSet<String> set) {
                String value = document.getString(key);
                if (StringUtils.isNotEmpty(value)) {
                    for (String city : citys) {
                        if (value.contains(city))
                            set.add(city);
                    }
                    if (set.size() == 0)
                        set.add("unknow");
                } else {
                    set.add("unknow");
                }
            }
        });


        Map<String, String> writeOverrides = new HashMap<String, String>();
        writeOverrides.put("database", "resume_report");
        writeOverrides.put("collection", "resume_report_" + sdf.format(start_time) + "_" + sdf.format(end_time));
        writeOverrides.put("writeConcern.wTimeoutMS", "" + (1000 * 60 * 60 * 2));
        WriteConfig wc = WriteConfig.create(conf, writeOverrides);

        dimenResuem.take(100).forEach(p -> System.out.println(p._2()));

//         按照 城市+职位统计个数
//        JavaRDD<Document> cityPositionOut = analysisCount(dimenResuem);
//        cityPositionOut.take(100).forEach(p -> System.out.println(p));
//        System.out.println(cityPositionOut.count());
//        MongoSpark.save(cityPositionOut, wc);

        // 计算人才地域流动
//        JavaRDD<Document> flowOut = analysisFlow(dimenResuem, broadcast, out_citys_exclude);
//        System.out.println(flowOut.count());
//        MongoSpark.save(flowOut, wc);
//        flowOut.take(100).forEach(p -> System.out.println(p));

        // 计算人才职能-岗位流动
        JavaRDD<Document> funcFlowOut = analysisFuncFlow(dimenResuem, broadcast);
        MongoSpark.save(funcFlowOut, wc);
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start_time = null, end_time = null;
        if (args.length >= 3) {
            start_time = sdf.parse(args[1]);
            end_time = sdf.parse(args[2]);
        } else {
            start_time = new Date(114, 3, 1);
            end_time = new Date(118, 3, 31);
        }
        excute(args, start_time, end_time);
    }
}
