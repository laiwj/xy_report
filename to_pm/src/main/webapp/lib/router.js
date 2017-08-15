'use strict';
define(['aTab','director'], function(xy_aTab){
    var routes = {
        '/report': function(){
            xy_aTab.creatTab({
                controller:'report',
                template:'components/main/report.vm'
            });
        },
        '/userInfo': function(id){
            xy_aTab.creatTab({
                controller:'userInfo',
                template:'components/main/userInfo.vm'
            });
        },
        '/userInfo/:id': function(id){
            xy_aTab.creatTab({
                controller:'userInfo',
                param: {id: id},
                template:'components/main/userInfo.vm'
            });
        },
        '/account': function(){
            xy_aTab.creatTab({
                controller:'account',
                template:'components/main/account.vm'
            });
        }


    };
    var router = Router(routes);
    router.init();
});


