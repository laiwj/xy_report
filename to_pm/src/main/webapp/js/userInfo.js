/**
 * Created by Administrator on 2017/5/5.
 */
/**
 * Created by Administrator on 2017/5/4.
 */
define([
    'underscore',
    'beanUtil',
    'validator',
    'SelectControl',
    'util',
    'text!/components/main/dialog.vm',
    'page',
    'colorbox'
], function (_, xy_beanUtil, xy_validator, SelectControl, xy_util, dialog, page) {
    var xy_userInfo = {
        selectMap: {},
        param: '',
        ready: function (param) {
            this.initDomListener();
            this.param = param;
            this.initList({page: 1, user_id: param.id ? param.id : ''});
            if ($("#userinfoType").val() == 3) {
                $("#J_invitedUsers").show();
            }


        },
        initList: function (bean) {
            var me = this;
            xy_util.showAjaxOverlay($(".tabContainer"));
            $.post('/user/list/b', bean, function (res) {
                xy_util.hideAjaxOverlay();
                xy_util.restCallback(res, function (data) {
                    $(".J_account_num").text(data.count);
                    if (data.data.length == 0) {
                        $(".infolist").hide();
                        $(".null-model").show();

                    } else {
                        $(".infolist").show();
                        $(".null-model").hide();
                        var template = _.template($("#userList").html());
                        $(".userList").empty().append(template({data: data.data}));
                        if (bean.page == 1) {
                            me.initPage(data.count, true, 1, 10);
                        }
                    }


                    me.bindTempEvent();


                })
            })
        },
        bindTempEvent: function () {
            var me = this;

            $(".J_power").off().on("click", function () {
                var uid = $(this).attr("data-userid");
                var pl = $(this).attr('_powerList');
                me.boxPower(uid,pl);
            })
            /*确定邀请*/
            $(".J_sure_invited").click(function () {
                $.colorbox.close();
                /* $.post("/user/power/add", {user_id:param.user_id,power:power.join(",")},function(result){
                 xy_util.restCallback(result, function(data){
                 jTip(STATICMSG("ok"));
                 $.colorbox.close();
                 })
                 })*/
            });
        },
        boxPower: function (uid,pl) {
            var me = this;
            var oldpowerlist = pl.split(',');
            var newlist=[];
            $.colorbox({
                inline: true,
                href: "#powerB_dialog",
                innerWidth: 500,
                title: "权限设置",
                initialHeight: 390,
                onOpen: function () {
                    $.each(oldpowerlist,function(index,val){
                        $.each($("#powerB_dialog input[type='checkbox']"), function (i,v) {
                            var boxval  = v.value;
                            if(boxval == val){
                                $(this).attr('checked','checked');
                                newlist.push(val);
                            }

                        });

                    });
                    $("#J_sure_powerB").off().on("click", function () {
                        me.savePower({user_id: uid},newlist);
                    })
                }
            });


        },
        savePower: function (param,oldpowerlist) {
            var me = this;
            var newpower = [];
            $.each($("#powerB_dialog").find("input[type='checkbox']"), function (index) {
                if ($(this).is(':checked')) {
                    newpower.push($(this).attr("value"))
                }

            })
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
            $.post("/user/power/add", {user_id: param.user_id, power_del: remove.join(","), power: add.join(","),source:'b'}, function (result) {
                xy_util.restCallback(result, function (data) {
                    jTip(STATICMSG["ok"]);
                    me.initList({page: 1, user_id: ''});
                    $.colorbox.close();
                })
            })
        },
        initDomListener: function () {
            $("#dialog").append(dialog);
            /*邀请用户*/
            $("#J_invitedUsers").colorbox({
                inline: true,
                href: "#invitedUsers_dialog",
                title: '邀请用户',
                innerWidth: 500,
                initialHeight: 100,
                onOpen: function () {
                    $("#user_msg").val('http://localhost:8081/register?id=' + $("#userinfoshortId").val());
                }
            });
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
                        user_id: me.param.id ? me.param.id : ''
                    }
                    me.initList(param);
                }
            });
        }

    }
    return xy_userInfo;
})