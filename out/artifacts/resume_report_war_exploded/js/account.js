define([
    "jquery",
    "underscore",
    "util",
    "select",
    "page",
    "dic",
    "prototype",
    "alertdialog"
    ],function($,_,xy_util,SelectControl,page,dic) {
    return  xy_account = {
        collect_count:0,//收藏条数
        ready: function(){
            this.initSelect();
            this.initDownload({page:1});
            this.initCollect({page:1,force:true});
            this.initDomListener();
        },
        initSelect:function(){
            var me = this;
            var p = {
                domId:'J_industry', //必填
                options: dic.industry,//必填
                isDefaultTitle:false,
                defaultTitle:false,
                defaultVal:$("#J_industry").find(".select-value").val(),
                width: 200,//下拉宽度
                event: {
                    onchange: function(obj, value, text){
                        me.saveIndustry(value);
                    },
                    onload: function(){

                    }
                }
            }
            var selectControl1 = new SelectControl(p);
        },
        saveIndustry: function(value){
            var me = this;
            $.post("/user/modify/industry",{industry:value}, function(result){
                xy_util.restCallback(result, function(data){

                })
            })
        },
        initDownload: function(param){
            var me = this;
            $.post("/report/download/list",{page:param.page} , function(result){
                xy_util.restCallback(result, function(data){
                    if(data.data.length == 0 ){
                        $(".download_table").hide();
                        $(".no_data_download").show()
                        $("#kkpager").hide();
                        return;
                    }

                    var template = _.template($("#J_download_temp").html());
                    $("#download_tbody").html(template({data: data.data}));
                    $(".download_table").show();
                    $(".no_data_download").hide()

                    me.bindTempEvent();

                    $("#kkpager").show();

                    if(param.page == 1){
                        me.initPage(data.count,true,param.page,10);
                    }
                })
            })
        },
        initCollect: function(param){
            var me = this;
            $.post("/report/collect/list",{page:param.page} ,function(result){
                xy_util.restCallback(result, function(data){
                    if(data.data.length == 0 && param.page > 1){
                        me.initCollect({page:--param.page});
                        return;
                    }

                    if(data.data.length == 0 ){
                        $(".collect_table").hide();
                        $(".no_data_collect").show();

                        $("#kkpager2").hide();
                        return;
                    }

                    if( me.collect_count == 0){
                        me.collect_count = data.count;
                    }

                    var template = _.template($("#J_collect_temp").html());
                    $("#collect_tbody").html(template({data: data.data}));
                    $(".collect_table").show();
                    $(".no_data_collect").hide();


                    me.bindTempEvent();

                    $("#kkpager2").show();

                    me.initPage2(me.collect_count,param.force,param.page,10);
                })
            })
        },
        bindTempEvent: function(){
            var me  = this;
            $(".J_del_collect").off().on("click", function(){
                me.delCollect(this);
            })
        },
        delCollect: function(obj){
            var me = this;
            jConfirm("确认删除？", function(isDeal){
                if(isDeal){
                    $.post("/report/collect/del", {report_id:$(obj).attr("data-id")},function(result){
                        xy_util.restCallback(result, function(data){
                            jTip("删除成功！");
                            me.collect_count -- ;
                            me.initCollect({page:$("#kkpager2 .curr").text(),force:false})
                        })
                    })
                }
            })
        },
        initDomListener: function(){
            var me = this;

            $(".tab").on("click", function(e){
                e.preventDefault();
                $("li.active").removeClass("active");
                $(this).parent().addClass("active");
                $(".tab_content").hide();
                $($(this).attr("href")).show();
            })
        },
        initPage: function(all, force,pno,pageCount) {
            var me = this;
            var total = (all%pageCount === 0)?all/pageCount : (all/pageCount + 1);
            // 生成分页
            new page( {
                pagerid:"kkpager1",
                pno : pno,
                // 总页码
                total : total,
                totalRecords :  all,
                mode : 'click',// 默认值是link，可选link或者click
                enforceInit:force,
                click : function(page) {
                    this.selectPage(page);
                    me.initDownload({page:page,force:false})
                }
            });
        },
        initPage2: function(all, force,pno,pageCount) {
            var me = this;
            var total = (all%pageCount === 0)?all/pageCount : (all/pageCount + 1);
            // 生成分页
            new page( {
                pagerid:"kkpager2",
                pno : pno,
                // 总页码
                total : total,
                totalRecords :  all,
                mode : 'click',// 默认值是link，可选link或者click
                enforceInit:force,
                click : function(page) {
                    this.selectPage(page);
                    me.initCollect({page:page,force:false})
                }
            });
        }
    }
})
