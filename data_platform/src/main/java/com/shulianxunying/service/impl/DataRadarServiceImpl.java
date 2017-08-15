package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.MDataDao;
import com.shulianxunying.dao.impldao.MRadarBaseDao;
import com.shulianxunying.dao.impldao.MRadarDataDao;
import com.shulianxunying.entity.RadarData;
import com.shulianxunying.entity.impl.ApiRadarData;
import com.shulianxunying.service.IDataRadarService;
import com.shulianxunying.util.CommonUtil;
import org.bson.Document;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhang on 2017/6/12.
 */
@Service("radar")
public class DataRadarServiceImpl implements IDataRadarService {

    @Resource
    MRadarDataDao radarDataDao;
    @Resource
    MRadarBaseDao radarBaseDao;

    // 国家映射
    static HashMap<String, String> country_map = new HashMap<>();
    static {
        country_map.put("所罗门群岛", "所罗门群岛");
        country_map.put("巴布亚新几内亚", "巴布亚新几内亚");
        country_map.put("瑞士", "瑞士");
        country_map.put("塞浦路斯", "塞浦路斯");
        country_map.put("刚果", "刚果共和国");
        country_map.put("玻利维亚", "玻利维亚");
        country_map.put("捷克共和国", "捷克共和国");
        country_map.put("多米尼加", "多米尼加");
        country_map.put("贝宁", "贝宁");
        country_map.put("老挝", "老挝");
        country_map.put("西班牙", "西班牙");
        country_map.put("南非", "南非");
        country_map.put("牙买加", "牙买加");
        country_map.put("摩尔多瓦", "摩尔多瓦");
        country_map.put("加纳", "加纳");
        country_map.put("圣多美和普林西比", "圣多美和普林西比");
        country_map.put("北塞浦路斯", "北塞浦路斯");
        country_map.put("埃及", "埃及");
        country_map.put("圭亚那", "圭亚那");
        country_map.put("冈比亚", "冈比亚");
        country_map.put("越南", "越南");
        country_map.put("马提尼克", "马提尼克");
        country_map.put("挪威", "挪威");
        country_map.put("英属维尔京群岛", "英属维尔京群岛");
        country_map.put("科特迪瓦", "科特迪瓦");
        country_map.put("佛得角", "佛得角");
        country_map.put("坦桑尼亚", "坦桑尼亚联合共和国");
        country_map.put("斯洛伐克", "斯洛伐克");
        country_map.put("亚美尼亚", "亚美尼亚");
        country_map.put("阿尔巴尼亚", "阿尔巴尼亚");
        country_map.put("尼日利亚", "尼日利亚");
        country_map.put("比利时", "比利时");
        country_map.put("尼加拉瓜", "尼加拉瓜");
        country_map.put("海地", "海地");
        country_map.put("马来西亚", "马来西亚");
        country_map.put("伊拉克", "伊拉克");
        country_map.put("葡萄牙", "葡萄牙");
        country_map.put("多米尼加共和国", "多米尼加共和国");
        country_map.put("津巴布韦", "津巴布韦");
        country_map.put("法罗群岛", "法罗群岛");
        country_map.put("塔吉克斯坦", "塔吉克斯坦");
        country_map.put("瓦利斯和福图纳", "瓦利斯和福图纳");
        country_map.put("乌兹别克斯坦", "乌兹别克斯坦");
        country_map.put("土库曼斯坦", "土库曼斯坦");
        country_map.put("马里", "马里");
        country_map.put("伊朗", "伊朗");
        country_map.put("吉尔吉斯斯坦", "吉尔吉斯斯坦");
        country_map.put("新加坡", "新加坡");
        country_map.put("赫德和麦克唐纳群岛", "赫德和麦克唐纳群岛");
        country_map.put("北马里亚纳群岛", "北马里亚纳群岛");
        country_map.put("瑞典", "瑞典");
        country_map.put("开曼群岛", "开曼群岛");
        country_map.put("白俄罗斯", "白俄罗斯");
        country_map.put("多哥", "多哥");
        country_map.put("法国", "法国");
        country_map.put("罗马尼亚", "罗马尼亚");
        country_map.put("圣卢西亚", "圣卢西亚");
        country_map.put("帕劳群岛", "帕劳群岛");
        country_map.put("中非共和国", "中非共和国");
        country_map.put("俄罗斯", "俄罗斯");
        country_map.put("布隆迪", "布隆迪");
        country_map.put("赞比亚", "赞比亚");
        country_map.put("加蓬", "加蓬");
        country_map.put("利比亚", "利比亚");
        country_map.put("巴勒斯坦", "巴勒斯坦");
        country_map.put("科威特", "科威特");
        country_map.put("弗兰克群岛", "弗兰克群岛");
        country_map.put("卢旺达", "卢旺达");
        country_map.put("巴拿马", "巴拿马");
        country_map.put("梵蒂冈", "梵蒂冈");
        country_map.put("秘鲁", "秘鲁");
        country_map.put("科科斯群岛", "科科斯群岛");
        country_map.put("乌干达", "乌干达");
        country_map.put("几内亚", "几内亚");
        country_map.put("立陶宛", "立陶宛");
        country_map.put("特克斯和凯克特斯群岛", "特克斯和凯克特斯群岛");
        country_map.put("斯威士兰", "斯威士兰");
        country_map.put("法属波利尼西亚", "法属波利尼西亚");
        country_map.put("格恩西岛", "格恩西岛");
        country_map.put("尼泊尔", "尼泊尔");
        country_map.put("吉布提", "吉布提");
        country_map.put("几内亚比绍", "几内亚比绍");
        country_map.put("智利", "智利");
        country_map.put("泰国", "泰国");
        country_map.put("瑙鲁", "瑙鲁");
        country_map.put("厄瓜多尔", "厄瓜多尔");
        country_map.put("卡塔尔", "卡塔尔");
        country_map.put("荷兰", "荷兰");
        country_map.put("萨摩亚", "萨摩亚");
        country_map.put("阿拉伯联合酋长国", "阿拉伯联合酋长国");
        country_map.put("伯利兹", "伯利兹");
        country_map.put("墨西哥", "墨西哥");
        country_map.put("奥兰群岛", "奥兰群岛");
        country_map.put("朝鲜", "北朝鲜");
        country_map.put("托克劳", "托克劳");
        country_map.put("缅甸", "缅甸");
        country_map.put("柬埔寨", "柬埔寨");
        country_map.put("博茨瓦纳", "博茨瓦纳");
        country_map.put("阿曼", "阿曼");
        country_map.put("布韦岛", "布韦岛");
        country_map.put("瓦努阿图", "瓦努阿图");
        country_map.put("美属萨摩亚", "美属萨摩亚");
        country_map.put("马耳他", "马耳他");
        country_map.put("圣基茨和尼维斯", "圣基茨和尼维斯");
        country_map.put("巴西", "巴西");
        country_map.put("阿富汗", "阿富汗");
        country_map.put("日本", "日本");
        country_map.put("洪都拉斯", "洪都拉斯");
        country_map.put("曼岛", "曼岛");
        country_map.put("荷属安地列斯", "荷属安地列斯");
        country_map.put("巴基斯坦", "巴基斯坦");
        country_map.put("关岛", "关岛");
        country_map.put("爱尔兰", "爱尔兰");
        country_map.put("萨尔瓦多", "萨尔瓦多");
        country_map.put("爱沙尼亚", "爱沙尼亚");
        country_map.put("马拉维", "马拉维");
        country_map.put("巴巴多斯岛", "巴巴多斯岛");
        country_map.put("也门", "也门");
        country_map.put("孟加拉", "孟加拉国");
        country_map.put("印度", "印度");
        country_map.put("马绍尔群岛", "马绍尔群岛");
        country_map.put("毛里塔尼亚", "毛里塔尼亚");
        country_map.put("马尔代夫", "马尔代夫");
        country_map.put("安哥拉", "安哥拉");
        country_map.put("图瓦卢", "图瓦卢");
        country_map.put("匈牙利", "匈牙利");
        country_map.put("蒙特塞拉特", "蒙特塞拉特");
        country_map.put("格陵兰", "格陵兰");
        country_map.put("基里巴斯", "基里巴斯");
        country_map.put("尼日尔", "尼日尔");
        country_map.put("以色列", "以色列");
        country_map.put("拉脱维亚", "拉脱维亚");
        country_map.put("文莱", "文莱");
        country_map.put("巴林", "巴林");
        country_map.put("哈萨克斯坦", "哈萨克斯坦");
        country_map.put("阿鲁巴", "阿鲁巴");
        country_map.put("刚果民主共和国", "刚果民主共和国");
        country_map.put("土耳其", "土耳其");
        country_map.put("波兰", "波兰");
        country_map.put("安道尔", "安道尔");
        country_map.put("卢森堡", "卢森堡");
        country_map.put("百慕大", "百慕大");
        country_map.put("塞拉利昂", "塞拉利昂");
        country_map.put("利比里亚", "利比里亚");
        country_map.put("乔治亚", "乔治亚");
        country_map.put("法属南部领地", "法属南部领地");
        country_map.put("安圭拉", "安圭拉");
        country_map.put("法属圭亚那", "法属圭亚那");
        country_map.put("毛里求斯", "毛里求斯");
        country_map.put("斯洛文尼亚", "斯洛文尼亚");
        country_map.put("圣皮埃尔和米克隆群岛", "圣皮埃尔和米克隆群岛");
        country_map.put("美国", "美国");
        country_map.put("韩国", "韩国");
        country_map.put("古巴", "古巴");
        country_map.put("泽西岛", "泽西岛");
        country_map.put("希腊", "希腊");
        country_map.put("马约特岛", "马约特岛");
        country_map.put("蒙古", "蒙古");
        country_map.put("纳米比亚", "纳米比亚");
        country_map.put("南乔治亚和南桑德威奇群岛", "南乔治亚和南桑德威奇群岛");
        country_map.put("乍得", "乍得");
        country_map.put("约旦", "约旦");
        country_map.put("摩纳哥", "摩纳哥");
        country_map.put("冰岛", "冰岛");
        country_map.put("埃塞俄比亚", "埃塞俄比亚");
        country_map.put("英属印度洋领地", "英属印度洋领地");
        country_map.put("巴哈马", "巴哈马");
        country_map.put("意大利", "意大利");
        country_map.put("安提瓜岛和巴布达", "安提瓜岛和巴布达");
        country_map.put("菲律宾", "菲律宾");
        country_map.put("索马里", "索马里");
        country_map.put("印度尼西亚", "印度尼西亚");
        country_map.put("丹麦", "丹麦");
        country_map.put("圣诞岛", "圣诞岛");
        country_map.put("库克群岛", "库克群岛");
        country_map.put("肯尼亚", "肯尼亚");
        country_map.put("哥斯达黎加", "哥斯达黎加");
        country_map.put("克罗地亚", "克罗地亚");
        country_map.put("哥伦比亚", "哥伦比亚");
        country_map.put("美属维尔京群岛", "美属维尔京群岛");
        country_map.put("美属外岛", "美属外岛");
        country_map.put("留尼旺岛", "留尼旺岛");
        country_map.put("阿森松岛", "阿森松岛");
        country_map.put("塞尔维亚", "塞尔维亚共和国");
        country_map.put("皮特凯恩", "皮特凯恩");
        country_map.put("阿塞拜疆", "阿塞拜疆");
        country_map.put("格林纳达", "格林纳达");
        country_map.put("黑山", "黑山");
        country_map.put("摩洛哥", "摩洛哥");
        country_map.put("瓜德罗普", "瓜德罗普");
        country_map.put("德国", "德国");
        country_map.put("圣文森特和格林纳丁斯", "圣文森特和格林纳丁斯");
        country_map.put("苏丹", "苏丹");
        country_map.put("汤加", "汤加");
        country_map.put("列支敦士登", "列支敦士登");
        country_map.put("密克罗尼西亚", "密克罗尼西亚");
        country_map.put("澳大利亚", "澳大利亚");
        country_map.put("西撒哈拉", "西撒哈拉");
        country_map.put("斯里兰卡", "斯里兰卡");
        country_map.put("马其顿", "马其顿");
        country_map.put("新西兰", "新西兰");
        country_map.put("英国", "英国");
        country_map.put("叙利亚", "叙利亚");
        country_map.put("突尼斯", "突尼斯");
        country_map.put("新喀里多尼亚", "新喀里多尼亚");
        country_map.put("南极洲", "南极洲");
        country_map.put("阿根廷", "阿根廷");
        country_map.put("波斯尼亚和黑塞哥维那", "波斯尼亚和黑塞哥维那");
        country_map.put("阿尔及利亚", "阿尔及利亚");
        country_map.put("直布罗陀", "直布罗陀");
        country_map.put("黎巴嫩", "黎巴嫩");
        country_map.put("斐济", "斐济");
        country_map.put("莫桑比克", "莫桑比克");
        country_map.put("奥地利", "奥地利");
        country_map.put("厄立特里亚", "厄立特里亚");
        country_map.put("圣马力诺", "圣马力诺");
        country_map.put("圣赫勒拿", "圣赫勒拿");
        country_map.put("布基纳法索", "布基纳法索");
        country_map.put("特里斯坦达昆哈", "特里斯坦达昆哈");
        country_map.put("喀麦隆", "喀麦隆");
        country_map.put("诺福克", "诺福克");
        country_map.put("塞舌尔", "塞舌尔");
        country_map.put("莱索托", "莱索托");
        country_map.put("苏里南", "苏里南");
        country_map.put("危地马拉", "危地马拉");
        country_map.put("特立尼达和多巴哥", "特立尼达和多巴哥");
        country_map.put("芬兰", "芬兰");
        country_map.put("索马里兰", "索马里兰");
        country_map.put("科摩罗", "科摩罗");
        country_map.put("保加利亚", "保加利亚");
        country_map.put("纽埃", "纽埃");
        country_map.put("不丹", "不丹");
        country_map.put("塞内加尔", "塞内加尔");
        country_map.put("巴拉圭", "巴拉圭");
        country_map.put("加拿大", "加拿大");
        country_map.put("沙特阿拉伯", "沙特阿拉伯");
        country_map.put("东帝汶", "东帝汶");
        country_map.put("中国", "中国");
        country_map.put("斯瓦尔巴和扬马廷", "斯瓦尔巴和扬马廷");
        country_map.put("乌拉圭", "乌拉圭");
        country_map.put("波多黎各", "波多黎各");
        country_map.put("马达加斯加", "马达加斯加");
        country_map.put("委内瑞拉", "委内瑞拉");
        country_map.put("乌克兰", "乌克兰");
    }

    // 全国城市映射
    static HashMap<String, String> city_map = new HashMap<>();
    static {
        city_map.put("中卫", "中卫市");
        city_map.put("七台河", "七台河市");
        city_map.put("红河", "红河哈尼族彝族自治州");
        city_map.put("三明", "三明市");
        city_map.put("白城", "白城市");
        city_map.put("池州", "池州市");
        city_map.put("阿拉善", "阿拉善盟");
        city_map.put("莆田", "莆田市");
        city_map.put("大连", "大连市");
        city_map.put("莱芜", "莱芜市");
        city_map.put("和田", "和田地区");
        city_map.put("贵阳", "贵阳市");
        city_map.put("钦州", "钦州市");
        city_map.put("赣州", "赣州市");
        city_map.put("三亚", "三亚市");
        city_map.put("昭通", "昭通市");
        city_map.put("郑州", "郑州市");
        city_map.put("黑河", "黑河市");
        city_map.put("汉中", "汉中市");
        city_map.put("衡水", "衡水市");
        city_map.put("雅安", "雅安市");
        city_map.put("滨州", "滨州市");
        city_map.put("阳泉", "阳泉市");
        city_map.put("通辽", "通辽市");
        city_map.put("深圳", "深圳市");
        city_map.put("景德镇", "景德镇市");
        city_map.put("台州", "台州市");
        city_map.put("潮州", "潮州市");
        city_map.put("九江", "九江市");
        city_map.put("保亭", "保亭");
        city_map.put("塔城", "塔城地区");
        city_map.put("云浮", "云浮市");
        city_map.put("汕尾", "汕尾市");
        city_map.put("商洛", "商洛市");
        city_map.put("常州", "常州市");
        city_map.put("克拉玛依", "克拉玛依市");
        city_map.put("梧州", "梧州市");
        city_map.put("运城", "运城市");
        city_map.put("澄迈", "澄迈");
        city_map.put("西双版纳", "西双版纳傣族自治州");
        city_map.put("石嘴山", "石嘴山市");
        city_map.put("哈尔滨", "哈尔滨市");
        city_map.put("新乡", "新乡市");
        city_map.put("宁波", "宁波市");
        city_map.put("西安", "西安市");
        city_map.put("天水", "天水市");
        city_map.put("巴彦淖尔", "巴彦淖尔市");
        city_map.put("怒江", "怒江傈僳族自治州");
        city_map.put("盘锦", "盘锦市");
        city_map.put("香港", "香港");
        city_map.put("台湾", "台湾");
        city_map.put("海西蒙古族藏族自治州", "海西蒙古族藏族自治州");
        city_map.put("阿勒泰", "阿勒泰地区");
        city_map.put("金昌", "金昌市");
        city_map.put("来宾", "来宾市");
        city_map.put("新余", "新余市");
        city_map.put("平凉", "平凉市");
        city_map.put("林芝", "林芝地区");
        city_map.put("图木舒克", "图木舒克");
        city_map.put("驻马店", "驻马店市");
        city_map.put("固原", "固原市");
        city_map.put("石河子", "石河子市");
        city_map.put("河池", "河池市");
        city_map.put("齐齐哈尔", "齐齐哈尔市");
        city_map.put("广州", "广州市");
        city_map.put("苏州", "苏州市");
        city_map.put("仙桃", "仙桃市");
        city_map.put("克孜勒苏", "克孜勒苏");
        city_map.put("济南", "济南市");
        city_map.put("江门", "江门市");
        city_map.put("郴州", "郴州市");
        city_map.put("内江", "内江市");
        city_map.put("长春", "长春市");
        city_map.put("佛山", "佛山市");
        city_map.put("儋州", "儋州");
        city_map.put("舟山", "舟山市");
        city_map.put("文昌", "文昌");
        city_map.put("惠州", "惠州市");
        city_map.put("哈密", "哈密地区");
        city_map.put("商丘", "商丘市");
        city_map.put("益阳", "益阳市");
        city_map.put("大庆", "大庆市");
        city_map.put("昆明", "昆明市");
        city_map.put("洛阳", "洛阳市");
        city_map.put("朝阳", "朝阳市");
        city_map.put("佳木斯", "佳木斯市");
        city_map.put("清远", "清远市");
        city_map.put("铜仁", "铜仁地区");
        city_map.put("通化", "通化市");
        city_map.put("邵阳", "邵阳市");
        city_map.put("荆门", "荆门市");
        city_map.put("本溪", "本溪市");
        city_map.put("珠海", "珠海市");
        city_map.put("陇南", "陇南市");
        city_map.put("玉林", "玉林市");
        city_map.put("廊坊", "廊坊市");
        city_map.put("白沙", "白沙");
        city_map.put("宜宾", "宜宾市");
        city_map.put("亳州", "亳州市");
        city_map.put("无锡", "无锡市");
        city_map.put("资阳", "资阳市");
        city_map.put("泰州", "泰州市");
        city_map.put("抚州", "抚州市");
        city_map.put("海西", "海西蒙古族藏族自治州");
        city_map.put("海南", "海南藏族自治州");
        city_map.put("葫芦岛", "葫芦岛市");
        city_map.put("恩施", "恩施土家族苗族自治州");
        city_map.put("百色", "百色市");
        city_map.put("杭州", "杭州市");
        city_map.put("黄南", "黄南藏族自治州");
        city_map.put("酒泉", "酒泉市");
        city_map.put("临高", "临高");
        city_map.put("北京", "北京市");
        city_map.put("自贡", "自贡市");
        city_map.put("晋城", "晋城市");
        city_map.put("武汉", "武汉市");
        city_map.put("济源", "济源");
        city_map.put("大同", "大同市");
        city_map.put("广元", "广元市");
        city_map.put("肇庆", "肇庆市");
        city_map.put("抚顺", "抚顺市");
        city_map.put("吕梁", "吕梁市");
        city_map.put("宁德", "宁德市");
        city_map.put("五指山", "五指山市");
        city_map.put("秦皇岛", "秦皇岛市");
        city_map.put("沧州", "沧州市");
        city_map.put("南通", "南通市");
        city_map.put("芜湖", "芜湖市");
        city_map.put("青岛", "青岛市");
        city_map.put("中山", "中山市");
        city_map.put("漳州", "漳州市");
        city_map.put("开封", "开封市");
        city_map.put("泸州", "泸州市");
        city_map.put("喀什", "喀什地区");
        city_map.put("毕节", "毕节地区");
        city_map.put("贺州", "贺州市");
        city_map.put("玉树", "玉树藏族自治州");
        city_map.put("菏泽", "菏泽");
        city_map.put("蚌埠", "蚌埠市");
        city_map.put("株洲", "株洲市");
        city_map.put("阜阳", "阜阳市");
        city_map.put("邯郸", "邯郸市");
        city_map.put("达州", "达州市");
        city_map.put("大理", "大理白族自治州");
        city_map.put("漯河", "漯河市");
        city_map.put("黄冈", "黄冈市");
        city_map.put("琼海", "琼海");
        city_map.put("呼伦贝尔", "呼伦贝尔市");
        city_map.put("宿州", "宿州市");
        city_map.put("上海", "上海市");
        city_map.put("鞍山", "鞍山市");
        city_map.put("湛江", "湛江市");
        city_map.put("永州", "永州市");
        city_map.put("铜陵", "铜陵市");
        city_map.put("吐鲁番", "吐鲁番地区");
        city_map.put("丽江", "丽江市");
        city_map.put("朔州", "朔州市");
        city_map.put("四平", "四平市");
        city_map.put("阿拉尔", "阿拉尔");
        city_map.put("鄂尔多斯", "鄂尔多斯市");
        city_map.put("海东", "海东地区");
        city_map.put("六安", "六安市");
        city_map.put("伊春", "伊春市");
        city_map.put("湘潭", "湘潭市");
        city_map.put("庆阳", "庆阳市");
        city_map.put("连云港", "连云港市");
        city_map.put("梅州", "梅州市");
        city_map.put("伊犁", "伊犁哈萨克自治州");
        city_map.put("南京", "南京市");
        city_map.put("桂林", "桂林市");
        city_map.put("承德", "承德市");
        city_map.put("咸阳", "咸阳市");
        city_map.put("北海", "北海市");
        city_map.put("榆林", "榆林市");
        city_map.put("南昌", "南昌市");
        city_map.put("天门", "天门");
        city_map.put("咸宁", "咸宁市");
        city_map.put("琼中", "琼中");
        city_map.put("万宁", "万宁");
        city_map.put("上饶", "上饶市");
        city_map.put("威海", "威海市");
        city_map.put("大兴安岭", "大兴安岭地区");
        city_map.put("泉州", "泉州市");
        city_map.put("唐山", "唐山市");
        city_map.put("遂宁", "遂宁市");
        city_map.put("铁岭", "铁岭市");
        city_map.put("鹰潭", "鹰潭市");
        city_map.put("甘南", "甘南藏族自治州");
        city_map.put("防城港", "防城港市");
        city_map.put("镇江", "镇江市");
        city_map.put("济宁", "济宁市");
        city_map.put("潜江", "潜江");
        city_map.put("宜春", "宜春市");
        city_map.put("临汾", "临汾市");
        city_map.put("天津", "天津市");
        city_map.put("日照", "日照市");
        city_map.put("马鞍山", "马鞍山市");
        city_map.put("安顺", "安顺市");
        city_map.put("宜昌", "宜昌市");
        city_map.put("锦州", "锦州市");
        city_map.put("襄阳", "襄阳");
        city_map.put("昌吉", "昌吉回族自治州");
        city_map.put("潍坊", "潍坊市");
        city_map.put("海北", "海北藏族自治州");
        city_map.put("濮阳", "濮阳市");
        city_map.put("淮安", "淮安市");
        city_map.put("普洱", "普洱");
        city_map.put("延边", "延边朝鲜族自治州");
        city_map.put("乐山", "乐山市");
        city_map.put("湘西", "湘西土家族苗族自治州");
        city_map.put("山南", "山南地区");
        city_map.put("渭南", "渭南市");
        city_map.put("信阳", "信阳市");
        city_map.put("博尔塔拉蒙古自治州", "博尔塔拉蒙古自治州");
        city_map.put("茂名", "茂名市");
        city_map.put("赤峰", "赤峰市");
        city_map.put("南阳", "南阳市");
        city_map.put("烟台", "烟台市");
        city_map.put("徐州", "徐州市");
        city_map.put("南平", "南平市");
        city_map.put("甘孜", "甘孜藏族自治州");
        city_map.put("德宏", "德宏傣族景颇族自治州");
        city_map.put("昌江", "昌江");
        city_map.put("武威", "武威市");
        city_map.put("湖州", "湖州市");
        city_map.put("吉安", "吉安市");
        city_map.put("吉林", "吉林市");
        city_map.put("阳江", "阳江市");
        city_map.put("乌鲁木齐", "乌鲁木齐市");
        city_map.put("淮北", "淮北市");
        city_map.put("盐城", "盐城市");
        city_map.put("铜川", "铜川市");
        city_map.put("双鸭山", "双鸭山市");
        city_map.put("怀化", "怀化市");
        city_map.put("许昌", "许昌市");
        city_map.put("北屯", "北屯");
        city_map.put("楚雄", "楚雄彝族自治州");
        city_map.put("文山", "文山壮族苗族自治州");
        city_map.put("阿坝", "阿坝藏族羌族自治州");
        city_map.put("绵阳", "绵阳市");
        city_map.put("宝鸡", "宝鸡市");
        city_map.put("周口", "周口市");
        city_map.put("保山", "保山市");
        city_map.put("三沙", "三沙");
        city_map.put("张家界", "张家界市");
        city_map.put("临沂", "临沂市");
        city_map.put("定西", "定西市");
        city_map.put("衢州", "衢州市");
        city_map.put("白山", "白山市");
        city_map.put("晋中", "晋中市");
        city_map.put("宣城", "宣城市");
        city_map.put("临沧", "临沧市");
        city_map.put("温州", "温州市");
        city_map.put("眉山", "眉山市");
        city_map.put("西宁", "西宁市");
        city_map.put("迪庆", "迪庆藏族自治州");
        city_map.put("衡阳", "衡阳市");
        city_map.put("宿迁", "宿迁市");
        city_map.put("辽源", "辽源市");
        city_map.put("东方", "东方");
        city_map.put("遵义", "遵义市");
        city_map.put("阜新", "阜新市");
        city_map.put("安阳", "安阳市");
        city_map.put("凉山", "凉山彝族自治州");
        city_map.put("延安", "延安市");
        city_map.put("沈阳", "沈阳市");
        city_map.put("兰州", "兰州市");
        city_map.put("汕头", "汕头市");
        city_map.put("鹤岗", "鹤岗市");
        city_map.put("娄底", "娄底市");
        city_map.put("阿克苏", "阿克苏地区");
        city_map.put("辽阳", "辽阳市");
        city_map.put("绥化", "绥化市");
        city_map.put("韶关", "韶关市");
        city_map.put("龙岩", "龙岩市");
        city_map.put("福州", "福州市");
        city_map.put("泰安", "泰安市");
        city_map.put("河源", "河源市");
        city_map.put("巴音郭楞", "巴音郭楞蒙古自治州");
        city_map.put("淮南", "淮南市");
        city_map.put("恩施土家族苗族自治州", "恩施土家族苗族自治州");
        city_map.put("成都", "成都市");
        city_map.put("太原", "太原市");
        city_map.put("随州", "随州市");
        city_map.put("果洛", "果洛藏族自治州");
        city_map.put("忻州", "忻州市");
        city_map.put("博尔塔拉", "博尔塔拉蒙古自治州");
        city_map.put("滁州", "滁州市");
        city_map.put("东营", "东营市");
        city_map.put("拉萨", "拉萨市");
        city_map.put("保定", "保定市");
        city_map.put("长治", "长治市");
        city_map.put("鸡西", "鸡西市");
        city_map.put("崇左", "崇左市");
        city_map.put("包头", "包头市");
        city_map.put("黔西南", "黔西南");
        city_map.put("绍兴", "绍兴市");
        city_map.put("聊城", "聊城市");
        city_map.put("巴中", "巴中市");
        city_map.put("松原", "松原市");
        city_map.put("鹤壁", "鹤壁市");
        city_map.put("阿里地区", "阿里地区");
        city_map.put("厦门", "厦门市");
        city_map.put("黔南", "黔南布依族苗族自治州");
        city_map.put("澳门", "澳门");
        city_map.put("德阳", "德阳市");
        city_map.put("阿里", "阿里地区");
        city_map.put("乐东", "乐东");
        city_map.put("湘西土家族苗族自治州", "湘西土家族苗族自治州");
        city_map.put("荆州", "荆州市");
        city_map.put("淄博", "淄博市");
        city_map.put("鄂州", "鄂州市");
        city_map.put("五家渠", "五家渠");
        city_map.put("营口", "营口市");
        city_map.put("丽水", "丽水市");
        city_map.put("丹东", "丹东市");
        city_map.put("嘉峪关", "嘉峪关市");
        city_map.put("攀枝花", "攀枝花市");
        city_map.put("锡林郭勒", "锡林郭勒盟");
        city_map.put("石家庄", "石家庄市");
        city_map.put("合肥", "合肥市");
        city_map.put("东莞", "东莞市");
        city_map.put("枣庄", "枣庄市");
        city_map.put("延边朝鲜族自治州", "延边朝鲜族自治州");
        city_map.put("重庆", "重庆市");
        city_map.put("焦作", "焦作市");
        city_map.put("巴音郭楞蒙古自治州", "巴音郭楞蒙古自治州");
        city_map.put("屯昌", "屯昌");
        city_map.put("牡丹江", "牡丹江市");
        city_map.put("乌海", "乌海市");
        city_map.put("黄石", "黄石市");
        city_map.put("呼和浩特", "呼和浩特市");
        city_map.put("三门峡", "三门峡市");
        city_map.put("神农架", "神农架");
        city_map.put("扬州", "扬州市");
        city_map.put("张家口", "张家口市");
        city_map.put("昌都", "昌都地区");
        city_map.put("十堰", "十堰市");
        city_map.put("张掖", "张掖市");
        city_map.put("银川", "银川市");
        city_map.put("玉溪", "玉溪市");
        city_map.put("乌兰察布", "乌兰察布市");
        city_map.put("日喀则", "日喀则地区");
        city_map.put("常德", "常德市");
        city_map.put("临夏", "临夏回族自治州");
        city_map.put("陵水", "陵水");
        city_map.put("吴忠", "吴忠市");
        city_map.put("德州", "德州市");
        city_map.put("平顶山", "平顶山市");
        city_map.put("嘉兴", "嘉兴市");
        city_map.put("柳州", "柳州市");
        city_map.put("白银", "白银市");
        city_map.put("兴安", "大兴安岭地区");
        city_map.put("长沙", "长沙市");
        city_map.put("定安", "定安");
        city_map.put("金华", "金华市");
        city_map.put("黄山", "黄山市");
        city_map.put("广安", "广安市");
        city_map.put("岳阳", "岳阳市");
        city_map.put("揭阳", "揭阳市");
        city_map.put("南充", "南充市");
        city_map.put("海口", "海口市");
        city_map.put("贵港", "贵港市");
        city_map.put("萍乡", "萍乡市");
        city_map.put("六盘水", "六盘水市");
        city_map.put("安康", "安康市");
        city_map.put("南宁", "南宁市");
        city_map.put("孝感", "孝感市");
        city_map.put("那曲", "那曲地区");
        city_map.put("安庆", "安庆市");
        city_map.put("邢台", "邢台市");
        city_map.put("曲靖", "曲靖市");
        city_map.put("黔东南", "黔东南苗族侗族自治州");
    }

    // 全国省映射
    static HashMap<String, String> province_map = new HashMap<>();
    static {
        province_map.put("北京", "北京");
        province_map.put("天津", "天津");
        province_map.put("河北", "河北");
        province_map.put("山西", "山西");
        province_map.put("内蒙古", "内蒙古");
        province_map.put("辽宁", "辽宁");
        province_map.put("吉林", "吉林");
        province_map.put("黑龙江", "黑龙江");
        province_map.put("上海", "上海");
        province_map.put("江苏", "江苏");
        province_map.put("浙江", "浙江");
        province_map.put("安徽", "安徽");
        province_map.put("福建", "福建");
        province_map.put("江西", "江西");
        province_map.put("山东", "山东");
        province_map.put("河南", "河南");
        province_map.put("湖北", "湖北");
        province_map.put("湖南", "湖南");
        province_map.put("广东", "广东");
        province_map.put("广西", "广西");
        province_map.put("海南", "海南");
        province_map.put("重庆", "重庆");
        province_map.put("四川", "四川");
        province_map.put("贵州", "贵州");
        province_map.put("云南", "云南");
        province_map.put("西藏", "西藏");
        province_map.put("陕西", "陕西");
        province_map.put("甘肃", "甘肃");
        province_map.put("青海", "青海");
        province_map.put("宁夏", "宁夏");
        province_map.put("新疆", "新疆");
        province_map.put("台湾", "台湾");
        province_map.put("香港", "香港");
        province_map.put("澳门", "澳门");
    }

    // 成都市下属区映射
    static HashMap<String, String> cd_area_map = new HashMap<>();
    static {
        cd_area_map.put("锦江", "锦江区");
        cd_area_map.put("青羊", "青羊区");
        cd_area_map.put("武侯", "武侯区");
        cd_area_map.put("龙泉驿", "龙泉驿区");
        cd_area_map.put("金牛", "金牛区");
        cd_area_map.put("成华", "成华区");
        cd_area_map.put("青白江", "青白江区");
        cd_area_map.put("温江", "温江区");
        cd_area_map.put("新都", "新都区");
        cd_area_map.put("金堂", "金堂县");
        cd_area_map.put("双流", "双流区");
        cd_area_map.put("郫县", "郫都区");
        cd_area_map.put("大邑", "大邑县");
        cd_area_map.put("蒲江", "蒲江县");
        cd_area_map.put("新津", "新津县");
        cd_area_map.put("简阳", "简阳市");
        cd_area_map.put("都江堰", "都江堰市");
        cd_area_map.put("彭州", "彭州市");
        cd_area_map.put("邛崃", "邛崃市");
        cd_area_map.put("崇州", "崇州市");
    }


    /**
     * 检查报告表是否更新
     * @param start_time    开始时间
     * @param t             报告周期类型,1:周 2:月 3:季 4:年
     * @return
     */
    @Override
    public Model check_update(String start_time, Integer t) {
        MongoCursor<Document> route_data = radarDataDao.collectionRoute("resume", t, start_time);
        if (!route_data.hasNext())
            return new Model(-1, "请求时间："+ start_time +"，没有数据！");

        Document next = route_data.next();
        String coll_name = next.getString("collection_name");
        Long count = radarDataDao.getColletion(coll_name).count();
        return new Model().setData(new Document("name", coll_name).append("count", count));
    }

    /**
     * 人才流动
     * @param query     过滤条件
     * @param key       聚合关键词
     * @param api_url   api相对地址
     * @param params    参数键值对
     * @return
     */
    @Override
    public Model talent_flow(Document query, String key, String api_url, JSONObject params) {
        // 查询是否有路由表
        Integer t = params.getInteger("t");
        String start_time = params.getString("start_time");
        MongoCursor<Document> route_data = radarDataDao.collectionRoute("resume", t, start_time);
        if (!route_data.hasNext())
            return new Model(-1, "请求时间："+ start_time +"，没有数据！");

        // 查询是否有缓存
        List<RadarData> out = new ArrayList<>();
        Document next = route_data.next();
        String collection_name = next.getString("collection_name");
        RadarData first = radarBaseDao.find_one(collection_name, api_url, params);
        if (first != null) {
            out.add(first);
            return returnOneData(out, api_url, params);
        }

        // 聚合城市数据，并放入缓存
        String type = "resume_flow";
        Integer top = params.getIntValue("top");
        AggregateIterable<Document> city_documents = radarDataDao.talent_flow(collection_name, query, key, type, top);
        List<Document> city_data = getValue(city_documents, "_id", "count", top, "city");
        SimpleDateFormat time_format = new SimpleDateFormat("yyyy-MM-dd");

        RadarData radar_data = new RadarData();
        radar_data.set_id(CommonUtil.md5(collection_name + api_url + JSON.toJSONString(params)));
        radar_data.setCollection(collection_name);
        radar_data.setApi_url(api_url);
        radar_data.setParams(params);
        radar_data.setT(t);
        radar_data.setApi_time(time_format.format(new Date()));
        radar_data.setYear(start_time.split("-")[0]);
        radar_data.setMonth(start_time.split("-")[1]);
        radar_data.setCity_data(city_data);
        out.add(radar_data);
        radarBaseDao.saveRadarData(radar_data);

        return returnOneData(out, api_url, params);
    }

    /**
     * 人才需求分布
     * @param api_url   api相对地址
     * @param query     过滤条件
     * @param params    参数键值对
     * @return
     */
    @Override
    public Model talent_demand_distribute(String api_url, Document query, JSONObject params) {
        // 查询是否有路由表
        Integer t = params.getInteger("t");
        String start_time = params.getString("start_time");
        MongoCursor<Document> route_data = radarDataDao.collectionRoute("resume", t, start_time);
        if (!route_data.hasNext())
            return new Model(-1, "请求时间："+ start_time +"，没有数据！");

        // 查询是否有缓存
        List<RadarData> out = new ArrayList<>();
        Document next = route_data.next();
        String collection_name = next.getString("collection_name");
        RadarData first = radarBaseDao.find_one(collection_name, api_url, params);
        if (first != null) {
            out.add(first);
            return returnOneData(out, api_url, params);
        }

        // 聚合区域数据，并放入缓存
        String type = "chengdu_position_demand";
        Integer top = params.getIntValue("top");
        String key = "area";
        AggregateIterable<Document> area_documents = radarDataDao.talent_demand_distribute(collection_name, query, key, type, top);
        List<Document> city_data = getValue(area_documents, "_id", "count", top, "area");
        // 聚合行业数据
        key = "company_type";
        AggregateIterable<Document> industry_documents = radarDataDao.talent_demand_distribute(collection_name, query, key, type, top);
        List<Document> industry_data = getValue(industry_documents, "_id", "count", top, "");
        // 聚合岗位数据
        key = "position";
        AggregateIterable<Document> position_documents = radarDataDao.talent_demand_distribute(collection_name, query, key, type, top);
        List<Document> position_data = getValue(position_documents, "_id", "count", top, "");
        SimpleDateFormat time_format = new SimpleDateFormat("yyyy-MM-dd");

        RadarData radar_data = new RadarData();
        radar_data.set_id(CommonUtil.md5(collection_name + api_url + JSON.toJSONString(params)));
        radar_data.setCollection(collection_name);
        radar_data.setApi_url(api_url);
        radar_data.setParams(params);
        radar_data.setT(t);
        radar_data.setApi_time(time_format.format(new Date()));
        radar_data.setYear(start_time.split("-")[0]);
        radar_data.setMonth(start_time.split("-")[1]);
        radar_data.setCity_data(city_data);
        radar_data.setIndustry_data(industry_data);
        radar_data.setPosition_data(position_data);
        out.add(radar_data);
        radarBaseDao.saveRadarData(radar_data);

        return returnOneData(out, api_url, params);
    }

    /**
     * 人才分布
     * @param api_url   api相对地址
     * @param params    参数键值对
     * @return
     */
    @Override
    public Model talent_distribute(String api_url, JSONObject params) {
        // 查询是否有路由表
        Integer t = params.getInteger("t");
        String start_time = params.getString("start_time");
        MongoCursor<Document> route_data = radarDataDao.collectionRoute("resume", t, start_time);
        if (!route_data.hasNext())
            return new Model(-1, "请求时间："+ start_time +"，没有数据！");

        // 查询是否有缓存
        List<RadarData> out = new ArrayList<>();
        Document next = route_data.next();
        String collection_name = next.getString("collection_name");
        RadarData first = radarBaseDao.find_one(collection_name, api_url, params);
        if (first != null) {
            out.add(first);
            return returnOneData(out, api_url, params);
        }

        // 聚合国家数据，并放入缓存
        Document query = new Document();
        String type = "resume_flow";
        Integer top = params.getIntValue("top");
        String key = "living_country";
        AggregateIterable<Document> country_documents = radarDataDao.talent_distribute(collection_name, query, key, type, top);
        List<Document> country_data = getValue(country_documents, "_id", "count", top, "country");
        // 聚合省份数据
        key = "living_province";
        query.append("living_country", "中国");
        AggregateIterable<Document> province_documents = radarDataDao.talent_distribute(collection_name, query, key, type, top);
        List<Document> province_data = getValue(province_documents, "_id", "count", top, "province");
        SimpleDateFormat time_format = new SimpleDateFormat("yyyy-MM-dd");

        RadarData radar_data = new RadarData();
        radar_data.set_id(CommonUtil.md5(collection_name + api_url + JSON.toJSONString(params)));
        radar_data.setCollection(collection_name);
        radar_data.setApi_url(api_url);
        radar_data.setParams(params);
        radar_data.setT(t);
        radar_data.setApi_time(time_format.format(new Date()));
        radar_data.setYear(start_time.split("-")[0]);
        radar_data.setMonth(start_time.split("-")[1]);
        radar_data.setCountry_data(country_data);
        radar_data.setProvince_data(province_data);
        out.add(radar_data);
        radarBaseDao.saveRadarData(radar_data);

        return returnOneData(out, api_url, params);
    }

    /**
     * 从迭代器中 获取数据
     * @param iterable  迭代器
     * @param key1      关键词1
     * @param key2      关键词2
     * @param count     Top指数
     * @param type      映射类型
     * @return
     */
    public List<Document> getValue(Iterable<Document> iterable, String key1, String key2, Integer count, String type) {
        List<Document> out = new ArrayList<>();
        int i = 0;
        for (Document d : iterable) {
            Object id = d.get("_id");
            if (id != null && !id.equals("") && !id.equals("unknow") && !id.equals("unknown") && !id.equals("empty") && !id.equals("NotRecognition")) {
                if (i >= count && count != 0)
                    break;

                if (type.equals("city"))
                    d.put("name", city_map.get(d.getString(key1)));
                else if (type.equals("country"))
                    d.put("name", country_map.get(d.getString(key1)));
                else if (type.equals("province"))
                    d.put("name", province_map.get(d.getString(key1)));
                else if (type.equals("area"))
                    d.put("name", cd_area_map.get(d.getString(key1)));
                else
                    d.put("name", d.get(key1));
                d.put("value", d.get(key2));
                d.remove("_id");
                d.remove("count");
                out.add(d);
                i++;
            }
        }
        return out;
    }

    /**
     * 返回接口data数据
     * @param resume    数据列表
     * @param url       api相对地址
     * @param params    参数键值对
     * @return
     */
    public Model returnOneData(List<RadarData> resume, String url, JSONObject params) {
        ApiRadarData radar_data = new ApiRadarData();
        if (resume.size() > 0) {
            // 此处不考虑 同比环比，所以直接取第一个
            radar_data.set_id(resume.get(0).get_id());
            radar_data.setT(resume.get(0).getT());
            radar_data.setYear(resume.get(0).getYear());
            radar_data.setMonth(resume.get(0).getMonth());
            radar_data.setApi_time(resume.get(0).getApi_time());
            radar_data.setApi_url(resume.get(0).getApi_url());
            radar_data.setParams(resume.get(0).getParams());
            radar_data.setCity_data(resume.get(0).getCity_data());
            radar_data.setProvince_data(resume.get(0).getProvince_data());
            radar_data.setCountry_data(resume.get(0).getCountry_data());
            radar_data.setIndustry_data(resume.get(0).getIndustry_data());
            radar_data.setPosition_data(resume.get(0).getPosition_data());
            return new Model().setData(radar_data);
        } else
            return new Model(-1, "查询数据失败");
    }
}
