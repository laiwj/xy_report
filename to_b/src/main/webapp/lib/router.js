'use strict';
define(['aTab', 'director'], function (xy_aTab) {
    var routes = {
        '/dashboard': function () {
            xy_aTab.creatTab({
                controller: 'dashboard',
                template: '/static/html/dashboard/dashboard.html'
            });
        },
        '/countAnalysis': function () {
            xy_aTab.creatTab({
                controller: 'countAnalysis',
                template: "/static/html/countAnalysis/countAnalysis.html"
            });
        },
        '/recommendAnalysis': function () {
            xy_aTab.creatTab({
                controller: 'recommendAnalysis',
                template: "/static/html/recommendAnalysis/recommendAnalysis.html"
            });
        },
       /* '/project': function () {
            xy_aTab.creatTab({
                controller: 'myproject',
                template: '/static/html/project/project.html'
            });
        },*/
          '/myproject': function () {
            xy_aTab.creatTab({
                controller: 'myproject',
                template: '/static/html/myproject/myproject.html'
            });
        },
        '/myprojectByPage': {
            '/?([^\/]*)\/([^\/]*)/?': function (a, b) {
                xy_aTab.creatTab({
                    controller: 'myproject',
                    param: {status: a, page: b},
                    template: '/static/html/myproject/myproject.html'
                });
            }
        },
         '/checkproject': function () {
            xy_aTab.creatTab({
                controller: 'checkproject',
                template: '/static/html/checkproject/checkproject.html'
            });
        },
          '/checkprojectByPage': {
            '/?([^\/]*)\/([^\/]*)/?': function (a, b) {
                xy_aTab.creatTab({
                    controller: 'checkproject',
                    param: {status: a, page: b},
                    template: '/static/html/checkproject/checkproject.html'
                });
            }
        },
        '/projectByPage/:page': function (page) {
            xy_aTab.creatTab({
                controller: 'project',
                param: {page: page},
                template: '/static/html/project/project.html'
            });
        },
        '/projectByStatus/:status':  function (a) {
                xy_aTab.creatTab({
                    controller: 'project',
                    param: {status: a},
                    template: '/static/html/project/project.html'
                });
        },
        '/projectBycomId': {
            '/?([^\/]*)\/([^\/]*)/?': function (a, b) {
                xy_aTab.creatTab({
                    controller: 'project',
                    param: {com_id: a, status: b},
                    template: '/static/html/project/project.html'
                });
            }
        },
        '/resume': function (id) {
            xy_aTab.creatTab({
                controller: 'resume',
                template: '/static/html/resume/resume.html'
            });
        },
        '/systemresume': function (id) {
            xy_aTab.creatTab({
                controller: 'systemresume',
                template: '/static/html/systemresume/systemresume.html'
            });
        },
         '/myproject/systemresumeByProId':{
              '/?([^\/]*)\/([^\/]*)/?': function (a, b) {
                xy_aTab.creatTab({
                    controller: 'systemresume',
                     param: {pro_id: a, mode: b},//mode 0 自动推荐 1手动推荐
                    template: '/static/html/systemresume/systemresume.html'
                });
            }
        },
        '/resume/list': function () {
            xy_aTab.creatTab({
                controller: 'resume',
                template: '/static/html/resume/resumeFilter.html'
            });
        },
        '/resume/create/:local': function (local) {
            xy_aTab.creatTab({
                controller: 'createResume',
                  param: {local: local},
                template: '/static/html/resume/createResume.html'
            });
        },
        '/resume/edit/:id': function (id) {
            xy_aTab.creatTab({
                controller: 'createResume',
                param: {id: id},
                template: '/static/html/resume/createResume.html'
            });
        },
        '/resume/:id': function (id) {
            xy_aTab.creatTab({
                controller: 'resume',
                param: {id: id},
                template: '/static/html/resume/resume.html'
            });
        },
         '/resume/': {
           '?([^\/]*)\/([^\/]*)/?': function (a, b) {
                   xy_aTab.creatTab({
                       controller: 'resume',
                       param: {id: a,status: b},
                       template: '/static/html/resume/resume.html'
                   });
          }
        },
        '/myproject/resumelist/': {
            '?([^\/]*)\/([^\/]*)/?': function (a, b) {
                xy_aTab.creatTab({
                    controller: 'resume',
                    param: {id: a, status: b},
                    template: '/static/html/resume/resumeFilter.html'
                });
            }
        },
       /* '/resume/local/:id': function (id) {
            xy_aTab.creatTab({
                controller: 'resume',
                param: {id: id},
                template: '/static/html/resume/localResume.html'
            });
        },*/
        'localresume': function(){
             xy_aTab.creatTab({
                controller: 'localresume',
                template: '/static/html/localresume/localresume.html'
            });
        },
        '/searchResume/:search_str': function (search_str) {
            xy_aTab.creatTab({
                controller: 'searchResume',
                param: {search_str: search_str},
                template: '/static/html/resume/searchResume.html'
            });
        },
        '/client': function () {
            xy_aTab.creatTab({
                controller: 'client',
                template: '/static/html/client/client.html'
            });
        },
         '/clientByPage': {
            '?([^\/]*)\/([^\/]*)/?': function (a, b) {
                xy_aTab.creatTab({
                    controller: 'client',
                    param: {search_word: a, page: b},
                    template: '/static/html/client/client.html'
                });
            }
        },
        '/report': function () {
            xy_aTab.creatTab({
                controller: 'report',
                template: '/static/html/report/report.html'
            });
        },
        '/account': function () {
            xy_aTab.creatTab({
                controller: 'account',
                template: '/account.html'
            });
        },
        '/client/create': function () {
            xy_aTab.creatTab({
                controller: 'createClient',
                template: '/static/html/client/createClient.html'
            });
        },
        '/client/edit/:id': function (id) {
            xy_aTab.creatTab({
                controller: 'createClient',
                param: {id: id},
                template: '/static/html/client/createClient.html'
            });
        },
        '/client/detail/:id': function (id) {
            xy_aTab.creatTab({
                controller: 'clientDetail',
                param: {id: id},
                template: "/betago/customer?com_id=" + id
            });
        },
        '/project/create': function (id) {
            xy_aTab.creatTab({
                controller: 'createProject',
                template: '/static/html/project/createProject.html'
            });
        },
        '/project/edit/:id': function (id) {
            xy_aTab.creatTab({
                controller: 'createProject',
                param: {id: id},
                template: '/static/html/project/createProject.html'
            });
        },
        '/project/createByComId/:id': function (id) {
            xy_aTab.creatTab({
                controller: 'createProject',
                param: {com_id: id},
                template: '/static/html/project/createProject.html'
            });
        },
        '/project/detail/:id': function (id) {
            xy_aTab.creatTab({
                controller: 'projectDetail',
                param: {id: id},
                template: "/betago/pro/?pro_id=" + id
            });
        },
        '/project/filter/:id': function (id) {
            xy_aTab.creatTab({
                //controller:'flterProject',
                //param: {id:id},
                iframe: true,
                template: "/draw2d?pro_id=" + id
            });
        },
        'position':function(){
             xy_aTab.creatTab({
                controller:'position',
                template:'/static/html/position/position.html'
            });
        }



    };
    var router = Router(routes);
    router.init();
})


