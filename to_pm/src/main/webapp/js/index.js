require(['jquery', 'listener','util','alertdialog','bootstrap','jquery-cookie','router','staticMsg'],function($, xy_listener,xy_util,alertdialog){
    //,'',
   // 'wookmark','easing','imagesloaded','toolbar','createClient',

        xy_listener.initDefaultListener();
    /*$("#uploadURL").val();//*/
    xy_util = xy_util;
    uploadURL = "http://171.221.173.156:8082";
    htmlEncode = xy_util.htmlEncode;
    htmlTitleEncode = xy_util.htmlTitleEncode;
    htmlDecode = xy_util.htmlDecode;
    STATICMSG = STATICMSG;
    alertDIV = xy_util.alertDIV;
    confirmDIV = xy_util.confirmDIV;
    tipsDIV = xy_util.tipsDIV;
  /*  var layouts = {
        toolbar   : '/components/toolbar/toolbar.vm',
       // navigation: '/components/navigation/navigation.vm'
    };*/
    //$("#navigation").load(layouts["navigation"]);
/*    $("#toolbar").load(layouts["toolbar"]);*/

  /*  var showBar = [];
    $.ajax({
        url : '/login/getUserRole.json',
        async : false,
        success : function(res){
            if(res.code == 0){
               showBar = res.data;
            }
        }
    });

    $("#navigation").load(layouts["navigation"],function(){
        var cookiename= $.cookie(location.host + "_user");
        $(".J_name").text(cookiename)
    });
    $("#toolbar").load(layouts["toolbar"],function(){
        var $bar = $('#side_accordion');
        $.each(showBar,function(index,val){
            $bar.find('.J_show_' + val).removeClass('none');
        });
        var cookiename= $.cookie(location.host + "_user");
        $(".J_name").text(cookiename);
        var hash = window.location.hash;
        var src = '';
        if(hash && hash.indexOf('/') > 0){
            src = hash.split('/')[1];
        }
        $bar.find('.toggle_menu').removeClass('md-accent-bg').end()
            .find('a[href="#/'+ src +'"]').addClass('md-accent-bg');
    });
*/
});

	 
