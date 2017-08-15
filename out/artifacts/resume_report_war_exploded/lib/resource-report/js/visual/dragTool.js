
/*
依赖：d3.v3.min.js

功能：绘制拖动开关小插件
输入：id - 父容器ID, callback - 拖动触发事件
回掉函数参数： 当前阈值的线性比例，0-1之间
技术要点：多元素组合一起拖动，拖动控制，防止频繁触发拖动事件
调用： example
    var ds = new DragSwitch("main", function(d){
        cout(d);
    });
*/


function DragSwitch(id, callback) {
    "use strict";
    
    var $this = this;
    this.parentId = id;
    var svg = d3.select("#" + $this.parentId).append("svg") //.style("z-index", 999)
        .attr("width", 100).attr("height", 120).style("position", "absolute").style("top", "30px");
    
    this.svg = svg;
    this.value = 1;
    //核心配置，其它位置信息依此计算
    var cfg = {'x': 60, "y": 10, 'width': 14, 'height': 80};

    //计算内部各元素位置、大小、偏移信息
    var vertexs = [{'x': cfg.x, 'y': cfg.y}, {'x': cfg.x + cfg.width, 'y': cfg.y},	//类三角形各顶点
        {'x': cfg.x + cfg.width / 2 + 2, 'y': cfg.y + cfg.height},
        {'x': cfg.x + cfg.width / 2 - 2, 'y': cfg.y + cfg.height},
        {'x': cfg.x, 'y': cfg.y}];
    
    var rectT = { 'x': cfg.x - 3, 'width': cfg.width + 6, 'height': 11, 'y': cfg.y + 5},
        maxRH = cfg.height - rectT.height * 2 + 2;
    
    var lineI = [];		//矩形内的三条线
    for (var i=1; i<4; i++){
        lineI.push({'x1': rectT.x + 5, 'y1': rectT.y + 2 + i * 2, 'x2': rectT.x + rectT.width - 5, 'y2': rectT.y + 2 + i * 2 });
    }
        
    var texts = [{'x': cfg.x - 40, 'y': cfg.y + 13, 'text':'Top10'},
                 {'x': cfg.x - 36, 'y': cfg.height + cfg.y - 7, 'text': 'Top5'}];
    var dots = [{'x': cfg.x - 10, 'y': cfg.y + 10 }, {'x': cfg.x - 10, 'y': cfg.height + cfg.y - 10 }];

    //拖动功能
    var dragMove = function (d) {
        var y = d3.event.y - this.getBBox().y - rectT.height/2;
        y = (y<0 && 1) || (y>maxRH && maxRH) || y;
        y = (y<maxRH/2 && 1) || maxRH;
        d3.select(this).attr("transform", "translate(0," + (y-1) + ")");
        $this.value = 1 - (y-1)/(maxRH-1);
    };
    var dragStop = function(d){ if(callback)callback($this.value); };
    var drag = d3.behavior.drag().on("drag", dragMove).on("dragend", dragStop);


    function addMain(){
        //主轴
        var linfFunc = d3.svg.line()
                .x(function(d){ return d.x; })
                .y(function(d){ return d.y; })
                .interpolate("linear");
        //画三角形
        var line = svg.append("path")
                .attr("d", linfFunc(vertexs))
                .attr("stroke", "#ccc")
                .attr("stroke-width", 2)
                .attr("fill", "#2F7FDE");

        //刻度文字
        svg.selectAll("text")
                .data(texts)
                .enter()
                .append("text")
                .attr("font-size", 10)
                .attr("fill", "#444")
                .attr("font-family","楷体")
                .attr("x", function(d){ return d.x; })
                .attr("y", function(d){ return d.y; })
                .text(function(d){ return d.text; });
        //刻度点
        svg.selectAll("circle")
                .data(dots)
                .enter()
                .append("circle")
                .attr("fill", "#444")
                .attr("cx", function(d){ return d.x; })
                .attr("cy", function(d){ return d.y; })
                .attr("r", 2);
        return svg;
    }

    function addRect(){
        //手柄
        var gr = svg.append("g").call(drag)
        gr.append("rect")
                .attr("x", rectT.x)
                .attr("y", rectT.y)
                .attr("width", rectT.width)
                .attr("height", rectT.height)
                .attr("stroke", "#ccc")
                .attr("stroke-width", 1.5)
                .attr("fill", "#fff");
        //内部条纹
        gr.selectAll("line")
                .data(lineI)
                .enter()
                .append("line")
                .attr("x1", function(d){ return d.x1; })
                .attr("y1", function(d){ return d.y1; })
                .attr("x2", function(d){ return d.x2; })
                .attr("y2", function(d){ return d.y2; })
                .attr("fill","black")
                .attr("stroke", "#276EC7");
        return gr;
    }
    
    //调整控件位置
    this.setPos = function(x, y){ $this.svg.style("left", x + "px").style("top", y + "px"); };
    
    addMain();
    addRect();
    return this;
}    
//-----------------------End Class----------------------------------------
