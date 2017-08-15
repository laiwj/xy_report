
var require = {
    urlArgs:"v="+new Date().getTime() ,
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
        prototype:"/lib/prototype",
        bootstrap:"/lib/bootstrap/js/bootstrap.min",
        msg:"/lib/msg",
        validator:"/lib/validator",
        SelectControl:"/lib/select/select",
        beanUtil:"/lib/beanUtil",
        dic:"/lib/dic",
        resultCode:"/lib/resultCode",
        staticMsg:"/lib/staticMsg",
        alertdialog:"/lib/jqueryalert/jquery.alerts",
        colorbox:"/lib/colorbox/jquery.colorbox",
        page:"/lib/page/page",

        login:"/js/login",
        report:"/js/report",
        userInfo:"/js/userInfo",
        account:"/js/account"
    },
    shim: {
        'SelectControl':{
            exports: 'SelectControl'
        },
        'bootstrap':['jquery'],
        "alertdialog":{
            deps: ['jquery','css!/lib/jqueryalert/jquery.alerts.css']
        },
        "jquery-cookie"  : ["jquery"],
        'colorbox':{
            deps: ['jquery','css!/lib/colorbox/colorbox.css']
        },
        'page':{
            deps: ['css!/lib/page/page.css']
        },
        "beanUtil":["validator"]
    },
    waitSeconds: 0
};