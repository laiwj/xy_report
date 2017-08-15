
define([
    'underscore',
    'beanUtil',
    'validator',
    'text!/components/main/dialog.vm',
    'util',
    'page',
    'colorbox'
],function(_,xy_beanUtil,xy_validator,dialog,xy_util,page){
    var xy_account = {
        selectMap : {},
        list:{
            page: 1,
            force: true,
            user_id:''
        },
        ready:function(){
            this.initDomListener();
            this.initList(this.list);
        },
        initList:function(obj,objDom){

            var me =this;
            xy_util.showAjaxOverlay($(".tabContainer"));
            $.post('/user/list',obj,function(res){
                xy_util.hideAjaxOverlay();
                xy_util.restCallback(res,function(data){
                        if(data.data.length == 0){
                            if(obj.user_id){
                                objDom.text('暂无数据');
                                me.list.user_id = '';
                                me.list.page = 1;
                            }else{
                                $(".table_li").hide();
                                $(".null-model").show();
                            }
                        }else{
                            $(".table_li").show();
                            $(".null-model").hide();
                            if(obj.user_id){
                                var template = _.template($("#smailList").html());
                                objDom.after(template({data : data.data}));
                                me.list.user_id = '';
                                me.list.page = 1;
                            }else{
                                $(".J_account_num").text(data.count);
                                if($("#userinfoType").val() ==2){
                                    $(".company_model").show();
                                    $(".admin_model").hide();
                                    var template = _.template($("#userList").html());
                                    $(".userList").empty().append(template({data : data.data}));
                                }else if($("#userinfoType").val() ==1){
                                    $(".company_model").hide();
                                    $(".admin_model").show();
                                    var template = _.template($("#bigList").html());
                                    $(".biglist").empty().append(template({userinfoType:$("#userinfoType").val(),data : data.data}));
                                }
                                if (obj.page == 1) {
                                    me.initPage(data.count, true, 1, 10);
                                }
                            }




                        }
                        me.domListener();


                })
            })
        },
        domListener:function(){
            var me = this;
            /*展开子账号*/
            $(".J_click_pull").off().on('click',function(e){
                var objDom = $(this).parents('ul').next('.pull_model').find('.smaillList');
                var pull = $(this).parents('ul').next('.pull_model');
                if($(pull).is(":hidden")){
                    $(pull).slideDown();
                    me.list.user_id = $(this).attr('_id');
                    me.list.page = '';
                    me.initList(me.list,objDom);
                }else{
                    objDom.nextAll().remove();
                    $(pull).slideUp();
                }

            });
            /*创建账号*/
            $(".J_create_account").off().on('click',function(){
                me.addAcountBox($(this).attr('_title'),$(this).attr('_type'));
            });
            $(".J_power").off().on("click", function () {
                var uid = $(this).attr("data-userid");
                var pl = $(this).attr('_powerList');
                me.boxPower(uid,pl);
            })

        },
        boxPower: function (uid,pl) {
            var me = this;
            var oldpowerlist = pl.split(',');
            var newlist=[];
            $.colorbox({
                inline: true,
                href: "#powerPM_dialog",
                innerWidth: 500,
                title: "权限设置",
                initialHeight: 390,
                onOpen: function () {

                    $.each(oldpowerlist,function(index,val){
                        $.each($("#powerPM_dialog input[type='checkbox']"), function (i,v) {
                            var boxval  = v.value;
                            if(boxval == val ){
                                $(this).attr('checked','checked');
                                newlist.push(val);
                            }


                        });

                    });
                    $("#J_sure_powerPM").off().on("click", function () {
                        me.savePower({user_id: uid},newlist);
                    })
                }
            });


        },
        savePower: function (param,oldpowerlist) {
            var me = this;
            var newpower = [];
            $.each($("#powerPM_dialog").find("input[type='checkbox']"), function (index) {
                if ($(this).is(':checked')) {
                    newpower.push($(this).attr("value"))
                }

            });
            var contactArr = [];
            $.each(newpower, function(i){
                if($.inArray(newpower[i] , oldpowerlist) > -1){
                    contactArr.push(newpower[i]);
                }
            })

            var remove = [];
            $.each(oldpowerlist, function(i){
                if($.inArray(oldpowerlist[i] , contactArr) == -1){
                    remove.push(oldpowerlist[i]);
                }
            })
            var add = [];
            $.each(newpower, function(i){
                if($.inArray(newpower[i] , contactArr) == -1){
                    add.push(newpower[i]);
                }
            })


            $.post("/user/power/add", {user_id: param.user_id, power_del: remove.join(","), power: add.join(","),source:'pm'}, function (result) {
                xy_util.restCallback(result, function (data) {
                    jTip(STATICMSG["ok"]);
                    me.initList(me.list);
                    $.colorbox.close();
                })
            })
        },
        addAcountBox:function(txt,type){
            $.colorbox({
                inline: true,
                href: "#addeAccount_dialog",
                title: txt,
                innerWidth: 500,
                initialHeight: 390,
                onOpen:function(){
                    $('.J_sure_create').attr('_type',type);
                }
            });
        },
        initDomListener:function(){
            var me = this;
            $("#dialog").append(dialog);

            /*确定添加账号*/
            $(".J_sure_create").click(function(){

                me.addAcount($('.J_sure_create').attr('_type'));
            });

        },
        addAcount:function(type){
            var me =this;
            var result = xy_validator.autoValidator($('#addeAccount_dialog'));
            if( !result  ){
                return false;
            }
            if (me.isSumbit) {
                return;
            }

            var bean = xy_beanUtil.creatBean('user_',[
                {name : 'username'},
                {name : 'account'},
                {name : 'password'}
            ]);
            me.isSumbit = true;

            $.post('/user/regist',bean,function(res){
                me.isSumbit = false;
                xy_util.restCallback(res,function(data){
                    if(res.code == 0){
                        jTip('添加成功');
                        if(type == 0){
                            me.initList(me.list);
                        }
                    }else{
                        alertDIV(res.msg);
                    }
                    $.colorbox.close();
                })
            })
        },
        initPage: function (all, force, pno, pageCount) {
            var me = this;
            var total = (all % pageCount === 0) ? all / pageCount : (all / pageCount + 1);
            // 生成分页
            new page({
                pagerid: "kkpager",
                pno: pno,
                // 总页码
                total: total,
                totalRecords: all,
                mode: 'click',// 默认值是link，可选link或者click
                enforceInit: force,
                click: function (page) {
                    this.selectPage(page);
                    var param = {
                        force: false,
                        page: page,
                        user_id:''
                    }
                    me.initList(param);
                }
            });
        }

    }
    return xy_account;
})