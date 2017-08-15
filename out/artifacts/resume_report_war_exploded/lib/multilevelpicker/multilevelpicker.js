/**
 * use
 * new multilevelpicker({
        domId:"position_name",//点击的节点id
        html:$(positionSelectHTML),//需要添加的内容html
        callback:function(obj){
            $("#project_pro_name").parent().find(".select-text").text(obj.third).attr("pro_position_specific_type",obj.second);
            $("#project_pro_name").val(obj.third);
        }
    });
 */
define(["util","mCustomScrollbar"],
    function (xy_util,mCustomScrollbar) {

        var multilevelpicker = function (config) {
            this.container = $("#"+config.domId);
            this.option = config;
            this.init(config);
        }

        multilevelpicker.prototype = {
            init: function (config) {
                var me = this;

                //为了提高速度，使用util.js中的createMultilevelSelectHTML生成html的步骤修改为加载静态页面
                this.appendHtml()
                this.initEvent();
                this.setValue();
            },
            appendHtml: function(){
                var me = this;
                this.container.append(this.option.html);
                var w = this.option.width || this.container.width();
                var l = this.option.left || w;
                this.container.find(".options-picker").width(w);
                this.container.find(".picker-next-level").each(function(){
                    var h = 0;
                    $.each($(this).find(".second_f_child"), function(){
                        h = h + $(this).height();
                    })
                    $(this).css({"left":l,"min-height":h});
                })
            },
            setValue: function(value){
                var _value = value || this.container.find(".select-value").val();
                if(_value){
                    this.container.find(".select-text").text(_value);
                }
            },
            initEvent: function () {
                var me = this;
                $(".first-level-content").mCustomScrollbar();
                //$(".picker-next-level").mCustomScrollbar();

                this.container.find(".picker-first-level").on("mouseover", function(){
                    if($(this).hasClass("p-hover")){
                        return;
                    }
                    $(".picker-next-level").css("display","none");
                    var _title = $(this).find('.li-text').text();
                    var index = me.container.find(".picker-first-level").index(this);
                    me.container.find(".picker-next-level").eq(index).css('display','block');
                    me.container.find(".picker-next-level").eq(index).css('visibility','hidden');
                    //$(".picker-next-level[_title="+_title+"]").css('display','block');
                    $(".picker-first-level").removeClass("p-hover");
                    $(this).addClass("p-hover");
                    me.setVisibleHeight(index);
                   
                })

                this.container.find(".text-option").on("click", function(){
                    if( me.option.callback){
                            me.option.callback({
                                "first":$(this).attr("f-data"),
                                "second":$(this).attr("s-data"),
                                "third":$(this).find(".li-text").text()
                            });
                    }
                    me.container.find(".options-picker").css("display","none");
                    me.resetNode();
                })

                $(document).off("mousedown.mp").on('mousedown.mp',function(e){
                    var event = $.event.fix(e);

                    var $jqCity = $(event.target).closest(".select-picker");
                    if($jqCity.length > 0){
                        return false;
                    }
                    $(".options-picker").hide();
                    $(".picker-next-level").hide();


                });

                this.container.find(".select-text").on("mousedown", function(){
                    //隐藏其他下拉框
                    $(".options-warp").hide();
                    $(".options-picker").not(me.container.find(".options-picker")).hide();
                    me.container.find(".options-picker").css("top",me.container.outerHeight()+2).toggle();
                    return false;

                })

                if(this.option.isCheckbox){
                    this.container.find(".check-all-first").on("click", function(){
                        var _isCheck = this.checked;
                        $(this).parents(".options-picker").find("input[type='checkbox']").attr("checked",_isCheck);
                        me.getCheckBoxData();
                    })

                    this.container.find(".check-all-second").on("click", function(){
                        var _isCheck = this.checked;
                        var parentNode = $(this).parents(".picker-next-level");
                        var index = parentNode.index(".picker-next-level");
                        parentNode.find("input[type='checkbox']").attr("checked",_isCheck);
                        $(".picker-first-level").eq(index).find(".checkbox-first-item").attr("checked",_isCheck);

                        me.getCheckBoxData();

                    })

                     this.container.find(".checkbox-first-item").on("click", function(){
                        var _isCheck = this.checked;
                        var parentNode = $(this).parents(".picker-first-level");
                        var index = parentNode.index(".picker-first-level");
                        $(".picker-next-level").eq(index).find("input[type='checkbox']").attr("checked",_isCheck);

                         var isCheckAll = me.isAllCheckFirstLevel();
                         me.container.find(".check-all-first").attr("checked",isCheckAll);

                         me.getCheckBoxData();
                    })

                     this.container.find(".checkbox-second-item").on("click", function(){
                         var _firstParentNode = $(this).parent();
                         if(_firstParentNode.find(".picker-three-level").length > 0 ){
                            _firstParentNode.find("input[type='checkbox']").attr("checked",this.checked);
                         }
                         var parentNode = $(this).parents(".picker-next-level");
                         var isCheckAll = me.isAllCheckSecondLevel(this);
                         parentNode.find(".check-all-second").attr("checked",isCheckAll);

                         var isCheck = me.isAtLeastCheckOne(this);
                          var index = parentNode.index(".picker-next-level");
                        $(".picker-first-level").eq(index).find(".checkbox-first-item").attr("checked",isCheck);

                         me.getCheckBoxData();

                    })

                    this.container.find(".checkbox-third-item").on("click", function(){
                        var isAtLeastCheck = me.isAtLeastCheckOneThird(this);
                        $(this).parents(".pick-second-item").find(".checkbox-second-item").attr("checked",isAtLeastCheck);

                        var parentNode = $(this).parents(".picker-next-level");
                        var isCheck = me.isAtLeastCheckOne(this);
                        var index = parentNode.index(".picker-next-level");
                        $(".picker-first-level").eq(index).find(".checkbox-first-item").attr("checked",isCheck);

                        var isCheckAll = me.isAllCheckSecondLevel(this);
                         parentNode.find(".check-all-second").attr("checked",isCheckAll);

                        var isCheckAll = me.isAllCheckFirstLevel(this);
                         me.container.find(".check-all-first").attr("checked",isCheckAll);

                        me.getCheckBoxData();
                    })


                }

            },
            getCheckBoxData: function(){
                var me = this;
                if( me.option.callback) {
                         if (me.option.isCheckbox) {
                             var arr = [];
                             $.each(this.container.find($(".checkbox-first-item:checked")), function(){
                                var parentNode = $(this).parents(".picker-first-level");
                                var index = parentNode.index(".picker-first-level");
                                var secondNode = $(".picker-next-level").eq(index);
                                 var dataLevel = me.option.dataLevel ==undefined?2:me.option.dataLevel;
                                 if(dataLevel == 2){
                                     if(secondNode.find(".checkbox-second-item").length  == 0){
                                        arr.push(parentNode.attr("_value"))
                                        }else{
                                             $.each(secondNode.find(".checkbox-second-item:checked"), function () {
                                                 arr.push(this.value)
                                             })
                                        }
                                 }else if(dataLevel == 3){
                                         $.each(secondNode.find(".checkbox-second-item:checked"), function () {
                                                 var pNode = $(this).parent();
                                                $.each(pNode.find(".checkbox-third-item:checked"), function(){
                                                    arr.push(this.value)
                                                })
                                             })
                                 }

                             })
                             me.option.callback({
                                 "data": arr
                             });
                         }
                     }
            },
            isAllCheckFirstLevel: function(){
                var isCheckAll = true;
                if(this.container.find(".check-all-second").length > 0 ){
                     $.each(this.container.find(".check-all-second"), function(){
                        if(!this.checked){
                            isCheckAll = false;
                            return false;
                        }
                    })
                }else{
                     $.each(this.container.find(".checkbox-first-item"), function(){
                        if(!this.checked){
                            isCheckAll = false;
                            return false;
                        }
                    })
                }
                return isCheckAll;

            },
            isAllCheckSecondLevel: function(obj){
                 var isCheckAll = true;
                 var parentNode = $(obj).parents(".picker-next-level");
                 var checkeNodes = parentNode.find(".checkbox-third-item");
                $.each(checkeNodes, function(){
                    if(!this.checked){
                        isCheckAll = false;
                        return false;
                    }
                })
                return isCheckAll;
            },
            /*
            *只要二级有一个选中，一级就必须选中
            * */
            isAtLeastCheckOne: function(obj){
                 var isCheck= false;
                 var parentNode = $(obj).parents(".picker-next-level");
                 var checkeNodes = parentNode.find(".checkbox-second-item");
                $.each(checkeNodes, function(){
                    if(this.checked){
                        isCheck = true;
                        return false;
                    }
                })
                return isCheck;
            },
            isCheckAllThird: function(obj){
                var isCheckAll = true;
                 var parentNode = $(obj).parents(".picker-three-level");
                 var checkeNodes = parentNode.find(".checkbox-third-item");
                $.each(checkeNodes, function(){
                    if(!this.checked){
                        isCheckAll = false;
                        return false;
                    }
                })
                return isCheckAll;
            },
             isAtLeastCheckOneThird: function(obj){
                 var isCheck= false;
                  var parentNode = $(obj).parents(".picker-three-level");
                 var checkeNodes = parentNode.find(".checkbox-third-item");
                $.each(checkeNodes, function(){
                    if(this.checked){
                        isCheck = true;
                        return false;
                    }
                })
                return isCheck;
            },
            setVisibleHeight: function(index){
                var s_node = this.container.find(".picker-next-level").eq(index);
                var f_node = this.container.find(".first-level-content");
                var f_height = f_node.height();
                var w_height = $(window).height(),w_width = $(window).width();
                var height = s_node.height();
                var _top = 0,_w = s_node.width();
                if(parseInt(s_node.css("top"))!= 0){
                    _top = s_node.css("top");
                }else{
                     if(height > f_height){
                          if(height > w_height){
                               height = w_height - 100;
                           }
                           var _top = (f_height - height)/2;
                       }

                       if(Math.abs(_top) > f_node.offset().top){
                            _top = "-"+(f_node.offset().top-20);
                       }
                }

                 s_node.animate({
                       top:_top,
                       height:height
                   })
                s_node.mCustomScrollbar();

                var s_node_left = s_node.offset().left+s_node.width();
                if(s_node_left > w_width){
                     _w = s_node.width() - (s_node_left - w_width)-50;
                }
                 s_node.width(_w);
                if(s_node.find(".picker-three-level").length == 0){
                    if(s_node.find(".picker-next-level-item").length == 0){
                         s_node.width(0).hide();
                    }else{
                         s_node.width(_w);
                    }

                }else{
                     s_node.find(".picker-three-level").width(_w);
                }

                 s_node.css("visibility","visible");
            },
            unbindEvent: function(){
                this.container.find(".select-text").off().css("background-color",'#CECECE');
            },
            resetNode: function () {
                $(".picker-first-level").removeClass("p-hover");
                $(".picker-next-level").css("display","none");
            }
        }

        return multilevelpicker;
    })