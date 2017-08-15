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
    fDirection - 流动方向， 0-'flowOut' || 1-'flowIn' || 2-'flow'
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
    d3.v4.min.js        //d3绘图引擎
    dragTool.js         //addDragTool()
 */

function MapGraph(id, data, fDirection, cCity) {
    if (id == undefined || data == undefined) {
        console.error("实例化错误");
        return;
    }
    var $this = this;
    this.id = id;           //图形容器div
    this.maxR = 13;         //圆最大半径
    this.maxW = 3;          //线条宽度
    this.flowD = fDirection || 0;     //流动方向：0-流出，1-流入
    this.zoom = 8;          //地图区域放大比例
    this.data = null;
    this.city = cCity || "";
    this.province = "";
    this.center = [];
    this.myEcharts = null;
    this.level = 10;        //显示Top级别， 数据过滤

    this.dragTool = null;   //拖动控件
    this.flowPath = 'path://M5,1 L8,10 L5,6 L2,10 L5,2';     //箭头样式 - 自带arrow太难看
    this.color = {'points':'#397ACA', 'hover':'#EA899A', 'lines':'#397ACA', 'center':'#FFDEAD',
        'area':'#eee', 'borderColor':'#999', 'flow':'#fff'};
    var color = this.color;

    //地图区域及高亮颜色
    var itemStyle = {
        normal: { areaColor: color.area, borderColor: color.borderColor, borderWidth: 1, }, //区域样式
        emphasis: { areaColor: color.center }
    };

    //核心配置项，区域+点+线
    this.regions = {
        name: '中国',type: 'map',mapType: 'china', selectedMode : false,
        label: { normal: { show: true }, emphasis: {show: true}}, itemStyle: itemStyle,
        center: [], zoom: $this.zoom, data:[], silent:true //禁止鼠标事件
    };

    //中心区域
    this.changeCity = function (city){
        $this.city = city || '成都';
        $this.province = getProvinceByCity($this.city);
        $this.center = [{ name:$this.province, selected:true, label: { emphasis: {show:false}, normal:{ show:true } } },];
    };

    this.getOption = function(){
        return {
            title : { text: '', subtext: '', left: 'center', textStyle : { color: '#fff' } },
            tooltip: { trigger: 'item', formatter:function(p){
                if(p.componentSubType!="effectScatter")return;  //暂时不做线条提示
                return p.name + "<br>人数：" + p.data.value[2] + " "; }
            },
            geo: { map: 'china', label: { emphasis: { show: false } }, selectedMode:false, roam: true,
                itemStyle: itemStyle, silent:true, zoom:1.2,
            },
            series: []
        };
    };

    //数据规范化，添加经纬度信息
    this.convertData = function (dt) {
        return dt.map(function(d, i){
            cout(d.fromName);
            var fromCoord = getCityPosEx(d.fromName), toCoord = getCityPosEx(d.toName);
            var line = { fromName: d.fromName, toName: d.toName, value: d.value, coords: [fromCoord, toCoord]};
            if (fromCoord && toCoord) return line;
        });
    };

    this.loadLevel = function(level){
        level = level || 5;
        $this.option.series.forEach(function(ser, i){
            if(ser.type=="effectScatter")ser.data = (level==$this.dataTop.length) ? $this.dataTop : $this.dataTop.slice(0, level);
            if(ser.type=="lines")ser.data = (level==$this.dataFlow.length) ? $this.dataFlow : $this.dataFlow.slice(0, level);
        });
        $this.myEcharts.setOption($this.option);
    };
    this.loadLevel5 = function(){ this.loadLevel(5); };
    this.loadLevel10 = function(){ this.loadLevel(10); };

}



function MapGeo(id, data, fDirection, cCity, callbackTop){
    MapGraph.call(this, id, data, fDirection, cCity);
    var $this = this;
    this.callbackTop = callbackTop;

    //this.color = {'points':'#397ACA', 'hover':'#EA899A', 'lines':'#397ACA', 'center':'#C4DCFC', 'area':'#E6ECF2', 'borderColor':'#F5F6F8', 'flow':'#fff'};
    function mulitFlow(dt){ return dt.length>0 && ('fromName' in dt[0]); }

    //兼容单中心流动 和 多源流动
    this.formatData = function (dt, city, fd){
        if(mulitFlow(dt))return dt;
        city = city || $this.city;
        fd = fd || $this.flowD;
        return dt.map(function(d){
            var line = fd===1 ? { fromName: d.name, toName: city } : { fromName: city, toName: d.name};
            line['value'] = d.value;
            return line;
        });
    };

    this.setData = function (dt){
        //遇到双向流动，不知以流入还是流出量排序为好。
        $this.data = dt.sort(function(a, b){ return b.value - a.value; });
        $this.maxV = dt.reduce(function(a, b){ return a>b.value ? a : b.value; }, 0);
        $this.scaleS = function(v){ return Math.ceil($this.maxR * v / $this.maxV); };   //线性映射，控制节点大小
        $this.scaleW = function(v){ return Math.ceil($this.maxW * v / $this.maxV); };   //线性映射，控制节点大小

        var mf = mulitFlow(dt);
        //源数据 - 点
        var dataTop = data.map(function (d) {
            var name = mf ? d.toName : d.name, value = mf ? $this.maxV : d.value;
            var pos = getCityPosEx(name), line = { name: name, value: pos.concat([value])};
            return line;
        });
        $this.dataTop = dataTop.sort(function(a, b){ return b.value[2] - a.value[2]; });
        $this.dataFlow = $this.convertData($this.formatData(dt));
        cout($this.formatData(dt));
    };

    //给流动线条加宽度，在setData()和getSeriesLine()之后调用
    this.setLineWidth = function (){
        $this.dataFlow.forEach(function(d){
            d['lineStyle'] = {'normal':{'width': $this.scaleW(d.value) }};
        });
        //$this.lines[0]['effect']['symbolSize'] = 3;
        $this.lines[1]['effect']['symbolSize'] = 12;
    };

    //点
    this.getSeriesPoint = function (){
        var points ={
            type: 'effectScatter', coordinateSystem: 'geo', zlevel: 2,
            rippleEffect: { brushType: 'stroke' },
            label: { normal: { show: true, position: 'right', formatter: '{b}' } },
            symbolSize: function (val) { return (val==undefined && 1) || $this.scaleS(val[2]); },
            itemStyle: { normal: { color: $this.color.points }, emphasis:{ color:$this.color.hover,opacity:0.8 }}, //标注点样式
            data: $this.dataTop
        };
        $this.points = points;
        return points;
    };

    //线
    this.getSeriesLine = function (){
        var lines = [{
                type: 'lines', zlevel: 1,
                effect: { show: true, period: 6, trailLength: 0.7, color: $this.color.flow, symbolSize: 3},
                lineStyle: { normal: { color: $this.color.lines, width: 0, curveness: 0.2 } },
                data: $this.dataFlow
            },
            {
                type: 'lines', zlevel: 2, symbol: ['none', 'arrow'],
                effect: { show: true, period: 6, trailLength: 0, symbol: $this.flowPath, symbolSize: 8 },
                lineStyle: { normal: { width: 1, opacity: 0.6, curveness: 0.2, color: $this.color.lines}},   //线条样式
                data: $this.dataFlow
        }];
        $this.lines = lines;
        return lines;
    };

    //获取配置项
    this.getOptionMap = function(type){
        type = type || "top";
        var titles = {'top':'分布', 'flowOut':'流出', 'flowIn':'流入', 'flow':'流动', 'region':''}; //type选项
        var opt = $this.getOption();
        if(type in titles)opt.title.text += titles[type];
        opt.title.text = "";    //禁用标题
        if('region'==type){
            opt.series.push($this.regions);
        }else{
            opt.series.push($this.points);
        }
        if('flowOut'==type || 'flowIn'==type || 'flow'==type){
            opt.series.push($this.lines[0]);
            opt.series.push($this.lines[1]);
            opt.geo.regions = $this.center;
        }
        return opt;
    };


    //拖动选择top5-10
    this.addDragTool = function(option){
        $this.dragTool = new DragSwitch(id, function(d){
            $this.level = d < 0.5 ? 5 : 10;
            $this.loadLevel($this.level);
        });
        $this.dragTool.setPos(30, 30);      //  设置插件位置
    };

    //返回按钮
    this.addReturnTool = function(callback){
        $this.returnIcon = new ReturnTool($this.id, function(){
            $this.showMapTop();

            if(callback!=undefined)callback();
        });
    };

    this.returnIcon = null;
    this.option = this.getOption();
    this.changeCity(cCity);
    this.setData(data);
    this.getSeriesPoint();
    this.getSeriesLine();


    //---------------------------------------内部调用接口----------------------------------------
    /* 调用示例 */
    // 默认流出
    this.showMapFlow = function(key, level, useWidth){
        key = key || ($this.flowD==0 && 'flowOut') || ($this.flowD==1 && 'flowIn') || ($this.flowD==2 && 'flow');
        if(useWidth!=undefined)$this.setLineWidth();
        var opt = $this.option = $this.getOptionMap(key);
        $this.myEcharts = Init3(opt, $this.id);

        $this.addDragTool(opt);
        if(level!=undefined && level<data.length){
            $this.loadLevel(level);
            $this.dragTool.setLevel(level/data.length);
        }
    };

    this.showMapFlowOut = function(level){
        this.showMapFlow('flowOut', level);
    };
    this.showMapFlowIn = function(level){
        this.showMapFlow('flowIn', level);
    };

    this.showMapTop = function(callback, level){
        if(callback!=undefined) $this.callbackTop = callback;
        $this.option = $this.getOptionMap('top');   //top是底层图
        $this.myEcharts = Init3($this.option, $this.id);

        $this.myEcharts.on("click", function(p){
            if(p.componentSubType!=="effectScatter") return ;
            var city = p.name;
            cout(city);

            if($this.callbackTop!==undefined)$this.callbackTop(city, $this);
        });

        $this.addDragTool($this.option);
        if(level!=undefined && level<data.length){
            $this.dragTool.setLevel(level/data.length);
            $this.loadLevel(level);
        }
    };

}
var MG = MapGeo;
//-------------------------------End Class Map--------------------------------------------------------



/*
    人才流动图 - 统一调用
    输入：fDirection: 'flowOut' || 'flowIn'
            useWidth:线条是否使用不同宽度
    流动方向：0-流出，1-流入
*/
function ShowMapFlow(id, data, fDirection, cCity, level){
    var map = new MapGeo(id, data, fDirection, cCity);
    map.showMapFlow(level);


    return map;
}

function ShowMapFlowEx(id, data, cCity, level, useWidth){
    var map = new MapGeo(id, data, "flow", cCity);
    map.showMapFlow("flow", level, useWidth);


    return map;
}
//流出
function ShowMapFlowOut(id, data, cCity, level){
    var map = new MapGeo(id, data, 0, cCity);
    map.showMapFlowOut(level);

    //console.log(map.level);
    //map.loadLevel5();

    return map;
}

//流入
function ShowMapFlowIn(id, data, cCity, level){
    var map = new MapGeo(id, data, 1, cCity);
    map.showMapFlowIn(level);

    return map;
}

//人才分布图
function ShowMapTop(id, data, callback, level){
    var map = new MapGeo(id, data, 0);
    map.maxR = 30;
    map.showMapTop(callback, level);

    return map;
}

