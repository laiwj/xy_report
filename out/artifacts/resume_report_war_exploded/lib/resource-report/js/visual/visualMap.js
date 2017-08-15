/**
 * Created by simon on 2017/5/8.
 
依赖：
    china.js            //中国地图
    citys.js            //城市坐标
    echarts.v3.min.js   //图形库
    public.js           //Init3()
    d3.v3.min.js        //d3绘图引擎
    dragTool.js         //addDragTool()
    
 */   

/*----------------------------------地图核心程序----------------------------------*/
/*
 描述：整合 人才流动 和 人才分布 图功能
    人才流动：getOptionMap("flowOut") 或 showMapFlow()
    人才分布：getOptionMap("top") 或 showMapTop()
    职能分布：getOptionMap("region")     - 配合饼图，区域放大
    输出图配置项或直接绘图
 输入：
    id - 图形容器div, string
    data - 数据, array with object
        example: [{name:'上海',value:85},{name:'广州',value:90}]
    fDirection - 流动方向， 0-'flowOut' || 1-'flowIn'
    cCity - 中心城市, string
 调用：
    var map = new MapGeo(id, data);
    map.showMapFlow();
    map.showMapTop();
 依赖：
    china.js            //中国地图
    citys.js            //城市坐标
    echarts.v3.min.js   //图形库
    public.js           //Init3()
    d3.v3.min.js        //d3绘图引擎
    dragTool.js         //addDragTool()
 */

function MapGeo(id, data, fDirection, cCity){
    if(id==undefined || data==undefined){ console.error("实例化错误"); return; }
    var $this = this;
    this.id = id;           //图形容器div
    this.maxR = 13;         //圆最大半径
    this.flowD = fDirection || 0;     //流动方向：0-流出，1-流入
    this.zoom = 8;          //地图区域放大比例
    this.city = cCity || '成都';
    this.province = getProvinceByCity($this.city);
    this.data = data.sort(function(a, b){ return b.value - a.value; });
    this.myEcharts = null;
    MapGeo.flowIn = 'flowIn';
    MapGeo.flowOut = 'flowOut';
    MapGeo.regions = 'region';
    MapGeo.top = 'top';
    
    var flowPath = 'path://M5,1 L8,10 L5,6 L2,10 L5,2';     //箭头样式 - 自带arrow太难看
    var color = {'points':'#397ACA', 'hover':'#EA899A', 'lines':'#397ACA', 'center':'#C4DCFC', 'area':'#E6ECF2'};
    var maxV = data.reduce(function(a, b){ return a>b.value ? a : b.value; }, 0);
    var scaleS = function(v){ return Math.ceil($this.maxR * v / maxV); };   //线性映射，控制节点大小

    //数据规范化，添加经纬度信息
    function convertData (dt) {
        var res = [], fromCoord = getCityPosEx($this.city);
        dt.forEach(function(d, i){
            var toCoord = getCityPosEx(d.name);
            var line = { fromName: $this.city, toName: d.name, coords: [fromCoord, toCoord]};
            if($this.flowD==1)line = { fromName: d.name, toName: $this.city, coords: [toCoord, fromCoord]};
            if (toCoord) res.push(line);
        });
        return res;
    };

    //地图区域及高亮颜色
    var itemStyle = {
        normal: { areaColor: color.area, borderColor: '#F5F6F8', borderWidth: 2, }, //区域样式
        emphasis: { areaColor: color.center }
    };

    //核心配置项，区域+点+线
    var regions = {
        name: '中国',type: 'map',mapType: 'china', selectedMode : false,
        label: { normal: { show: true }, emphasis: {show: true}}, itemStyle: itemStyle,
        center: [], zoom: $this.zoom, data:[], silent:true //禁止鼠标事件
    };

    //源数据 - 点
    var dataTop = data.map(function (d) {
        var pos = getCityPosEx(d.name), line = { name: d.name, value: pos.concat([d.value])};
        if(d.value==maxV)line['itemStyle'] = {normal:{color:color.points }};
        return line;
    });     //.concat({ name:"成都", value:[103.9526, 30.7617, 100] })
    this.dataTop = dataTop.sort(function(a, b){ return b.value[2] - a.value[2]; });
    this.dataTop5 = $this.dataTop.slice(0, 5);
    this.dataFlow = convertData(data);
    this.dataFlow5 = $this.dataFlow.slice(0, 5);
    
    //点
    var points ={
        type: 'effectScatter', coordinateSystem: 'geo', zlevel: 2,
        rippleEffect: { brushType: 'stroke' },
        label: { normal: { show: true, position: 'right', formatter: '{b}' } },
        symbolSize: function (val) { return (val==undefined && 1) || scaleS(val[2]); },
        itemStyle: { normal: { color: color.points }, emphasis:{color:color.hover,opacity:0.8 }}, //标注点样式
        data: $this.dataTop
    };

    //线
    var lines = {
        type: 'lines', zlevel: 1,
        symbol: ['none', 'arrow'], symbolSize: 10,
        effect: { show: true, period: 6, trailLength: 0, symbolSize: 8, symbol: flowPath },
        lineStyle: { normal: { width: 1, opacity: 0.6, curveness: 0.2, color: color.lines}},   //线条样式
        data: $this.dataFlow
    };

    //四川区域
    var center = [{ name:$this.province, selected:true, label: { emphasis: {show:false}, normal:{ show:true } } },];

    var visualMap = {
        left: 'right', top: '10%', dimension: 2,
        min: 0, max: 250,
        itemWidth: 30, itemHeight: 120, calculable: true, precision: 0.1,
        text: ['Top：5-10'], textGap: 30, textStyle: { color: '#fff' },
        inRange: { symbolSize: [10, 70] },
        outOfRange: { symbolSize: [10, 70], color: ['rgba(255,255,255,.2)'] },
        controller: {
            inRange: { color: ['#c23531'] }, outOfRange: { color: ['#444']}
        }
    };


    //配置项
    this.option = {
        //backgroundColor: '#404a59',
        title : { text: '', subtext: '', left: 'center', textStyle : { color: '#fff' } },
        tooltip: { trigger: 'item', formatter:function(p){
            if(p.componentSubType!="effectScatter")return;  //暂时不做线条提示
            return p.name + "<br>人数：" + p.data.value[2] + " "; }
        },
        geo: { map: 'china', label: { emphasis: { show: false } }, selectedMode:false, roam: false,
            itemStyle: itemStyle, silent:true, zoom:1.2,
        },
        series: []
    };

    //获取配置项
    this.getOptionMap = function(type){
        type = type || "top";
        var titles = {'top':'分布', 'flowOut':'流出', 'flowIn':'流入', 'region':''}; //type选项
        var opt = $this.option;
        if(type in titles)opt.title.text += titles[type];
        if('region'==type){
            opt.series.push(regions);
        }else{
            opt.series.push(points);
        }
        if('flowOut'==type || 'flowIn'==type){
            opt.series.push(lines);
            opt.geo.regions = center;
        }
        return opt;
    };
    
    //拖动选择top5-10
    this.addDragTool = function(){
        var ds = new DragSwitch(id, function(d){
            var dts = $this.option.series[0].data;
            $this.option.series.forEach(function(ser, i){
                if(ser.type=="effectScatter")ser.data = (d < 0.5 ? $this.dataTop5 : $this.dataTop ); 
                if(ser.type=="lines")ser.data = (d < 0.5 ? $this.dataFlow5 : $this.dataFlow ); 
            });
            $this.myEcharts.setOption($this.option);
        });
        ds.setPos(30, 30);      //  设置插件位置
    };
    
    

    /* 调用示例 */
    // 默认流出
    this.showMapFlow = function(){
        var key = (!$this.flowD && 'flowOut') || 'flowIn';
        var opt = $this.getOptionMap(key);
        $this.myEcharts = Init3(opt, $this.id);
    };
    this.showMapFlowOut = function(){
        var opt = $this.getOptionMap('flowout');
        $this.myEcharts = Init3(opt, $this.id);
    };
    this.showMapFlowIn = function(){
        var opt = $this.getOptionMap('flowIn');
        $this.myEcharts = Init3(opt, $this.id);
    };
    this.showMapTop = function(){
        var opt = $this.getOptionMap('top');
        $this.myEcharts = Init3(opt, $this.id);
    };
}
var MG = MapGeo;
//-------------------------------End Class Map--------------------------------------------------------



/*
    人才流动图 - 统一调用
    输入：fDirection: 'flowOut' || 'flowIn'
    流动方向：0-流出，1-流入
*/
function ShowMapFlow(id, data, fDirection, cCity){
    var map = new MapGeo(id, data, fDirection, cCity);
    var key = (!fDirection && 'flowOut') || 'flowIn';
    var option = map.getOptionMap(key);
    map.myEcharts = Init3(option, id);
    map.addDragTool();
}

//流出
function ShowMapFlowOut(id, data, cCity){
    var map = new MapGeo(id, data, 0, cCity);
    var option = map.getOptionMap('flowOut');
    map.myEcharts = Init3(option, id);
    map.addDragTool();
}

//流入
function ShowMapFlowIn(id, data, cCity){
    var map = new MapGeo(id, data, 1, cCity);
    var option = map.getOptionMap('flowIn');
    map.myEcharts = Init3(option, id);
    map.addDragTool();
}

//人才分布图
function ShowMapTop(id, data){
    var map = new MapGeo(id, data, 0);
    map.maxR = 30;
    var option = map.getOptionMap('top');
    map.myEcharts = Init3(option, id);
    map.addDragTool();
}

