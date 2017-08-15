/**
 * Created by Administrator on 2017/5/8.
 */






function set(){this.data={};this.keys=[];this.add=function(e){if(!this.data.hasOwnProperty(e))this.keys.push(e);this.data[e]=1;};}
function cout(obj){ return obj; }



function Init3(option, id){
    var myChart = echarts.init(document.getElementById(id), 'dark');
    myChart.setOption(option);
    myChart.hideLoading();
    return myChart;
}




