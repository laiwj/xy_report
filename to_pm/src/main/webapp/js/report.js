/**
 * Created by Administrator on 2017/5/4.
 */
define([
    'underscore',
    'SelectControl',
    'dic',
    'beanUtil',
    'util',
    'text!/components/main/dialog.vm',
    'colorbox'
],function(_,SelectControl,dic,xy_beanUtil,xy_util,dialog){
    var xy_dashboard = {
        selectMap : {},
        ready:function(){

            $("body").append(dialog);

            this.initDomListener();
            this.initSelect();

            //初始化数据
            this.analysisData();

        },
        initDomListener:function(){
            var me = this;
            $("#J_analysis").off().on("click", function(){
               // xy_util.lockScreen();
                me.analysisData();
            })



            $("#report_info").off().on("blur", function(){
                me.saveDesc();
            })

            $("#J_data_disturb").on("click", function(){
                me.createHTML();
                $.colorbox({
                    inline:true,
                    href:"#data_disturb_dialog",
                    innerWidth:400,
                    innerHeight:400,
                    title:'数据干预',
                    onComplete:function(){
                        }
                });
            })


        },
        createHTML: function(){
            var data = JSON.parse($("#J_charts_data").val());

            var temp = _.template($("#data_disturb_temp").html());
            var _html = temp({data:data});

            $("#data_disturb_dialog").find(".control-group-warp").html(_html);

            this.bindTempEvent();
        },
        bindTempEvent: function(){
            var me = this;
            $("#J_save_disturb").on("click", function(){
                me.saveDisturb();
            })
        },
        saveDisturb: function(){
            var me = this;
            var arr = [];
            $.each($("#data_disturb_dialog").find(".control-group"), function(){
                arr.push({name:$(this).find(".name").text(),value:$(this).find(".data").val()})
            })

            var data = this.getBean();
            var tab = dic["chartsname"][data.tab];



            xy_util.lockScreen();
            var bean = {data:JSON.stringify(arr),data_id:me.g_data.data._id};

            $.post("/api/data/cheat",bean, function(result){
                xy_util.hideLock();
                xy_util.restCallback(result, function(data){
                    jTip(STATICMSG["setOK"]);
                    $.colorbox.close();
                    $("#J_charts_data").val(JSON.stringify(arr));
                    $("#report_iframe").attr("src","/lib/resource-report/"+tab+".html");
                })
            })



        },
        saveDesc: function(obj){
            var me = this;
            var user_id = $("#report_info").attr("user_id")?$("#report_info").attr("user_id"):$("#userinfoId").val();
            var bean = {};
            if(obj){
                var param = {
                    api_url:$(obj).attr("api_url"),
                    user_id:$(obj).attr("user_id"),
                    report_info:$(obj).parent().find(".edit_desc_input").val(),
                    params:JSON.stringify(me.g_data.data.params)
                }
                bean = param;
                console.log(bean);
                //bean["api_url"] = bean["data_url"]
            }else{
                bean = {
                    api_url:me.g_data.data.api_url,
                    user_id:user_id,
                    report_info:$("#report_info").val(),
                    params:JSON.stringify(me.g_data.data.params)};
            }

            $.post("/api/info/write",bean, function(result){
                xy_util.restCallback(result, function(data){
                    ;
                    jTip(STATICMSG["ok"]);
                    me.resetEditDesc(obj,bean.report_info);
                })
            })
        },
        editDesc: function(obj){

            var _value = $(obj)
                            .parents("p")
                            .find(".text_desc")
                            .text();

            $(obj).parents("p").hide();

            var temp = _.template( $("#J_editdesc_temp").html() );
            $(temp({
                    value:_value,
                    api_url:$(obj).attr("api_url"),
                    user_id:$(obj).attr("user_id"),
                    data_id:$(obj).attr("data_id")
                }))
                    .insertAfter($(obj)
                    .parents("p"));

            this.bindCancelAndSave();//模板中的按钮绑定事件
        },
        bindCancelAndSave: function(){
            var me = this;
            $(".J_save_desc").off().on("click", function(){
                me.saveDesc(this);
            })

            $(".J_cancel_desc").off().on("click", function(){
                me.resetEditDesc(this);
            })
        },
        resetEditDesc:function(obj,_text){
            var node = $(obj)
                .parents(".edit_desc_input_warp")
                .parent()
                .find(".text_desc");
            if(_text){
                node.text(_text)
            }
            node.parent().show();

            $(obj)
                .parents(".edit_desc_input_warp")
                .remove();
        },
        analysisData: function(){
            var me = this;
            var param = this.getBean();
            var tab = dic["chartsname"][param.tab];
            xy_util.lockScreen();
            $.post(param.url, param.bean, function(result){
                xy_util.hideLock();
                xy_util.restCallback(result, function(data){
                    me.g_data = data;
                    //数据干预按钮
                    if($("#userinfoType").val() < 3){
                        $("#J_data_disturb").show();
                    }

                    $("#J_charts_data").val(JSON.stringify(data.data.data)).attr("charts_type",param.charts_type);
                    $("#report_iframe").attr("src","/lib/resource-report/"+tab+".html");
                    $("#report_info").attr("api_url",data.data.api_url);
                    if(data.info.length > 0){
                        $("#report_info").attr("user_id",data.info[0].pm_user_id);
                        $("#report_info").attr("data_id",data.info[0]._id);
                        if($("#userinfoType").val() == 3){
                            $("#report_info").val('').val(data.info[0].info);
                        }
                    }else{
                        $("#report_info").attr("data_url","").attr("data_id","").val('');
                    }

                    //普通业务员
                    if($("#userinfoType").val() == 3){
                        $("#report_info").show();
                    }

                    //超管与公司权限
                    if($("#userinfoType").val() < 3){
                        var temp = _.template($("#desc_temp").html());
                        $(".J_analysis_items_warp").html(temp({data:data.info}))
                        $(".J_analysis_items_warp").show();

                        $(".J_edit_desc").off().on("click", function(){
                            me.editDesc(this);
                        })
                    }

                })
            })
        },
        getBean:function(){
            var tab = $("#bean_chartstype").val();
            var bean = {};
            var url = "";
            var charts_type = "";
            switch (tab){
                case "tab1" :
                    bean = xy_beanUtil.creatBean("bean_",[
                        {name:"industry"},
                        {name:"cf"},
                        {name:"type"},
                    ])
                    bean.city="";
                    bean.top=10;
                    url = "/api/talent/distribution";
                    charts_type = bean.cf;
                    break;
                case "tab2" :
                    bean = xy_beanUtil.creatBean("bean_",[
                        {name:"city"},
                        {name:"industry"},
                        {name:"direction"},
                        {name:"cf"},
                        {name:"type"}
                    ])
                    bean.top =10;
                    url = "/api/talent/flow";
                    charts_type = bean.cf;
                    break;
                case "tab3" :
                    bean = xy_beanUtil.creatBean("bean_",[
                        {name:"industry"},
                        {name:"na"},
                        {name:"fp"},
                        {name:"type"}
                    ])
                    bean.top = 5;
                    bean.city = "";
                    url = "/api/talent/exponential";
                    charts_type = bean.na;
                    break;
                default:
                    break;
            }

            return {bean:bean,tab:tab,url:url,charts_type:charts_type};
        },
        initSelect:function(){
            var me = this;
            //标记下拉框初始化
           this.selectMap['J_chartstype'] = new SelectControl({
                domId:'J_chartstype',
                width: 100,
                options:dic.chartstype,
                defaultTitle:false,
                isDefaultTitle:false,
               event: {
                   onchange: function(obj, value, text){
                       me.setTabVisible(value);
                       me.analysisData();
                   },
                   onload: function(){

                   }
               }

            });

            var p = {
                domId:'J_industry', //必填
                options: dic.industry,//必填
                isDefaultTitle:false,
                defaultTitle:false,
                width: 200,//下拉宽度
                event: {
                    onchange: function(obj, value, text){
                        me.analysisData();
                    },
                    onload: function(){

                    }
                }
            }
            this.selectMap['J_industry'] = new SelectControl(p);

            var p1 = {
                domId:'J_direction', //必填
                options: dic.direction,//必填
                isDefaultTitle:false,
                defaultTitle:false,
                width: 200,//下拉宽度
                event: {
                    onchange: function(obj, value, text){
                        me.analysisData();
                    },
                    onload: function(){

                    }
                }
            }
            this.selectMap['J_direction']  = new SelectControl(p1);

            var p2 = {
                domId:'J_city', //必填
                options: dic.hotCity,//必填
                isDefaultTitle:false,
                defaultTitle:false,
                width: 200,//下拉宽度
                event: {
                    onchange: function(obj, value, text){
                        me.analysisData();
                    },
                    onload: function(){

                    }
                }
            }
            this.selectMap['J_city']= new SelectControl(p2);

            var p3 = {
                domId:'J_type', //必填
                options: dic.timeType,//必填
                isDefaultTitle:false,
                defaultTitle:false,
                width: 200,//下拉宽度
                event: {
                    onchange: function(obj, value, text){
                        me.analysisData();
                    },
                    onload: function(){

                    }
                }
            }
            this.selectMap['J_type'] = new SelectControl(p3);

            var p4 = {
                domId:'J_cf', //必填
                options: dic.cityPosition,//必填
                isDefaultTitle:false,
                defaultTitle:false,
                width: 200,//下拉宽度
                event: {
                    onchange: function(obj, value, text){
                        me.analysisData();
                    },
                    onload: function(){

                    }
                }
            }
            this.selectMap['J_cf'] = new SelectControl(p4);

            var p5 = {
                domId:'J_na', //必填
                options: dic.na,//必填
                isDefaultTitle:false,
                defaultTitle:false,
                width: 200,//下拉宽度
                event: {
                    onchange: function(obj, value, text){
                        me.analysisData();
                    },
                    onload: function(){

                    }
                }
            }
            this.selectMap['J_na'] = new SelectControl(p5);

            var p6 = {
                domId:'J_fp', //必填
                options: dic.fp,//必填
                isDefaultTitle:false,
                defaultTitle:false,
                width: 200,//下拉宽度
                event: {
                    onchange: function(obj, value, text){
                        me.analysisData();
                    },
                    onload: function(){

                    }
                }
            }
            this.selectMap['J_fp'] = new SelectControl(p6);


        },
        setTabVisible: function(tab){
            $(".report_tab").hide();
            $(".report_tab[for-tab*="+tab+"]").show();
        }
    }
    return xy_dashboard;
})