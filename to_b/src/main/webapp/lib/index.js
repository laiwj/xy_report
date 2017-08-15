var g_breadcrumd = window.g_breadcrumd = [];
require(['jquery',"jquery-cookie", 'listener','validator', 'util', 'resultCode','STATICMSG','dic', 'router', 'alertdialog' ],
    function($,jquerycookie, xy_listener, xy_validator, util, resultCode, STATICMSG,dic){

    $("#navigation").load('/navigation');
    $("#toolbar").load('/toolbar', function(){
         listener = xy_listener;
         xy_listener.initDefaultListener();
         xy_listener.initErrorListerner();
         xy_validator.initAutoValidator();
         resultCode = resultCode;
         STATICMSG = STATICMSG;
         dic = dic;
         xy_util = util;
         alertDIV = xy_util.alertDIV;
         confirmDIV = xy_util.confirmDIV;
         tipsDIV = xy_util.tipsDIV;
    });
});

//* detect touch devices 
function is_touch_device() {
  return !!('ontouchstart' in window);
}
//生成全局面包屑数据
function setBreadcrumbData(obj){
    var mark = true;
    $.each(g_breadcrumd, function(i){
        if(g_breadcrumd[i].href == obj.href){
            mark = false;
            return;
        }
    })
    if(mark){
        g_breadcrumd.push(obj);
    }
    setBreadcrumdCookie();
}

function setBreadCrumd(){
        var me = this;
        $("#breadcrumb").attr("data","0");
        g_breadcrumd = getBreadcrumdCookie() ? JSON.parse( getBreadcrumdCookie()) : [];
         $(".breadCrumb-nav").remove();
        if(g_breadcrumd.length == 0){
            return ;
        }
        var arr = [];
        arr.push('<nav class="breadCrumb-nav"><div id="jCrumbs" class="breadCrumb module" style="margin:0"><ul>');
        $.each(g_breadcrumd, function(i){
            if(g_breadcrumd[i].href){
                arr.push('<li><a href="'+g_breadcrumd[i].href+'">'+g_breadcrumd[i].text+'</a></li>');
            }
        })
        arr.push('<li class="active">当前位置</li></ul></div></nav>');
        $("#breadcrumb").html(arr.join(""));

         $("#breadcrumb li a").off().on("click", function(){
             var _that = this;
             $("#breadcrumb").attr("data","1");
             $.each(g_breadcrumd, function(i){
                 var _text = g_breadcrumd[i].text;
                 if( _text== $(_that).text()){
                     if(g_breadcrumd.length == 1){
                        $("#breadcrumb").html('');
                         $("#jCrumbs").parent().remove();
                         $("#breadcrumb").attr("data","0");
                     }else{
                         $.each($("#jCrumbs").find("a"), function(){
                             if($(this).text() == _text){
                                 var _index = parseInt($(this).parent("li").index());
                                 var len = parseInt($("#jCrumbs").find("li").length);
                                 for(var i = _index; i <len ; i ++ ){
                                    $("#jCrumbs").find("li").eq(i).remove();
                                 }
                                 if($("#jCrumbs").find("li").length  == 1){
                                    $("#jCrumbs").parent().remove();
                                    $("#breadcrumb").attr("data","0");
                                 }
                             }
                             return;
                         })
                     }
                     g_breadcrumd.splice(i,g_breadcrumd.length);
                     setBreadcrumdCookie();
                 }
             })
         })
}

function setBreadcrumdCookie(){
     $.cookie(location.host +"g_breadcrumd",JSON.stringify(g_breadcrumd), { expires: 365 })
}

function getBreadcrumdCookie(){
    return $.cookie(location.host +"g_breadcrumd");
}
function clearBreadcrumd(){
    $.cookie(location.host +"g_breadcrumd",'');
    g_breadcrumd = [];
}


	 
