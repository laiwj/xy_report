define(function(require){
	require('./prototype')
    var xy_util = require('./util');
    var xy_validator = require("./validator");
    var xy_aTab =  {

        /**
         * 获取当前打开的页签
         */
        getActiveTab: function()
        {
            return $("#content-container:visible");
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
                        try {
                            var path =container.attr("controller");
                            var actionName = "xy_" + path;
                            //data = 1表示点击的是面包屑本身，不需要重新生成面包屑数据
                            if($("#breadcrumb").attr("data") != 1){
                                window[actionName]["onBreadcrumb"]();
                            }
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
            var me = this;
            var path = window.location.href;
            var controller = obj.controller;
            var template =obj.template;
             //移除当前页签
           this.removeActiveTab();
           xy_util.showLoadingCard();
            var container = $("#content-container");
            if(obj.iframe){
                //项目筛选组件单独做适配
                //生成面包屑
                setBreadCrumd();
                $(".breadCrumb-nav").addClass("iframe-nav");
                $("body").prepend($(".breadCrumb-nav"));
                var _top =  0 ;
                if($(".breadCrumb-nav").length > 0){
                    _top = $(".breadCrumb-nav").height();
                }

                var width = document.documentElement.clientWidth ;
                var height = document.documentElement.clientHeight ;
                var iframe='<iframe id="frames" name="frames" width="'+width+'" height="'+height+'" frameborder=0 src="'+obj.template+'" scrolling="no" style="position: fixed;z-index:1000;top:'+_top+'px;left:0;"></iframe>';
                container.html(iframe).show();
                xy_util.hideLoadingCard();
                return;
            }

             container.attr({"path":template,"controller":controller});
            container.load(template, function(a,b,c){
                if($("#session_user_id").val()==''){
                   window.location.href="/betago/logout/";
                    return;
                }
                if($("#toolbar").hasClass("small-toolbar")){
                    $("#toolbar").removeClass("small-toolbar");
                    $(".md-backdrop").remove();
                }

                container.show();
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
                                params[map[0]] = map[1];
                            }
                        }
                    }
                    if(obj.param){
                         params = $.extend(true,params, obj.param);
                      }
                   
                    if(controller){
                         var actionName = controller;
                        require(['domReady',actionName], function(domReady){
                            domReady(function(){
                                var actionModule = require('./'+actionName);

                                xy_util.hideLoadingCard();
                                actionModule["ready"](params);
                            })
                            
                        });
                    }
                   
                    //resize.window(true);

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
