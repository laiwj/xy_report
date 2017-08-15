define([
        "jquery",
        "underscore",
        "select",
        "dic",
        "beanUtil",
        "util",
        "alertdialog"],function($,_,SelectControl,dic,xy_beanUtil,xy_util) {
    return  xy_talentdistribution = {
        ready: function(){
            this.initSelect();
            this.initDomListener();

            //初始化数据
            this.analysisData();
        },
        initSelect: function(){
            var me = this;
            var p = {
                domId:'J_industry', //必填
                options: dic.industry,//必填
                isDefaultTitle:false,
                defaultTitle:false,
                width: 200,//下拉宽度
                event: {
                    onchange: function(obj, value, text){
                    },
                    onload: function(){

                    }
                }
            }
            var selectControl = new SelectControl(p);

            var p1 = {
                domId:'J_function', //必填
                options: dic.cityPosition,//必填
                isDefaultTitle:false,
                defaultTitle:false,
                width: 200,//下拉宽度
                event: {
                    onchange: function(obj, value, text){
                    },
                    onload: function(){

                    }
                }
            }
            var selectControl = new SelectControl(p1);

            var p3 = {
                domId:'J_type', //必填
                options: dic.timeType,//必填
                isDefaultTitle:false,
                defaultTitle:false,
                width: 200,//下拉宽度
                event: {
                    onchange: function(obj, value, text){
                    },
                    onload: function(){

                    }
                }
            }
            var selectControl = new SelectControl(p3);

        },
        initDomListener: function(){
            var me = this;
            $("#J_analysis").off().on("click", function(){
                me.analysisData();
            })

            $("#J_collect").off().on("click", function(){
                me.collect();
            })

            $("#J_download").off().on("click", function(){
                me.download();
            })

        },
        getBean: function(){
            var bean = xy_beanUtil.creatBean("bean_",[
                {name:"industry"},
                {name:"cf"},
                {name:"type"},
               // {name:"top"},
            ])

            bean.top = 10;
            bean.city="全国";

            return bean;
        },
        analysisData: function(){
            var me = this;
            var bean = this.getBean();

            $.post("/api/talent/distribution", bean, function(result){
                xy_util.restCallback(result, function(data){
                    me.g_data = data;
                    if(data.info.length > 0 ){
                        $(".charts_desc_p").text(data.info[0].info).show();
                    }
                    $("#J_charts_data").val(JSON.stringify(data.data.data)).attr("charts_type",$("#bean_cf").val());
                    $("#report_iframe").attr("src","/lib/resource-report/talentdistribution.html")
                })
            })

        },
        collect: function(){
            var me = this;
            var bean = this.getBean();
            var name = bean.industry+"从业人员的"+dic["cityPosition"][bean.cf]+dic["top"][bean.top];
            var data = {report_name:name,info_id:"",data_id:me.g_data.data._id};
            if(me.g_data.info.length > 0 ){
                data["info_id"] = me.g_data.info[0]._id;
            }

            $.post("/report/collect", data, function(result){
                xy_util.restCallback(result, function(data){
                    //jTip("<div class='firstOne'>收藏成功！</div>"+"<div class='secOne'>您可以在您的账户设置中查看下载历史</div>");
                    jTip({firstOne:'收藏成功！',secOne:'您可以在您的账户设置中查看下载历史'});

                })
            })
        },
        download: function(){
            var me = this;
            var bean = this.getBean();
            var name = bean.industry+"从业人员的"+dic["cityPosition"][bean.cf]+dic["top"][bean.top];
            var data = {report_name:name,info_id:"",data_id:me.g_data.data._id};
            if(me.g_data.info.length > 0 ){
                data["info_id"] = me.g_data.info[0]._id;
            }
            //下载文件不弹出新窗口
            var form = $("<form>");//定义一个form表单
            form.attr("style", "display:none");
            form.attr("target", "");
            form.attr("method", "post");
            form.attr("action", "/report/download");


            $.each(data, function(k,v){
                var input = $("<input>");
                input.attr("type", "hidden");
                input.attr("name", k);
                input.attr("value", v);
                form.append(input);
            })

            $("body").append(form);//将表单放置在web中
            form.submit();//表单提交*/
            form.remove();
        }
    }
})
