package com.shulianxunying.position;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import com.shulianxunying.resume.CompanyKey;
import com.shulianxunying.resume.FuncPositionMap;
import com.shulianxunying.resume.PositionFunc;
import com.shulianxunying.utils.KeyValueUtils;
import com.shulianxunying.utils.RDDUtils;
import com.shulianxunying.utils.SparkMongoHelper;
import com.shulianxunying.utils.locationrecognizeutil.SpecifyCityRecognize;
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
import scala.Tuple4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SuChang on 2017/5/11 10:42.
 */
public class PositionReport {

    public static void execute(HashMap<String, String> argsMap, Date start_time, Date end_time, JSONObject mongoConfig) {
        // 生成 职能 子分类 职位 的三层映射关系
        List<PositionFunc> positionFuncList = FuncPositionMap.getList();
        //配置 MongoDB
        String database = mongoConfig.getString("database");
        String in_database = mongoConfig.getString("in_database");
        String in_collectionName = mongoConfig.getString("in_collection");
        String out_collectionName = mongoConfig.getString("out_collection");
        String authSource = mongoConfig.getString("authSource");
        String ip = mongoConfig.getString("ip");
        String port = mongoConfig.getString("port");
        String inputUri = "";
        if (StringUtils.isNotEmpty(mongoConfig.getString("username"))) {
            HashMap<String, String> optins = new HashMap<String, String>();
            optins.put("authSource", authSource);
            inputUri = SparkMongoHelper.createMongoUrl(ip, Integer.parseInt(port), mongoConfig.getString("username"), mongoConfig.getString("password"), in_database, in_collectionName, optins);
        } else {
            inputUri = SparkMongoHelper.createMongoUrl(ip, Integer.parseInt(port), null, null, in_database, in_collectionName);
        }
        // 根据 日期参数 设置appName
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        SparkConf conf = new SparkConf()
                .setAppName("PositionReport" + sdf.format(start_time) + "_" + sdf.format(end_time))
//                .setMaster("local[*]")
                .setMaster("spark://10.101.1.230:7077")
                .set("spark.mongodb.output.uri", inputUri)
                .set("spark.mongodb.input.uri", inputUri);
        JavaSparkContext jsc = new JavaSparkContext(conf);
        final Broadcast<List<PositionFunc>> broadcast = jsc.broadcast(positionFuncList);
        JavaRDD<Document> mongoRDD = RDDUtils.readTextToMongoRDD(argsMap.get("--paths"), jsc);

        // 过滤
        mongoRDD = mongoRDD.filter(new Function<Document, Boolean>() {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

            @Override
            public Boolean call(Document v1) throws Exception {

                String publish_time = v1.getString("publish_time");
                String crawl_time = v1.getString("crawl_time");
                if (StringUtils.isNotEmpty(publish_time)) {
                    try {
                        Date parse = sdf1.parse(publish_time);
                        if (parse.after(start_time) && parse.before(end_time))
                            return true;
                    } catch (ParseException e) {
                    }
                }
                if (StringUtils.isNotEmpty(crawl_time)) {
                    try {
                        Date parse = sdf2.parse(crawl_time);
                        if (parse.after(start_time) && parse.before(end_time))
                            return true;
                    } catch (ParseException e) {
                        return false;
                    }
                }
                return false;
            }
        });

        //清理重复数据
        mongoRDD = mongoRDD.mapToPair(new PairFunction<Document, String, Document>() {
            @Override
            public Tuple2<String, Document> call(Document document) throws Exception {
                return new Tuple2<String, Document>(document.getString("work_id"), document);
            }
        }).reduceByKey(new Function2<Document, Document, Document>() {
            @Override
            public Document call(Document v1, Document v2) throws Exception {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String crawl_time1 = v1.getString("crawl_time");
                if (StringUtils.isEmpty(crawl_time1))
                    return v2;
                String crawl_time2 = v2.getString("crawl_time");
                if (StringUtils.isEmpty(crawl_time2))
                    return v1;
                Date parse1;
                Date parse2;
                try {
                    parse1 = sdf.parse(crawl_time1);
                } catch (ParseException e) {
                    return v2;
                }
                try {
                    parse2 = sdf.parse(crawl_time2);
                } catch (ParseException e) {
                    return v1;
                }
                if (parse1.before(parse2))
                    return v2;
                else
                    return v1;
            }
        }).values();

//        String out_citys_exclude = "上海,临沧,丽江,保山,大理,德宏,怒江,文山,昆明,昭通,思茅,普洱,曲靖,楚雄,玉溪,红河,西双版纳,迪庆,乌兰察布,乌海,兴安盟,包头,呼伦贝尔,呼和浩特,巴彦淖尔,赤峰,通辽,鄂尔多斯,锡林郭勒盟,阿拉善盟,北京,台中,台北,台南,嘉义,基隆,新竹,高雄,吉林,四平,延边,松原,白城,白山,辽源,通化,长春,乐山,内江,凉山,南充,宜宾,巴中,广元,广安,德阳,成都,攀枝花,泸州,甘孜,眉山,绵阳,自贡,资阳,达州,遂宁,阿坝,雅安,天津,中卫,吴忠,固原,石嘴山,银川,亳州,六安,合肥,安庆,宣城,宿州,巢湖,池州,淮北,淮南,滁州,芜湖,蚌埠,铜陵,阜阳,马鞍山,黄山,东营,临沂,威海,德州,日照,枣庄,泰安,济南,济宁,淄博,滨州,潍坊,烟台,聊城,莱芜,菏泽,青岛,临汾,吕梁,大同,太原,忻州,晋中,晋城,朔州,运城,长治,阳泉,中山,东莞,云浮,佛山,广州,惠州,揭阳,梅州,汕头,汕尾,江门,河源,深圳,清远,湛江,潮州,珠海,肇庆,茂名,阳江,韶关,北海,南宁,崇左,来宾,柳州,桂林,梧州,河池,玉林,百色,贵港,贺州,钦州,防城港,乌鲁木齐,伊犁,克孜勒苏,克拉玛依,博尔塔拉,吐鲁番,和田,哈密,喀什,塔城,巴音郭楞,昌吉,阿克苏,阿勒泰,南京,南通,宿迁,常州,徐州,扬州,无锡,泰州,淮阴,淮安,盐城,苏州,连云港,镇江,上饶,九江,南昌,吉安,宜春,抚州,新余,景德镇,萍乡,赣州,鹰潭,保定,唐山,廊坊,张家口,承德,沧州,石家庄,秦皇岛,衡水,邢台,邯郸,三门峡,信阳,南阳,周口,商丘,安阳,平顶山,开封,新乡,洛阳,漯河,濮阳,焦作,许昌,郑州,驻马店,鹤壁,丽水,台州,嘉兴,宁波,杭州,温州,湖州,绍兴,舟山,衢州,金华,三亚,三沙,海口,十堰,咸宁,孝感,宜昌,恩施,武汉,荆州,荆门,襄樊,襄阳,鄂州,随州,黄冈,黄石,娄底,岳阳,常德,张家界,怀化,株洲,永州,湘潭,湘西,益阳,衡阳,邵阳,郴州,长沙,澳门,临夏,兰州,嘉峪关,天水,定西,平凉,庆阳,张掖,武威,甘南,白银,酒泉,金昌,陇南,三明,南平,厦门,宁德,泉州,漳州,福州,莆田,龙岩,山南,拉萨,日喀则,昌都,林芝,那曲,阿里,六盘水,安顺,毕节,贵阳,遵义,铜仁,黔东南,黔南,黔西南,丹东,大连,抚顺,朝阳,本溪,沈阳,盘锦,营口,葫芦岛,辽阳,铁岭,锦州,阜新,鞍山,重庆,咸阳,商洛,安康,宝鸡,延安,榆林,汉中,渭南,西安,铜川,海东,海北,海南,海西,玉树,西宁,黄南,九龙,新界,香港,七台,伊春,佳木斯,双鸭山,哈尔滨,大兴安岭,大庆,牡丹江,绥化,鸡西,鹤岗,黑河,齐齐哈尔,台湾";
        // 识别 城市， 不在范围为unknown
        JavaPairRDD<Document, PositionType> postion = mongoRDD.flatMapToPair(new PairFlatMapFunction<Document, Document, PositionType>() {
            private final HashSet<String> citys = KeyValueUtils.getCitysprovince();
            SpecifyCityRecognize specifyCityRecognize = new SpecifyCityRecognize("中国");

            @Override
            public Iterator<Tuple2<Document, PositionType>> call(Document document) throws Exception {
                ArrayList<Tuple2<Document, PositionType>> out = new ArrayList<Tuple2<Document, PositionType>>();
                String city = document.getString("work_city");
                String province = document.getString("work_province");
                String address = document.getString("work_address");
                String country = document.getString("work_country");
                boolean flag = false;
                if (StringUtils.isNotEmpty(city))
                    flag = addCity(city, out, document);
                if (!flag && StringUtils.isNotEmpty(address)) {
//                    if (address.length() > 8)
//                        flag = addCity(address.substring(0, 8), out, document); // 减小地址名称对分类的影响，故而截取一部分后面的详细地址
//                    else
                    flag = addCity(address, out, document); // 减小地址名称对分类的影响，故而截取一部分后面的详细地址
                }
                if (!flag && StringUtils.isNotEmpty(province))
                    flag = addCity(province, out, document);
                if (!flag && StringUtils.isNotEmpty(country))
                    flag = addCity(country, out, document);
                if (!flag) {
                    PositionType positionType = new PositionType();
                    positionType.setCity("unknown");
                    out.add(new Tuple2<>(document, positionType));
                }

                return out.iterator();
            }

            public boolean addCity(String real_city, ArrayList<Tuple2<Document, PositionType>> out, Document document) {
                int i = 0;
                for (Tuple4<String, String, String, String> c : specifyCityRecognize.locationRecognize(real_city)) {
                    PositionType positionType = new PositionType();
                    positionType.setCity(c._2());
                    out.add(new Tuple2<>(document, positionType));
                    i++;
                }
                if (i == 0) {
                    return false;
                } else
                    return true;
            }
        });

        // 识别 公司性质
        List<String> strings = Arrays.asList("互联网/电子商务,贸易/进出口",
                "仪器仪表/工业自动化",
                "制药/生物工程",
                "银行",
                "航天/航空,机械/设备/重工",
                "多元化业务集团公司",
                "物业管理/商业中心",
                "保险",
                "电气/电力/水利,新能源",
                "新能源",
                "计算机软件",
                "教育/培训/院校",
                "医疗设备/器械",
                "电子技术/半导体/集成电路,计算机服务(系统、数据服务、维修)",
                "10000人以上",
                "新能源,石油/化工/矿产/地质",
                "unknown",
                "计算机服务(系统、数据服务、维修)",
                "5000-10000人",
                "汽车及零配件,多元化业务集团公司",
                "金融/投资/证券,银行",
                "快速消费品(食品、饮料、化妆品)",
                "通信/电信/网络设备",
                "酒店/旅游",
                "农/林/牧/渔",
                "家具/家电/玩具/礼品",
                "服装/纺织/皮革",
                "印刷/包装/造纸",
                "检测，认证",
                "保险,金融/投资/证券",
                "交通/运输/物流",
                "计算机服务(系统、数据服务、维修),电子技术/半导体/集成电路",
                "汽车及零配件",
                "医疗/护理/卫生",
                "建筑/建材/工程",
                "金融/投资/证券",
                "办公用品及设备",
                "机械/设备/重工,电子技术/半导体/集成电路",
                "机械/设备/重工",
                "批发/零售",
                "500-1000人",
                "原材料和加工",
                "专业服务(咨询、人力资源、财会)",
                "餐饮业",
                "汽车及零配件,交通/运输/物流",
                "贸易/进出口,服装/纺织/皮革",
                "电子技术/半导体/集成电路",
                "贸易/进出口",
                "互联网/电子商务",
                "房地产",
                "1000-5000人",
                "少于50人",
                "50-150人",
                "150-500人",
                "石油/化工/矿产/地质,机械/设备/重工"
        );

        postion = postion.mapToPair(new PairFunction<Tuple2<Document, PositionType>, Document, PositionType>() {
            List<String> list = strings;

            @Override
            public Tuple2<Document, PositionType> call(Tuple2<Document, PositionType> tuple2) throws Exception {
                String company_property = tuple2._1().getString("company_property");
                if (StringUtils.isNotEmpty(company_property)) {
                    String trim = KeyValueUtils.trim(company_property);
                    if (list.contains(trim)) {
                        tuple2._2().setError(tuple2._1().getObjectId("_id").toString());
                        return tuple2;
                    }
                    tuple2._2().setCompany_property(trim);
                } else {
                    tuple2._2().setCompany_property("unknown");
                }
                return tuple2;
            }
        }).filter(new Function<Tuple2<Document, PositionType>, Boolean>() {
            @Override
            public Boolean call(Tuple2<Document, PositionType> v1) throws Exception {
                String error = v1._2().getError();
                if (StringUtils.isEmpty(error))
                    return true;
                return false;
            }
        });

        // 识别 work_type 职位类别
        postion = postion.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, PositionType>, Document, PositionType>() {
            @Override
            public Iterator<Tuple2<Document, PositionType>> call(Tuple2<Document, PositionType> tuple2) throws Exception {
                ArrayList<Tuple2<Document, PositionType>> out = new ArrayList<Tuple2<Document, PositionType>>();
                ArrayList<Document> work_type_mapping = (ArrayList<Document>) tuple2._1().get("work_type_mapping");
                if (work_type_mapping != null && work_type_mapping.size() > 0) {
                    for (Document doc : work_type_mapping) {
                        String type = doc.getString("type");
                        if (StringUtils.isNotEmpty(type)) {
                            PositionType clone = tuple2._2().clone();
                            clone.setWork_type(type);
                            out.add(new Tuple2<>(tuple2._1(), clone));
                        }
                    }
                } else {
                    tuple2._2().setWork_type("error");
                    out.add(tuple2);
                }
                return out.iterator();
            }
        });

// 识别公司类别
        postion = postion.mapToPair(new PairFunction<Tuple2<Document, PositionType>, Document, PositionType>() {
            @Override
            public Tuple2<Document, PositionType> call(Tuple2<Document, PositionType> tuple2) throws Exception {
                String company_type = tuple2._1().getString("company_type");
                if (StringUtils.isNotEmpty(company_type)) {
                    tuple2._2().setCompany_type(KeyValueUtils.trim(company_type));
                } else {
                    tuple2._2().setCompany_type("unknown");
                }
                return tuple2;
            }
        });

        // 识别公司 规模
        postion = postion.mapToPair(new PairFunction<Tuple2<Document, PositionType>, Document, PositionType>() {
            @Override
            public Tuple2<Document, PositionType> call(Tuple2<Document, PositionType> tuple2) throws Exception {
                String company_size_max = tuple2._1().getString("company_size_max");
                String company_size_min = tuple2._1().getString("company_size_min");
                String company_size_str = tuple2._1().getString("company_size_str");
//                if (StringUtils.isNotEmpty(company_size_max) && StringUtils.isNotEmpty(company_size_min)) {
//                    if (company_size_max.equals("100000000")) {
//                        tuple2._2().setCompany_size(company_size_min);
//                    } else if (company_size_min.equals("-1")) {
//                        tuple2._2().setCompany_size(company_size_max);
//                    } else if (company_size_min.equals(company_size_max)) {
//                        tuple2._2().setCompany_size(company_size_min);
//                    } else {
//                        int i = Integer.parseInt(company_size_min) + Integer.parseInt(company_size_max) / 2;
//                        tuple2._2().setCompany_size("" + i);
//                    }
//                } else {
//                    tuple2._2().setCompany_size("unknown");
//                }
                if (StringUtils.isNotEmpty(company_size_str)) {
                    tuple2._2().setCompany_size(company_size_str);
                } else {
                    tuple2._2().setCompany_size("unknown");
                }
                return tuple2;
            }
        });

        // 识别最低学历
        postion = postion.mapToPair(new PairFunction<Tuple2<Document, PositionType>, Document, PositionType>() {
            @Override
            public Tuple2<Document, PositionType> call(Tuple2<Document, PositionType> tuple2) throws Exception {
                String min_education = tuple2._1().getString("min_education");
                if (StringUtils.isNotEmpty(min_education)) {
                    tuple2._2().setMin_education(min_education);
                } else {
                    tuple2._2().setMin_education("unknown");
                }
                return tuple2;
            }
        });

        // 识别 招聘人数
        postion = postion.mapToPair(new PairFunction<Tuple2<Document, PositionType>, Document, PositionType>() {
            @Override
            public Tuple2<Document, PositionType> call(Tuple2<Document, PositionType> tuple2) throws Exception {
                String work_hiring = tuple2._1().getString("work_hiring");
                if (StringUtils.isNotEmpty(work_hiring)) {
                    tuple2._2().setHire_count(work_hiring);
                } else {
                    tuple2._2().setHire_count("error");
                }
                return tuple2;
            }
        });

        // 识别 工作经验要求
        postion = postion.mapToPair(new PairFunction<Tuple2<Document, PositionType>, Document, PositionType>() {
            @Override
            public Tuple2<Document, PositionType> call(Tuple2<Document, PositionType> tuple2) throws Exception {
                String work_experience_min = tuple2._1().getString("work_experience_min");
                String work_experience_max = tuple2._1().getString("work_experience_max");
                String work_experience_str = tuple2._1().getString("work_experience_str");
//                if (StringUtils.isNotEmpty(work_experience_min) && StringUtils.isNotEmpty(work_experience_max)) {
//                    if (work_experience_min.equals("-1") && work_experience_max.equals("999")) {
//                        tuple2._2().setExperience("不限");
//                    } else if (work_hiring.equals("若干")) {
//                        tuple2._2().setHire_count("3");
//                    } else
//                        tuple2._2().setHire_count(work_hiring);
//                } else {
//                    tuple2._2().setExperience("unknown");
//                }
                if (StringUtils.isNotEmpty(work_experience_str)) {
                    tuple2._2().setExperience(work_experience_str);
                } else {
                    tuple2._2().setExperience("unknown");
                }
                return tuple2;
            }
        });

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

        // 识别 职位名称
        postion = postion.mapToPair(new PairFunction<Tuple2<Document, PositionType>, Document, PositionType>() {
            @Override
            public Tuple2<Document, PositionType> call(Tuple2<Document, PositionType> tuple2) throws Exception {
                String position_name = tuple2._1().getString("work_name");
                List<PositionFunc> positionFuncs = broadcast.getValue();
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
                    tuple2._2().setFunc("unknown");
                    tuple2._2().setSecond_level("unknown");
                    tuple2._2().setPosition("unknown");
                }
                return tuple2;
            }
        });

        // 识别 职位职位行业
        postion = postion.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, PositionType>, Document, PositionType>() {
            @Override
            public Iterator<Tuple2<Document, PositionType>> call(Tuple2<Document, PositionType> tuple2) throws Exception {
                ArrayList<Tuple2<Document, PositionType>> out = new ArrayList<Tuple2<Document, PositionType>>();
                String company_name = tuple2._1().getString("company_name");
                for (CompanyKey companyKey : companyList) {
                    if (companyKey.getKeyword().length() <= 3) {
                        if (company_name.contains(companyKey.getKeyword())) {
                            boolean flag = false;
                            for (String key : companyKey.getKeys()) {
                                if (company_name.contains(key)) {
                                    flag = true;
                                    break;
                                }
                            }
                            if (flag) {
                                for (String industry : companyKey.getIndustry()) {
                                    PositionType clone = tuple2._2().clone();
                                    clone.setIndustry(industry);
                                    out.add(new Tuple2<>(tuple2._1(), clone));
                                }
                            }
                        }
                    } else {
                        if (company_name.contains(companyKey.getKeyword())) {
                            for (String industry : companyKey.getIndustry()) {
                                PositionType clone = tuple2._2().clone();
                                clone.setIndustry(industry);
                                out.add(new Tuple2<>(tuple2._1(), clone));
                            }
                        }
                    }
                }
                if (out.size() == 0)
                    out.add(tuple2);
                return out.iterator();
            }
        });

        JavaPairRDD<String, Integer> tempRdd = postion.mapToPair(new PairFunction<Tuple2<Document, PositionType>, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(Tuple2<Document, PositionType> tuple2) throws Exception {
                PositionType positionType = tuple2._2();
                StringBuilder sb = new StringBuilder();
                sb.append(positionType.getCity());
                sb.append("\t");
                sb.append(positionType.getFunc());
                sb.append("\t");
                sb.append(positionType.getSecond_level());
                sb.append("\t");
                sb.append(positionType.getPosition());
                sb.append("\t");
                sb.append(positionType.getIndustry());
                sb.append("\t");
                Integer hire_count = 0;
                try {
                    hire_count = Integer.parseInt(positionType.getHire_count());
                } catch (NumberFormatException e) {

                }
                return new Tuple2<String, Integer>(sb.toString(), hire_count);
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });

        JavaRDD<Document> out = tempRdd.map(new Function<Tuple2<String, Integer>, Document>() {
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
                out.put("type", "city_position_count_demand");
                return out;
            }
        });

        Map<String, String> writeOverrides = new HashMap<String, String>();
        writeOverrides.put("database", database);
        writeOverrides.put("collection", out_collectionName + "_" + sdf.format(start_time) + "_" + sdf.format(end_time));
        writeOverrides.put("writeConcern.wTimeoutMS", "" + (1000 * 60 * 60 * 2));
        WriteConfig wc = WriteConfig.create(conf, writeOverrides);
        MongoSpark.save(out, wc);
        jsc.stop();
//        jsc.close();
    }

//    public static void main(String[] args) throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date start_time = null, end_time = null;
//        if (args.length >= 3) {
//            start_time = sdf.parse(args[1]);
//            end_time = sdf.parse(args[2]);
//        } else {
//            start_time = new Date(114, 3, 1);
//            end_time = new Date(118, 3, 31);
//        }
//        excute(args, start_time, end_time);
//    }
}
