package com.shulianxunying.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import scala.Serializable;
import scala.Tuple2;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SuChang on 2017/3/14 15:39.
 */
public class KeyValueUtils implements Serializable {

    public static void getStringValue(Document document, String key, Set<Tuple2<String, Integer>> out) {
        String string = document.getString(key);
        if (StringUtils.isNotEmpty(string))
            out.add(new Tuple2<String, Integer>(string, 1));
    }

    public static String dealSalary(String originalSalary) {
        String regEx = "[^0-9万千]";// 正则
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(originalSalary);
        String nums = m.replaceAll(" ").trim();
        if (StringUtils.isEmpty(nums)) {
            return "unknown";
        }
        nums = nums.replaceAll("  ", "");
        String[] split = nums.split(" ");
        int length = split.length;
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains("万")) {
                split[i] = split[i].replaceAll("万", "0000");
            }
            if (split[i].toLowerCase().contains("w")) {
                split[i] = split[i].replaceAll("[Ww]", "0000");
            }
            if (split[i].contains("千")) {
                split[i] = split[i].replaceAll("千", "000");
            }
            if (split[i].toLowerCase().contains("k")) {
                split[i] = split[i].replaceAll("[kK]]", "000");
            }
        }

        if (length == 1) {
            return String.valueOf(split[0]);
        } else {
            return split[0] + "-" + split[1];
        }
    }

    public static String trim(String str) {
        String replace = str.replace(" ", "").replace(" ", "");
        return replace.trim();
    }

    public static String encodeMD5Hex(String data) {
        return DigestUtils.md5Hex(data);
    }

    public static HashSet<String> citys = new HashSet<>();
    public static HashSet<String> province = new HashSet<>();
    public static HashSet<String> citysprovince = new HashSet<>();

    public static HashSet<String> getCitys() {
        return (HashSet<String>) citys.clone();
    }

    public static HashSet<String> getProvince() {
        return (HashSet<String>) province.clone();
    }

    public static HashSet<String> getCitysprovince() {
        return (HashSet<String>) citysprovince.clone();
    }

    static {
        String[] c = "上海,临沧,丽江,保山,大理,德宏,怒江,文山,昆明,昭通,思茅,普洱,曲靖,楚雄,玉溪,红河,西双版纳,迪庆,乌兰察布,乌海,兴安盟,包头,呼伦贝尔,呼和浩特,巴彦淖尔,赤峰,通辽,鄂尔多斯,锡林郭勒,阿拉善,北京,台中,台北,台南,嘉义,基隆,新竹,高雄,吉林,四平,延边,松原,白城,白山,辽源,通化,长春,乐山,内江,凉山,南充,宜宾,巴中,广元,广安,德阳,成都,攀枝花,泸州,甘孜,眉山,绵阳,自贡,资阳,达州,遂宁,阿坝,雅安,天津,中卫,吴忠,固原,石嘴山,银川,亳州,六安,合肥,安庆,宣城,宿州,巢湖,池州,淮北,淮南,滁州,芜湖,蚌埠,铜陵,阜阳,马鞍山,黄山,东营,临沂,威海,德州,日照,枣庄,泰安,济南,济宁,淄博,滨州,潍坊,烟台,聊城,莱芜,菏泽,青岛,临汾,吕梁,大同,太原,忻州,晋中,晋城,朔州,运城,长治,阳泉,中山,东莞,云浮,佛山,广州,惠州,揭阳,梅州,汕头,汕尾,江门,河源,深圳,清远,湛江,潮州,珠海,肇庆,茂名,阳江,韶关,北海,南宁,崇左,来宾,柳州,桂林,梧州,河池,玉林,百色,贵港,贺州,钦州,防城港,乌鲁木齐,伊犁,克孜勒苏柯尔克孜,克拉玛依,博尔塔拉,吐鲁番,和田,哈密,喀什,塔城,巴音郭楞,昌吉,阿克苏,阿勒泰,南京,南通,宿迁,常州,徐州,扬州,无锡,泰州,淮阴,淮安,盐城,苏州,连云港,镇江,上饶,九江,南昌,吉安,宜春,抚州,新余,景德镇,萍乡,赣州,鹰潭,保定,唐山,廊坊,张家口,承德,沧州,石家庄,秦皇岛,衡水,邢台,邯郸,三门峡,信阳,南阳,周口,商丘,安阳,平顶山,开封,新乡,洛阳,漯河,濮阳,焦作,许昌,郑州,驻马店,鹤壁,丽水,台州,嘉兴,宁波,杭州,温州,湖州,绍兴,舟山,衢州,金华,三亚,三沙,海口,十堰,咸宁,孝感,宜昌,恩施,武汉,荆州,荆门,襄樊,襄阳,鄂州,随州,黄冈,黄石,娄底,岳阳,常德,张家界,怀化,株洲,永州,湘潭,湘西,益阳,衡阳,邵阳,郴州,长沙,澳门,临夏,兰州,嘉峪关,天水,定西,平凉,庆阳,张掖,武威,甘南,白银,酒泉,金昌,陇南,三明,南平,厦门,宁德,泉州,漳州,福州,莆田,龙岩,山南,拉萨,日喀则,昌都,林芝,那曲,阿里,六盘水,安顺,毕节,贵阳,遵义,铜仁,黔东南,黔南,黔西南,丹东,大连,抚顺,朝阳,本溪,沈阳,盘锦,营口,葫芦岛,辽阳,铁岭,锦州,阜新,鞍山,重庆,咸阳,商洛,安康,宝鸡,延安,榆林,汉中,渭南,西安,铜川,果洛,海东,海北,海西,玉树,西宁,黄南,九龙,新界,香港,七台,伊春,佳木斯,双鸭山,哈尔滨,大兴安岭,大庆,牡丹江,绥化,鸡西,鹤岗,黑河,齐齐哈尔".split(",");
        for (String cc : c) {
            citys.add(cc);
        }
        String[] p = "四川,宁夏,安徽,山东,山西,广东,广西,新疆维吾尔,江苏,江西,河北,河南,浙江,海南,湖北,湖南,澳门,甘肃,福建,西藏,贵州,辽宁,陕西,青海,黑龙江".split(",");
        for (String cc : p) {
            province.add(cc);
        }
        String[] cp = "延吉,燕郊,泰兴,邓州,靖江,海宁,丹阳,义乌,以色列,韩国,日本,冰岛,马来西亚,南非,德国,泰国,巴西,国外,澳大利亚,匈牙利,西班牙,葡萄牙,安哥拉,阿联酋,捷克,阿根廷,印度,塞浦路斯,沙特阿拉伯,美国,埃及,巴基斯坦,法国,白俄罗斯,阿尔及利亚,北京,上海,广东,广州,韶关,深圳,珠海,汕头,佛山,江门,湛江,茂名,肇庆,惠州,梅州,汕尾,河源,阳江,清远,东莞,中山,潮州,揭阳,云浮,天津,湖北,武汉,黄石,十堰,宜昌,襄阳,鄂州,荆门,孝感,荆州,黄冈,咸宁,随州,恩施,公安,武穴,天门,仙桃,潜江,宜城,神农架,陕西,西安,铜川,宝鸡,咸阳,渭南,延安,汉中,榆林,安康,商洛,兴平,杨凌,西咸新区,四川,成都,自贡,攀枝花,泸州,德阳,绵阳,广元,遂宁,内江,乐山,南充,眉山,宜宾,广安,达州,雅安,巴中,资阳,阿坝,甘孜,凉山,峨眉,西昌,简阳,辽宁,大连,沈阳,鞍山,抚顺,本溪,丹东,锦州,营口,阜新,辽阳,盘锦,铁岭,朝阳,葫芦岛,兴城,海城,昌图,开原,东港,吉林,长春,珲春,吉林市,四平,辽源,通化,白山,松原,白城,延边,公主岭,江苏,南京,苏州,昆山,常熟,张家港,无锡,江阴,徐州,常州,南通,连云港,淮安,盐城,扬州,镇江,泰州,宿迁,太仓,宜兴,山东,济南,青岛,淄博,枣庄,东营,烟台,潍坊,济宁,泰安,威海,日照,莱芜,临沂,德州,聊城,滨州,菏泽,浙江,杭州,宁波,温州,嘉兴,湖州,绍兴,金华,衢州,舟山,台州,丽水,方家山,广西,南宁,柳州,桂林,梧州,北海,防城港,钦州,贵港,玉林,百色,贺州,河池,来宾,崇左,安徽,合肥,芜湖,蚌埠,淮南,马鞍山,淮北,铜陵,安庆,黄山,滁州,阜阳,宿州,六安,亳州,池州,宣城,凤阳,广德,宿松,河北,石家庄,唐山,秦皇岛,邯郸,邢台,保定,张家口,承德,沧州,廊坊,衡水,遵化,山西,太原,大同,阳泉,长治,晋城,朔州,晋中,运城,忻州,临汾,吕梁,永济市,内蒙古,呼和浩特,包头,乌海,赤峰,通辽,鄂尔多斯,呼伦贝尔,兴安盟,锡林郭勒盟,乌兰察布,巴彦淖尔,阿拉善盟,乌审旗,满洲里,黑龙江,哈尔滨,齐齐哈尔,鸡西,鹤岗,双鸭山,大庆,伊春,佳木斯,七台河,牡丹江,黑河,绥化,大兴安岭,安达,双城,尚志,绥芬河,肇东市,福建,福州,厦门,莆田,三明,泉州,漳州,南平,龙岩,宁德,江西,南昌,景德镇,萍乡,九江,新余,鹰潭,赣州,吉安,宜春,抚州,上饶,河南,郑州,开封,洛阳,平顶山,安阳,鹤壁,新乡,焦作,濮阳,许昌,漯河,三门峡,南阳,商丘,信阳,周口,驻马店,济源,西平,湖南,长沙,株洲,湘潭,衡阳,邵阳,岳阳,常德,张家界,益阳,郴州,永州,怀化,娄底,湘西,海南,海口,三亚,洋浦,琼海,儋州,五指山,文昌,万宁,东方,定安,屯昌,澄迈,临高,琼中,保亭,白沙,昌江,乐东,陵水,重庆,贵州,贵阳,六盘水,遵义,安顺,铜仁,黔西南,毕节,黔东南,黔南,云南,昆明,曲靖,玉溪,保山,昭通,楚雄,红河,文山,西双版纳,大理,德宏,丽江,怒江,迪庆,临沧,普洱,西藏,拉萨,昌都,山南,日喀则,那曲,阿里,林芝,甘肃,兰州,嘉峪关,金昌,白银,天水,武威,张掖,平凉,酒泉,庆阳,定西,陇南,临夏,甘南,青海,西宁,海东,海北,黄南,海南州,果洛,玉树,海西,宁夏,银川,石嘴山,吴忠,固原,中卫,新疆,乌鲁木齐,克拉玛依,吐鲁番,哈密,昌吉,博尔塔拉,巴音郭楞,阿克苏,克孜勒苏,喀什,和田,伊犁,塔城,阿勒泰,石河子,奎屯市,乌苏,阿拉尔,图木舒克,五家渠,北屯市,香港,澳门,台湾省,比利时,越南,丹麦,尼日利亚,加纳,波兰,加拿大,荷兰,柬埔寨,保加利亚,瑞典,英国,希腊,爱尔兰,塞内加尔,俄罗斯联邦,科威特,印度尼西亚,坦桑尼亚,新加坡,瑞士,意大利,乌克兰,芬兰,奥地利,乌干达,挪威,新西兰,土耳其".split(",");
        for (String cc : cp) {
            citysprovince.add(cc);
        }
    }
}
