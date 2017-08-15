/**
 * Created by Administrator on 2017/5/9.
 */


/*
 功能: 获取配置项
 输入：
    type - 'need' || 'index'
    data - [{name:'上海',value:85},{name:'广州',value:90}]
 */
function getOptionBar(type, data){
    type = type || 'need';
    var title = {'need':"需求量", 'index':"供需指数"}[type];
    var colorCfg = {'need':['#09c5db', '#04e0cf'], 'index':['#3485D7', '#36DBF9']};
    var GDC = function(cls){    //颜色梯度转换
        var cg = [{offset: 0, color: cls[0]}, {offset: 1, color: cls[1]}];
        return new echarts.graphic.LinearGradient( 0, 0, 1, 0, cg )
    };

    //var colorGradient = {'need':[{offset: 0, color: '#3485D7'}, {offset: 1, color: '#36DBF9'}],
    //    gdc = new echarts.graphic.LinearGradient( 0, 0, 1, 0, colorGradient );
    var color = {'need':["#C3EDE9", GDC(colorCfg.need)], 'index':["#BBD1E8", GDC(colorCfg.index)]}[type];

    var dt = data.map(function(d){ return d.value; });
    var option = {
        tooltip: { trigger: 'item', textStyle:{ fontFamily:"楷体" },
            formatter:function(p){ return p.name + "<br>" + title + "：" + p.data} },
        grid:{left:100},
        xAxis: { type: 'value',  boundaryGap: [0, 0.01], axisLine:{ lineStyle:{ color:'#d3d6db'}},
            axisLabel:{textStyle:{color:'#000'}},
        },
        yAxis: { type: 'category', axisLine:{ lineStyle:{ color:'#d3d6db'}}, axisLabel:{textStyle:{color:'#000'}},
            data: data.map(function(d){ return d.name; })
        },
        series: [ { type: 'bar', data: dt,
            itemStyle:{normal:{color:color[0] }, emphasis:{color:color[1] }}
        } ]
    };
    return option;
}

//需求量图
function ShowBarNeed(id, data){
    var option = getOptionBar('need', data);
    Init3(option, id);
}

//供需指数图
function ShowBarIndex(id, data){
    var option = getOptionBar('index', data);
    Init3(option, id);
}
