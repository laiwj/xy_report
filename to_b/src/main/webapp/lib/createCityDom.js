/**
 * Created by Administrator on 2017/3/14.
 */
//生成level2city.html dom 节点

//var firstLevel = {"北京":"北京","天津":"天津","河北":"河北","山西":"山西","内蒙古":"内蒙古","辽宁":"辽宁","吉林":"吉林","黑龙江":"黑龙江","上海":"上海","江苏":"江苏","浙江":"浙江","安徽":"安徽","福建":"福建","江西":"江西","山东":"山东","河南":"河南","湖北":"湖北","湖南":"湖南","广东":"广东","广西":"广西","海南":"海南","重庆":"重庆","四川":"四川","贵州":"贵州","云南":"云南","西藏":"西藏","陕西":"陕西","甘肃":"甘肃","青海":"青海","宁夏":"宁夏","新疆":"新疆"};
//var secondLevel = {"北京":{},"天津":{},"河北":{"石家庄市":"石家庄市","唐山市":"唐山市","秦皇岛市":"秦皇岛市","邯郸市":"邯郸市","邢台市":"邢台市","保定市":"保定市","张家口市":"张家口市","承德市":"承德市","沧州市":"沧州市","廊坊市":"廊坊市","衡水市":"衡水市"},"山西":{"太原市":"太原市","大同市":"大同市","阳泉市":"阳泉市","长治市":"长治市","晋城市":"晋城市","朔州市":"朔州市","晋中市":"晋中市","运城市":"运城市","忻州市":"忻州市","临汾市":"临汾市","吕梁市":"吕梁市"},"内蒙古":{"呼和浩特市":"呼和浩特市","包头市":"包头市","乌海市":"乌海市","赤峰市":"赤峰市","通辽市":"通辽市","鄂尔多斯市":"鄂尔多斯市","呼伦贝尔市":"呼伦贝尔市","巴彦淖尔市":"巴彦淖尔市","乌兰察布市":"乌兰察布市","兴安市":"兴安市","锡林郭勒市":"锡林郭勒市","阿拉善市":"阿拉善市"},"辽宁":{"沈阳市":"沈阳市","大连市":"大连市","鞍山市":"鞍山市","抚顺市":"抚顺市","本溪市":"本溪市","丹东市":"丹东市","锦州市":"锦州市","营口市":"营口市","阜新市":"阜新市","辽阳市":"辽阳市","盘锦市":"盘锦市","铁岭市":"铁岭市","朝阳市":"朝阳市","葫芦岛市":"葫芦岛市"},"吉林":{"长春市":"长春市","吉林市":"吉林市","四平市":"四平市","辽源市":"辽源市","通化市":"通化市","白山市":"白山市","松原市":"松原市","白城市":"白城市","延边市":"延边市"},"黑龙江":{"哈尔滨市":"哈尔滨市","齐齐哈尔市":"齐齐哈尔市","鸡西市":"鸡西市","鹤岗市":"鹤岗市","双鸭山市":"双鸭山市","大庆市":"大庆市","伊春市":"伊春市","佳木斯市":"佳木斯市","七台河市":"七台河市","牡丹江市":"牡丹江市","黑河市":"黑河市","绥化市":"绥化市","大兴安岭市":"大兴安岭市"},"上海":{"黄浦区":"黄浦区","卢湾区":"卢湾区","徐汇区":"徐汇区","长宁区":"长宁区","静安区":"静安区","普陀区":"普陀区","闸北区":"闸北区","虹口区":"虹口区","杨浦区":"杨浦区","闵行区":"闵行区","宝山区":"宝山区","嘉定区":"嘉定区","浦东新区":"浦东新区","金山区":"金山区","松江区":"松江区","奉贤区":"奉贤区","青浦区":"青浦区","崇明县":"崇明县"},"江苏":{"南京市":"南京市","无锡市":"无锡市","徐州市":"徐州市","常州市":"常州市","苏州市":"苏州市","南通市":"南通市","连云港市":"连云港市","淮安市":"淮安市","盐城市":"盐城市","扬州市":"扬州市","镇江市":"镇江市","泰州市":"泰州市","宿迁市":"宿迁市"},"浙江":{"杭州市":"杭州市","宁波市":"宁波市","温州市":"温州市","嘉兴市":"嘉兴市","湖州市":"湖州市","绍兴市":"绍兴市","金华市":"金华市","衢州市":"衢州市","舟山市":"舟山市","台州市":"台州市","丽水市":"丽水市"},"安徽":{"合肥市":"合肥市","芜湖市":"芜湖市","蚌埠市":"蚌埠市","淮南市":"淮南市","马鞍山市":"马鞍山市","淮北市":"淮北市","铜陵市":"铜陵市","安庆市":"安庆市","黄山市":"黄山市","滁州市":"滁州市","阜阳市":"阜阳市","宿州市":"宿州市","巢湖市":"巢湖市","六安市":"六安市","亳州市":"亳州市","池州市":"池州市","宣城市":"宣城市"},"福建":{"福州市":"福州市","厦门市":"厦门市","莆田市":"莆田市","三明市":"三明市","泉州市":"泉州市","漳州市":"漳州市","南平市":"南平市","龙岩市":"龙岩市","宁德市":"宁德市"},"江西":{"南昌市":"南昌市","景德镇市":"景德镇市","萍乡市":"萍乡市","九江市":"九江市","新余市":"新余市","鹰潭市":"鹰潭市","赣州市":"赣州市","吉安市":"吉安市","宜春市":"宜春市","抚州市":"抚州市","上饶市":"上饶市"},"山东":{"济南市":"济南市","青岛市":"青岛市","淄博市":"淄博市","枣庄市":"枣庄市","东营市":"东营市","烟台市":"烟台市","潍坊市":"潍坊市","济宁市":"济宁市","泰安市":"泰安市","威海市":"威海市","日照市":"日照市","莱芜市":"莱芜市","临沂市":"临沂市","德州市":"德州市","聊城市":"聊城市","滨州市":"滨州市","菏泽市":"菏泽市"},"河南":{"郑州市":"郑州市","开封市":"开封市","洛阳市":"洛阳市","平顶山市":"平顶山市","安阳市":"安阳市","鹤壁市":"鹤壁市","新乡市":"新乡市","焦作市":"焦作市","濮阳市":"濮阳市","许昌市":"许昌市","漯河市":"漯河市","三门峡市":"三门峡市","南阳市":"南阳市","商丘市":"商丘市","信阳市":"信阳市","周口市":"周口市","驻马店市":"驻马店市","济源市":"济源市"},"湖北":{"武汉市":"武汉市","黄石市":"黄石市","十堰市":"十堰市","宜昌市":"宜昌市","襄樊市":"襄樊市","鄂州市":"鄂州市","荆门市":"荆门市","孝感市":"孝感市","荆州市":"荆州市","黄冈市":"黄冈市","咸宁市":"咸宁市","随州市":"随州市","恩施市":"恩施市","仙桃市":"仙桃市","潜江市":"潜江市","天门市":"天门市","神农架市":"神农架市"},"湖南":{"长沙市":"长沙市","株洲市":"株洲市","湘潭市":"湘潭市","衡阳市":"衡阳市","邵阳市":"邵阳市","岳阳市":"岳阳市","常德市":"常德市","张家界市":"张家界市","益阳市":"益阳市","郴州市":"郴州市","永州市":"永州市","怀化市":"怀化市","娄底市":"娄底市","湘西市":"湘西市"},"广东":{"广州市":"广州市","韶关市":"韶关市","深圳市":"深圳市","珠海市":"珠海市","汕头市":"汕头市","佛山市":"佛山市","江门市":"江门市","湛江市":"湛江市","茂名市":"茂名市","肇庆市":"肇庆市","惠州市":"惠州市","梅州市":"梅州市","汕尾市":"汕尾市","河源市":"河源市","阳江市":"阳江市","清远市":"清远市","东莞市":"东莞市","中山市":"中山市","潮州市":"潮州市","揭阳市":"揭阳市","云浮市":"云浮市"},"广西":{"南宁市":"南宁市","柳州市":"柳州市","桂林市":"桂林市","梧州市":"梧州市","北海市":"北海市","防城港市":"防城港市","钦州市":"钦州市","贵港市":"贵港市","玉林市":"玉林市","百色市":"百色市","贺州市":"贺州市","河池市":"河池市","来宾市":"来宾市","崇左市":"崇左市"},"海南":{"海口市":"海口市","三亚市":"三亚市","五指山市":"五指山市","琼海市":"琼海市","儋州市":"儋州市","文昌市":"文昌市","万宁市":"万宁市","东方市":"东方市"},"重庆":{"万州区":"万州区","涪陵区":"涪陵区","渝中区":"渝中区","大渡口区":"大渡口区","江北区":"江北区","沙坪坝区":"沙坪坝区","九龙坡区":"九龙坡区","南岸区":"南岸区","北碚区":"北碚区","万盛区":"万盛区","双挢区":"双挢区","渝北区":"渝北区","巴南区":"巴南区","长寿区":"长寿区","綦江县":"綦江县","潼南县":"潼南县","铜梁县":"铜梁县","大足县":"大足县","荣昌县":"荣昌县","壁山县":"壁山县","梁平县":"梁平县","城口县":"城口县","丰都县":"丰都县","垫江县":"垫江县","武隆县":"武隆县","忠县":"忠县","开县":"开县","云阳县":"云阳县","奉节县":"奉节县","巫山县":"巫山县","巫溪县":"巫溪县","黔江区":"黔江区","石柱土家族自治县":"石柱土家族自治县","秀山土家族苗族自治县":"秀山土家族苗族自治县","酉阳土家族苗族自治县":"酉阳土家族苗族自治县","彭水苗族土家族自治县":"彭水苗族土家族自治县","江津区":"江津区","合川区":"合川区","永川区":"永川区","南川区":"南川区"},"四川":{"成都市":"成都市","自贡市":"自贡市","攀枝花市":"攀枝花市","泸州市":"泸州市","德阳市":"德阳市","绵阳市":"绵阳市","广元市":"广元市","遂宁市":"遂宁市","内江市":"内江市","乐山市":"乐山市","南充市":"南充市","眉山市":"眉山市","宜宾市":"宜宾市","广安市":"广安市","达川市":"达川市","雅安市":"雅安市","巴中市":"巴中市","资阳市":"资阳市","阿坝市":"阿坝市","甘孜市":"甘孜市","凉山市":"凉山市"},"贵州":{"贵阳市":"贵阳市","六盘水市":"六盘水市","遵义市":"遵义市","安顺市":"安顺市","铜仁市":"铜仁市","黔西南市":"黔西南市","毕节市":"毕节市","黔东南市":"黔东南市","黔南市":"黔南市"},"云南":{"昆明市":"昆明市","曲靖市":"曲靖市","玉溪市":"玉溪市","保山市":"保山市","昭通市":"昭通市","丽江市":"丽江市","普洱市":"普洱市","临沧市":"临沧市","楚雄市":"楚雄市","红河市":"红河市","文山市":"文山市","西双版纳市":"西双版纳市","大理市":"大理市","德宏市":"德宏市","怒江傈市":"怒江傈市","迪庆市":"迪庆市"},"西藏":{"拉萨市":"拉萨市","昌都市":"昌都市","山南市":"山南市","日喀则市":"日喀则市","那曲市":"那曲市","阿里市":"阿里市","林芝市":"林芝市"},"陕西":{"西安市":"西安市","铜川市":"铜川市","宝鸡市":"宝鸡市","咸阳市":"咸阳市","渭南市":"渭南市","延安市":"延安市","汉中市":"汉中市","榆林市":"榆林市","安康市":"安康市","商洛市":"商洛市"},"甘肃":{"兰州市":"兰州市","嘉峪关市":"嘉峪关市","金昌市":"金昌市","白银市":"白银市","天水市":"天水市","武威市":"武威市","张掖市":"张掖市","平凉市":"平凉市","酒泉市":"酒泉市","庆阳市":"庆阳市","定西市":"定西市","陇南市":"陇南市","临夏市":"临夏市","甘南市":"甘南市"},"青海":{"西宁市":"西宁市","海东市":"海东市","海北市":"海北市","黄南市":"黄南市","海南市":"海南市","果洛市":"果洛市","玉树市":"玉树市","海西市":"海西市"},"宁夏":{"银川市":"银川市","石嘴山市":"石嘴山市","吴忠市":"吴忠市","固原市":"固原市","中卫市":"中卫市"},"新疆":{"乌鲁木齐市":"乌鲁木齐市","克拉玛依市":"克拉玛依市","吐鲁番市":"吐鲁番市","哈密市":"哈密市","昌吉市":"昌吉市","博尔塔拉市":"博尔塔拉市","巴音郭楞市":"巴音郭楞市","阿克苏市":"阿克苏市","克孜勒苏市":"克孜勒苏市","喀什市":"喀什市","和田市":"和田市","伊犁市":"伊犁市","塔城市":"塔城市","阿勒泰市":"阿勒泰市","石河子市":"石河子市","阿拉尔市":"阿拉尔市","图木舒克市":"图木舒克市","五家渠市":"五家渠市"},"香港":{"中西区市":"中西区市","东区市":"东区市","九龙城区市":"九龙城区市","观塘区市":"观塘区市","南区市":"南区市","深水区市":"深水区市","湾仔区市":"湾仔区市","黄大仙区市":"黄大仙区市","油尖旺区市":"油尖旺区市","离岛区市":"离岛区市","葵青区市":"葵青区市","北区市":"北区市","西贡区市":"西贡区市","沙田区市":"沙田区市","屯门区市":"屯门区市","大埔区市":"大埔区市","荃湾区市":"荃湾区市","元朗区市":"元朗区市"},"澳门":{"花地玛堂区市":"花地玛堂区市","圣安多尼堂区市":"圣安多尼堂区市","大堂区市":"大堂区市","望德堂区市":"望德堂区市","风顺堂区市":"风顺堂区市","嘉模堂区市":"嘉模堂区市","圣方济各堂区市":"圣方济各堂区市"},"台湾":{"台北市市":"台北市市","高雄市市":"高雄市市","基隆市市":"基隆市市","台中市市":"台中市市","台南市市":"台南市市","新竹市市":"新竹市市","嘉义市市":"嘉义市市","台北县市":"台北县市","宜兰县市":"宜兰县市","新竹县市":"新竹县市","桃园县市":"桃园县市","苗栗县市":"苗栗县市","台中县市":"台中县市","彰化县市":"彰化县市","南投县市":"南投县市","嘉义县市":"嘉义县市","云林县市":"云林县市","台南县市":"台南县市","高雄县市":"高雄县市","屏东县市":"屏东县市","台东县市":"台东县市","花莲县市":"花莲县市","澎湖县市":"澎湖县市"}};
var firstLevel = {"北京":"北京","上海":"上海","深圳":"深圳","广州":"广州","成都":"成都","武汉":"武汉","南京":"南京","杭州":"杭州","苏州":"苏州","辽源":"辽源","天津":"天津","重庆":"重庆"}
var secondLevel = {};
var _option = {data:{firstLevel:firstLevel,secondLevel:secondLevel}}
function createMultilevelSelectHTML(option){
			 var me = this;
			 var arr = [];
			 var data = option.data || this.getAll();
			 arr.push('<div class="options-picker" style="display: none;">');
			 arr.push('<div><div class="first-level-content">')
            arr.push('<div  _value="" class="pick-all-first"><input type="checkbox" class="check-all-first"/><span class="li-text ml5"><b>全部</b></span></div>');
			 var _data = data.firstLevel;
			var f_index = 0;
			 $.each(_data, function(i,index){
				 arr.push('<div  _value="'+_data[i]+'" class="picker-first-level"><input index="'+(f_index+=f_index)+'" type="checkbox" class="checkbox-first-item" value="'+_data[i]+'"/><span class="li-text ml5">'+_data[i]+'</span></div>');
			 })
			 arr.push('</div>')

			 arr.push('<div classs="next-level-content">')
			 $.each(_data, function(i){
				 arr.push('<div class="picker-next-level level2-picker-next-level" style="display: none;" _title="'+_data[i]+'">');
				 var _second = data.secondLevel[_data[i]];
                 if(!_second){
                     arr.push('</div>');
					 arr.push('</div>');
                     return;
                 }
                 if(!$.isEmptyObject(_second)){
                      arr.push('<div  class="m10 pick-all-second" ><input type="checkbox" class="check-all-second"/><span class="picker-second-title ml5">全部</span></div>');
                 }
                 var s_index= 0 ;
				 $.each(_second, function(k ,v){
					 arr.push('<div  class="picker-next-level-item" >');
					 if(_data[i] != k){
						 arr.push('<input type="checkbox"  index="'+f_index+'$'+(s_index+=s_index)+'" class="checkbox-second-item" value="'+k+'"/><span class="picker-second-title ml5">'+k+'</span>');
					 }
					 if(!v || v.length == 0){
						 arr.push('</div>');
						 arr.push('</div>');
						 return;
					 }
					 arr.push('</div>');
				 })
				 arr.push('</div>');

			 })
			 arr.push('</div>')
    return arr.join("");

		 }

		 console.log(createMultilevelSelectHTML(_option))