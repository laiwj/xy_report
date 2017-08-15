// Author: Pwstrick <pwstrick@163.com>
// Filename: config.js

/*var uploadURL = "http://192.168.2.223:9090";
var ImgURL = uploadURL+"/bgImg/";*/
var require = {
    urlArgs:+new Date().getTime() ,//"v=1.9.5",//
    //baseUrl : "static",
    paths: {
        css:"/lib/require/css",
        text:"/lib/require/require.text",
        domReady:"/lib/require/domReady",
        jquery: "/lib/jquery-1.8.3.min",
        'jquery-cookie':"/lib/jquery.cookie.min",
        underscore:"/lib/underscore",
        router:"/lib/router",
        director:"/lib/director",
        listener:"/lib/listener",
        util:"/lib/util",
        aTab:"/lib/aTab",
        index:"/lib/index",
        bootstrap:"bootstrap",
        prototype:"/lib/prototype",
        msg:"/lib/msg",
        validator:"/lib/validator",
        selectControl:"/lib/select/select",
        beanUtil:"/lib/beanUtil",
        dic:"/lib/dic",
        resultCode:"/lib/resultCode",
        STATICMSG:"/lib/Msg",
        alertdialog:"/lib/jqueryalert/jquery.alerts",
        colorbox:"/lib/colorbox/jquery.colorbox",
        select:"/lib/select/select",
        page:"/lib/page/page",
        login:"/js/login",
        register:"/js/register",
        account:"/js/account",
        talentflow:"/js/talentflow",
        talentdistribution:"/js/talentdistribution",
        supplydemand:"/js/supplydemand"
    },
    shim: {
        'underscore': {
            exports: '_'
        },
          mousewheel:{
            deps:['jquery']
        },
         mCustomScrollbar:{
            exports:'mCustomScrollbar',
            deps:["jquery","mousewheel","css!/lib/mCustomScrollbar/jquery.mCustomScrollbar.css"]
        },
        echarts:{
            exports:'echarts',
        },
        'validator':["msg"],
        'colorbox':{
            deps: ['jquery','css!/commonjs/plugins/colorbox/colorbox-theme.css']
        },
        'WdatePicker':{
            //ie8下使用define依赖此模块时，不能成功下载此css
            deps: ['jquery'/*,'css!/commonjs/plugins/My97DatePicker/skin/WdatePicker.css'*/]
        },
        'dic':{
            exports: 'dic'
        },
        'imgCrop':{
            deps: ['jquery','ajaxfileupload','imgareaselect','css!/lib/upload/imgCrop.css','css!/lib/upload/imgAreaSelect/imgareaselect-default.css']
        },
        'ajaxfileupload':['jquery'],
        'imgareaselect':['jquery'],
        'baidumap':{
            deps: ['jquery'],
            exports:"baidumap"
        },
        "jquery-cookie"  : ["jquery"],
        "jquerymd5":["jquery"],
        "beanUtil":["validator"],
        staticMsg:{
            exports: 'STATICMSG'
        },
        "director":["jquery"],
        "wookmark":["jquery"],
        "jBreadCrumb":["jquery"],
        "imagesloaded":["jquery"],
        "easing":["jquery"],
        "jqueryList":["jquery"],
        "datapickerZH":["bootstrapDatepicker", "bootstrapTimepicker"],
        "bootstrapDatepicker":["css!/lib/datepicker/datepicker.css"],
        "bootstrapTimepicker":["css!/lib/datepicker/datepicker.css"],
        "datetimepicker":["css!/lib/bootstrap-datetimepicker-master/bootstrap-datetimepicker.min.css"],
        "chosen":["css!/lib/chosen/chosen.css"],
        "jqueryActual":["jquery"],
        "cityPicker":["jquery"],
        "jqueryForm":["jquery"],
         "alertdialog":{
            deps: ['jquery']
        },
         'colorbox':{
            deps: ['jquery','css!/lib/colorbox/colorbox-theme.css']
        }
    },
    waitSeconds: 0
};