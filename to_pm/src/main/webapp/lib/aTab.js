define(['util','prototype'],function(xy_util){
    var xy_aTab =  {

        /**
         * 获取当前打开的页签
         */
        getActiveTab: function()
        {
            return $("#index.js").children(":visible");
        },

        /**
         * 移除当前页签
         */
        removeActiveTab: function()
        {
            var container = this.getActiveTab();
            var visibleDomId = container.attr("id");

            switch (visibleDomId)
            {
                //标准页面
                case "content-container":
                    var path = container.attr("path");

                    //已加载页面
                    if ( xy_validator.notNullOrEmpty(path) )
                    {
                        //尝试调用移除页签方法
                        try
                        {
                            var actionName = "xy_" + path.split("/").last();
                            window[actionName]["onRemove"]();
                        }
                        catch (e)
                        {
                            //未设置移除方法
                        }

                        container.attr("path", "");
                    }

                    //清除内容
                    if ($.browser.msie)
                    {
                        container.html("");
                    }
                    container.empty();
                    container.hide();
                    break;


                //iframe页面
                case "main_3rdContainer":
                    container.attr("src", "");
                    container.hide();
                    break;

                default:
                    break;
            }
        },

        /**
         * 创建页签
         * @param obj
         */
        creatTab: function(obj)
        {



                var path = window.location.href;
                var controller = obj.controller;
                var template =obj.template;
                //移除当前页签
                this.removeActiveTab();
                xy_util.showLoadingCard();
                var container = $("#content-container");
                container.attr("path", template);
                container.load(template, function(){
                    container.show();
                    //for develop
                    //ar actionName = "xy_" + path.split("/").last();
                    //window[actionName]["ready"]();
                    //hideLoading();


                    //for publish
                    //避免初始化方式不存在异常
                    try
                    {
                        var params = {};
                        if ( path.split("?").length > 1 )
                        {
                            if(path.split("#").length > 1){
                                path = path.substring(0, path.indexOf("#"));
                            }

                            var pathParams =path.split("?")[1].split("&");
                            for ( var i = 0, len = pathParams.length; i < len; i++ )
                            {
                                var map = pathParams[i].split("=");
                                if ( map.length > 1 )
                                {
                                    params[map[0]] = decodeURIComponent(map[1]);
                                }
                            }
                        }
                        if(obj.param){
                            params = $.extend(true,params, obj.param);
                        }
                        var actionName = controller;
                        require([actionName], function(){
                            var actionModule = require('./'+actionName);
                            actionModule["ready"](params);
                        });
                        resize.window(true);
                        xy_util.hideLoadingCard();
                    }
                    catch(e)
                    {
                        xy_util.hideLoadingCard();
                    }

                });

        }
    };

    return xy_aTab;
})
