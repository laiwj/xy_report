define([
    'jquery'
], function ($) {


    var xy_listener = {

        /**
         * 默认全局监听
         */
        initDefaultListener: function () {
            var me = this;
              me.getTipMsg();
              me.setMenuSelected();

            $('body').on('click.tab.data-api', '[data-toggle="tab"], [data-toggle="pill"]', function (e) {
                e.preventDefault()
                $(this).tab('show')
            })
            //菜单监听
         /*   $(document).on('click.[data-toggle="menu"]', function (e) {
                //全局变量，在index.js中定义，面包屑数据
                 g_breadcrumd = [];
                $("#jCrumbs").html('');
                $("#side_accordion .accordion-heading a").removeClass("md-accent-bg");
                $(e.target).addClass("md-accent-bg");
            })*/
             //菜单监听
            $(document).on('click.[data-toggle="menu"]',".accordion-toggle", function (e) {
                delete window.search_page;
                delete window.search_keyword;
                  //全局变量，在index.js中定义，面包屑数据
                 clearBreadcrumd();
                $(".search_query").val('');
                $("#jCrumbs").html('');
                var target = $(e.target),parentNode = target.parents(".accordion-group");
                $(".md-accent-bg").removeClass("md-accent-bg");
                if(target.closest($(".accordion-children")).length > 0 ){
                     target.parents("li.accordion-group-children").find("a.accordion-toggle").addClass("md-accent-bg");
                    return;
                }else if(parentNode.find($(".accordion-children")).length == 0){
                    parentNode.find(".accordion-heading").addClass("md-accent-bg");
                }
                var height = 48* (parentNode.find(".accordion-children li").length);
                if(!(parentNode.find(".toolbar-arrow").hasClass("icon-chevron-right"))){
                    height = 0;
                }
                parentNode.find(".toolbar-arrow").toggleClass("icon-chevron-right");
                parentNode.find(".accordion-children").animate({
                     height:height
                 });
            })

            /*搜索*/
            $(document).on("click.button", "#search_btn", function () {
                var searchVal = $.trim($(".search_query ").val());
                window.location.hash = '#/searchResume/' + encodeURIComponent(searchVal);
            })
            $(document).on("keydown", ".search_query ", function (e) {
                if (e.keyCode == 13) {
                    var searchVal = $.trim($(".search_query ").val());
                    window.location.hash = '#/searchResume/' + encodeURIComponent(searchVal);
                }

            })

            /*
             * 文本框最大长度限制,监听键盘输入
             */
            $(document).on("keyup.maxlength mousemove.maxlength paste.maxlength", "textarea[maxlength]", function (event) {
                var maxlen = parseInt($(this).attr('maxlength'));
                var text = $(this).val();
                var chars = text.length;

                if (chars > maxlen) {
                    var new_text = text.substr(0, maxlen);
                    $(this).val(new_text);
                }
            });

            /*
             * 文本框最大长度限制,监听粘贴
             */
            $(document).on("paste.maxlength", "textarea[maxlength]", function (event) {
                var maxlen = parseInt($(this).attr('maxlength'));
                if (window.clipboardData && window.clipboardData.setData) {
                    var str = window.clipboardData.getData('Text');
                    if (str.length > maxlen) {
                        str = str.substr(0, maxlen);
                        window.clipboardData.setData('Text', str);
                    }
                }

            });

            /*
             * 多选框插件点击事件监听
             */
            $(document).on("click.checkbox", "div.md-checkbox[name!=checkAll]", function (event) {
                xy_util.stopPop(event);
                $(document).click();
                var me = $(event.target);
                if (me.hasClass("disabled")) {
                    return;
                }
                if (me.hasClass("md-checkbox")) {
                    me.toggleClass("md-checked").parents(".talent_list").toggleClass("select-talent");
                } else {
                    me.parents(".md-checkbox").toggleClass("md-checked").parents(".talent_list").toggleClass("select-talent");
                }


                var domList = $("div.md-checkbox[name!=checkAll]"), mark = true;
                $.each(domList, function () {
                    if (!$(this).hasClass("md-checked")) {
                        mark = false;
                        return;
                    }
                })

                if (mark) {
                    $(".checkAll").addClass("md-checked");
                } else {
                    $(".checkAll").removeClass("md-checked");
                }

                $(".count_check").text($("div.md-checked[name!=checkAll]").length);

            });

            //全选
            $(document).on("click.checkAll", ".checkAll", function (event) {
                xy_util.stopPop(event);
                var me = $(event.target);
                if (me.hasClass("disabled")) {
                    return;
                }

                if (me.hasClass("md-checkbox")) {
                    me.toggleClass("md-checked");
                } else {
                    me.parents(".md-checkbox").toggleClass("md-checked");
                }

                if (me.hasClass("md-checked") || me.parents(".md-checkbox").hasClass("md-checked")) {
                    $(".md-checkbox[name!=checkAll]").addClass("md-checked").parents(".talent_list").addClass("select-talent");
                } else {
                    $(".md-checkbox[name!=checkAll]").removeClass("md-checked").parents(".talent_list").removeClass("select-talent");
                }

                $(".count_check").text($("div.md-checked[name!=checkAll]").length);
                //$(".md-checkbox").toggleClass("md-checked");
            });

            //窗口缩小时，小菜单点击事件
            $(document).on("click.smallMenu", ".small-menu", function (event) {
                $("#toolbar").addClass("small-toolbar");
                $("body").append('<div class="md-backdrop"></div>');
            })

            $(document).on("click.backdrop", ".md-backdrop", function (event) {
                $("#toolbar").removeClass("small-toolbar");
                $(".md-backdrop").remove();
            })
            //浏览器
            //$(window).bind('beforeunload',function(){$.cookie(location.host+"_userInfoResult","");});

            //回退
            $(document).on("click.backstep", ".backstep", function (event) {
                window.history.back()
            })


            //消息列表
            $(document).off("click.J_msg").on("click.J_msg",".J_msg", function(event){
                me.getTipMsg();
                $(".J_msg_list").animate({
                    right:0
                })
            })

            $(document).on("click.J_close_msg",".J_close_msg", function(event){
                $(".J_msg_list").animate({
                    right:'-300'
                })
            })

             $(document).on("click", function(e){
                if($(e.target).closest($(".J_msg_list,.J_msg")).length == 0){
                    $(".J_msg_list").animate({
                        right:'-300'
                    })
                }
            })
        },
        getTipMsg:function(){
            var me = this;
            $(".J_msg_list").height(document.documentElement.clientHeight);
            //手动点击，停止自定请求，从当前开始重启自动请求
                clearInterval(window.t);
                me.postMsg();

                window.t =  setInterval(function(){
                         me.postMsg();
                         },300000);


        },
        setMenuSelected: function(){
            var me = this;
            var _hash = window.location.hash;
            $.each($(".sidebar_inner a"), function(){
                var _href = $(this).attr("href");
                if(_hash.indexOf(_href) > -1 ){
                    me.setMenuHeight(this);
                    return;
                }
            })
        },
        setMenuHeight: function(obj){
            /* g_breadcrumd = [];
             $("#jCrumbs").html('');*/
            var parentNode = $(obj).parents(".accordion-group");
             $(".md-accent-bg").removeClass("md-accent-bg");
            if(parentNode.find(".accordion-children").length  > 0){
                $(obj).addClass("md-accent-bg");
                //有二级节点
                var height = 48* (parentNode.find(".accordion-children li").length);
                if(!(parentNode.find(".toolbar-arrow").hasClass("icon-chevron-right"))){
                    height = 0;
                }
                parentNode.find(".toolbar-arrow").toggleClass("icon-chevron-right");
                parentNode.find(".accordion-children").animate({
                     height:height
                 });
            }else{
                $(obj).parents(".accordion-heading").addClass("md-accent-bg");
            }
        },
        postMsg: function(){
            var me = this;
             $("#msg_ul").html('');
              $.post("/betago/message_record/",function(result){
                xy_util.restCallback(result, function(data){
                    if(data.length != 0){
                        $(".new-msg-tip").show();
                    }else{
                         $(".new-msg-tip").hide();
                    }
                      var template = _.template($("#magListTemp").html());
                      $("#msg_ul").html(template({data: data}));
                    $("#msg_ul li a").off().on("click", function(){
                        setTimeout(function(){
                             $(".J_msg_list").animate({
                                right:'-300'
                            })
                        },1000)

                    })
                })
            })
        },


        /**
         * 窗口大小变更监听
         */
        initResizeListener: function () {
            $(window).resize(function () {
                resize.window();
            });
            resize.window();
        },

        /**
         * 自动较验监听
         */
        initValidatorListener: function () {
            xy_validator.initAutoValidator();
        },


        /**
         * 程序异常监听
         * @ignore
         */
        initErrorListerner: function () {

            window.onerror = function (msg, url, line) {
                try {
                    console.log(msg
                        + "\nurl:" + url
                        + "\nat line:" + line);
                }
                catch (e) {
                    console.log(e);
                }
                return true;
            };
        }
    };

    /**TAB***/
    !function ($) {

        "use strict"; // jshint ;_;


        /* TAB CLASS DEFINITION
         * ==================== */

        var Tab = function (element) {
            this.element = $(element)
        }

        Tab.prototype = {

            constructor: Tab

            , show: function () {
                var $this = this.element
                    , $ul = $this.closest('ul:not(.dropdown-menu)')
                    , selector = $this.attr('data-target')
                    , previous
                    , $target
                    , e

                if (!selector) {
                    selector = $this.attr('href')
                    selector = selector && selector.replace(/.*(?=#[^\s]*$)/, '') //strip for ie7
                }

                if ($this.parent('li').hasClass('active')) return

                previous = $ul.find('.active a').last()[0]

                e = $.Event('show', {
                    relatedTarget: previous
                })

                $this.trigger(e)

                if (e.isDefaultPrevented()) return

                $target = $(selector)

                this.activate($this.parent('li'), $ul)
                this.activate($target, $target.parent(), function () {
                    $this.trigger({
                        type: 'shown'
                        , relatedTarget: previous
                    })
                })
            }

            , activate: function (element, container, callback) {
                var $active = container.find('> .active')
                    , transition = callback
                    && $.support.transition
                    && $active.hasClass('fade')

                function next() {
                    $active
                        .removeClass('active')
                        .find('> .dropdown-menu > .active')
                        .removeClass('active')

                    element.addClass('active')

                    if (transition) {
                        element[0].offsetWidth // reflow for transition
                        element.addClass('in')
                    } else {
                        element.removeClass('fade')
                    }

                    if (element.parent('.dropdown-menu')) {
                        element.closest('li.dropdown').addClass('active')
                    }

                    callback && callback()
                }

                transition ?
                    $active.one($.support.transition.end, next) :
                    next()

                $active.removeClass('in')
            }
        }


        /* TAB PLUGIN DEFINITION
         * ===================== */

        $.fn.tab = function (option) {
            return this.each(function () {
                var $this = $(this)
                    , data = $this.data('tab')
                if (!data) $this.data('tab', (data = new Tab(this)))
                if (typeof option == 'string') data[option]()
            })
        }

        $.fn.tab.Constructor = Tab

    }(window.jQuery);


    !function ($) {

        "use strict"; // jshint ;_;


        /* DROPDOWN CLASS DEFINITION
         * ========================= */

        var toggle = '[data-toggle=dropdown]'
            , Dropdown = function (element) {
            var $el = $(element).on('click.dropdown.data-api', this.toggle)
            $('html').on('click.dropdown.data-api', function () {
                $el.parent().removeClass('open')
            })
        }

        Dropdown.prototype = {

            constructor: Dropdown

            , toggle: function (e) {
                var $this = $(this)
                    , $parent
                    , isActive

                if ($this.is('.disabled, :disabled')) return

                $parent = getParent($this)

                isActive = $parent.hasClass('open')

                clearMenus()

                if (!isActive) {
                    $parent.toggleClass('open')
                    $this.focus()
                }

                return false
            }

            , keydown: function (e) {
                var $this
                    , $items
                    , $active
                    , $parent
                    , isActive
                    , index

                if (!/(38|40|27)/.test(e.keyCode)) return

                $this = $(this)

                e.preventDefault()
                e.stopPropagation()

                if ($this.is('.disabled, :disabled')) return

                $parent = getParent($this)

                isActive = $parent.hasClass('open')

                if (!isActive || (isActive && e.keyCode == 27)) return $this.click()

                $items = $('[role=menu] li:not(.divider) a', $parent)

                if (!$items.length) return

                index = $items.index($items.filter(':focus'))

                if (e.keyCode == 38 && index > 0) index--                                        // up
                if (e.keyCode == 40 && index < $items.length - 1) index++                        // down
                if (!~index) index = 0

                $items
                    .eq(index)
                    .focus()
            }

        }

        function clearMenus() {
            getParent($(toggle))
                .removeClass('open')
        }

        function getParent($this) {
            var selector = $this.attr('data-target')
                , $parent

            if (!selector) {
                selector = $this.attr('href')
                selector = selector && /#/.test(selector) && selector.replace(/.*(?=#[^\s]*$)/, '') //strip for ie7
            }

            $parent = $(selector)
            $parent.length || ($parent = $this.parent())

            return $parent
        }


        /* DROPDOWN PLUGIN DEFINITION
         * ========================== */

        $.fn.dropdown = function (option) {
            return this.each(function () {
                var $this = $(this)
                    , data = $this.data('dropdown')
                if (!data) $this.data('dropdown', (data = new Dropdown(this)))
                if (typeof option == 'string') data[option].call($this)
            })
        }

        $.fn.dropdown.Constructor = Dropdown


        /* APPLY TO STANDARD DROPDOWN ELEMENTS
         * =================================== */

        $(function () {
            $('html')
                .on('click.dropdown.data-api touchstart.dropdown.data-api', clearMenus)
            $('body')
                .on('click.dropdown touchstart.dropdown.data-api', '.dropdown form', function (e) {
                    e.stopPropagation()
                })
                .on('click.dropdown.data-api touchstart.dropdown.data-api', toggle, Dropdown.prototype.toggle)
                .on('keydown.dropdown.data-api touchstart.dropdown.data-api', toggle + ', [role=menu]', Dropdown.prototype.keydown)
        })

    }(window.jQuery);

    /* ===========================================================
     * bootstrap-tooltip.js v2.1.1
     * http://twitter.github.com/bootstrap/javascript.html#tooltips
     * Inspired by the original jQuery.tipsy by Jason Frame
     * ===========================================================
     * Copyright 2012 Twitter, Inc.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     * ========================================================== */


    !function ($) {

        "use strict"; // jshint ;_;


        /* TOOLTIP PUBLIC CLASS DEFINITION
         * =============================== */

        var Tooltip = function (element, options) {
            this.init('tooltip', element, options)
        }

        Tooltip.prototype = {

            constructor: Tooltip

            , init: function (type, element, options) {
                var eventIn
                    , eventOut

                this.type = type
                this.$element = $(element)
                this.options = this.getOptions(options)
                this.enabled = true

                if (this.options.trigger == 'click') {
                    this.$element.on('click.' + this.type, this.options.selector, $.proxy(this.toggle, this))
                } else if (this.options.trigger != 'manual') {
                    eventIn = this.options.trigger == 'hover' ? 'mouseenter' : 'focus'
                    eventOut = this.options.trigger == 'hover' ? 'mouseleave' : 'blur'
                    this.$element.on(eventIn + '.' + this.type, this.options.selector, $.proxy(this.enter, this))
                    this.$element.on(eventOut + '.' + this.type, this.options.selector, $.proxy(this.leave, this))
                }

                this.options.selector ?
                    (this._options = $.extend({}, this.options, {trigger: 'manual', selector: ''})) :
                    this.fixTitle()
            }

            , getOptions: function (options) {
                options = $.extend({}, $.fn[this.type].defaults, options, this.$element.data())

                if (options.delay && typeof options.delay == 'number') {
                    options.delay = {
                        show: options.delay
                        , hide: options.delay
                    }
                }

                return options
            }

            , enter: function (e) {
                var self = $(e.currentTarget)[this.type](this._options).data(this.type)

                if (!self.options.delay || !self.options.delay.show) return self.show()

                clearTimeout(this.timeout)
                self.hoverState = 'in'
                this.timeout = setTimeout(function () {
                    if (self.hoverState == 'in') self.show()
                }, self.options.delay.show)
            }

            , leave: function (e) {
                var self = $(e.currentTarget)[this.type](this._options).data(this.type)

                if (this.timeout) clearTimeout(this.timeout)
                if (!self.options.delay || !self.options.delay.hide) return self.hide()

                self.hoverState = 'out'
                this.timeout = setTimeout(function () {
                    if (self.hoverState == 'out') self.hide()
                }, self.options.delay.hide)
            }

            , show: function () {
                var $tip
                    , inside
                    , pos
                    , actualWidth
                    , actualHeight
                    , placement
                    , tp

                if (this.hasContent() && this.enabled) {
                    $tip = this.tip()
                    this.setContent()

                    if (this.options.animation) {
                        $tip.addClass('fade')
                    }

                    placement = typeof this.options.placement == 'function' ?
                        this.options.placement.call(this, $tip[0], this.$element[0]) :
                        this.options.placement

                    inside = /in/.test(placement)

                    $tip
                        .remove()
                        .css({top: 0, left: 0, display: 'block'})
                        .appendTo(inside ? this.$element : document.body)

                    pos = this.getPosition(inside)

                    actualWidth = $tip[0].offsetWidth
                    actualHeight = $tip[0].offsetHeight

                    switch (inside ? placement.split(' ')[1] : placement) {
                        case 'bottom':
                            tp = {top: pos.top + pos.height, left: pos.left + pos.width / 2 - actualWidth / 2}
                            break
                        case 'top':
                            tp = {top: pos.top - actualHeight, left: pos.left + pos.width / 2 - actualWidth / 2}
                            break
                        case 'left':
                            tp = {top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left - actualWidth}
                            break
                        case 'right':
                            tp = {top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left + pos.width}
                            break
                    }

                    $tip
                        .css(tp)
                        .addClass(placement)
                        .addClass('in')
                }
            }

            , setContent: function () {
                var $tip = this.tip()
                    , title = this.getTitle()

                $tip.find('.tooltip-inner')[this.options.html ? 'html' : 'text'](title)
                $tip.removeClass('fade in top bottom left right')
            }

            , hide: function () {
                var that = this
                    , $tip = this.tip()

                $tip.removeClass('in')

                function removeWithAnimation() {
                    var timeout = setTimeout(function () {
                        $tip.off($.support.transition.end).remove()
                    }, 500)

                    $tip.one($.support.transition.end, function () {
                        clearTimeout(timeout)
                        $tip.remove()
                    })
                }

                $.support.transition && this.$tip.hasClass('fade') ?
                    removeWithAnimation() :
                    $tip.remove()

                return this
            }

            , fixTitle: function () {
                var $e = this.$element
                if ($e.attr('title') || typeof($e.attr('data-original-title')) != 'string') {
                    $e.attr('data-original-title', $e.attr('title') || '').removeAttr('title')
                }
            }

            , hasContent: function () {
                return this.getTitle()
            }

            , getPosition: function (inside) {
                return $.extend({}, (inside ? {top: 0, left: 0} : this.$element.offset()), {
                    width: this.$element[0].offsetWidth
                    , height: this.$element[0].offsetHeight
                })
            }

            , getTitle: function () {
                var title
                    , $e = this.$element
                    , o = this.options

                title = $e.attr('data-original-title')
                    || (typeof o.title == 'function' ? o.title.call($e[0]) : o.title)

                return title
            }

            , tip: function () {
                return this.$tip = this.$tip || $(this.options.template)
            }

            , validate: function () {
                if (!this.$element[0].parentNode) {
                    this.hide()
                    this.$element = null
                    this.options = null
                }
            }

            , enable: function () {
                this.enabled = true
            }

            , disable: function () {
                this.enabled = false
            }

            , toggleEnabled: function () {
                this.enabled = !this.enabled
            }

            , toggle: function () {
                this[this.tip().hasClass('in') ? 'hide' : 'show']()
            }

            , destroy: function () {
                this.hide().$element.off('.' + this.type).removeData(this.type)
            }

        }


        /* TOOLTIP PLUGIN DEFINITION
         * ========================= */

        $.fn.tooltip = function (option) {
            return this.each(function () {
                var $this = $(this)
                    , data = $this.data('tooltip')
                    , options = typeof option == 'object' && option
                if (!data) $this.data('tooltip', (data = new Tooltip(this, options)))
                if (typeof option == 'string') data[option]()
            })
        }

        $.fn.tooltip.Constructor = Tooltip

        $.fn.tooltip.defaults = {
            animation: true
            , placement: 'top'
            , selector: false
            , template: '<div class="tooltip"><div class="tooltip-arrow"></div><div class="tooltip-inner"></div></div>'
            , trigger: 'hover'
            , title: ''
            , delay: 0
            , html: true
        }

    }(window.jQuery);

    /* bootstrap-modal.js v2.1.1
     * http://twitter.github.com/bootstrap/javascript.html#modals
     * =========================================================
     * Copyright 2012 Twitter, Inc.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     * ========================================================= */


    !function ($) {

        "use strict"; // jshint ;_;


        /* MODAL CLASS DEFINITION
         * ====================== */

        var Modal = function (element, options) {
            this.options = options
            this.$element = $(element)
                .delegate('[data-dismiss="modal"]', 'click.dismiss.modal', $.proxy(this.hide, this))
            this.options.remote && this.$element.find('.modal-body').load(this.options.remote)
        }

        Modal.prototype = {

            constructor: Modal

            , toggle: function () {
                return this[!this.isShown ? 'show' : 'hide']()
            }

            , show: function () {
                var that = this
                    , e = $.Event('show')

                this.$element.trigger(e)

                if (this.isShown || e.isDefaultPrevented()) return

                $('body').addClass('modal-open')

                this.isShown = true

                this.escape()

                this.backdrop(function () {
                    var transition = $.support.transition && that.$element.hasClass('fade')

                    if (!that.$element.parent().length) {
                        that.$element.appendTo(document.body) //don't move modals dom position
                    }

                    that.$element
                        .show()

                    if (transition) {
                        that.$element[0].offsetWidth // force reflow
                    }

                    that.$element
                        .addClass('in')
                        .attr('aria-hidden', false)
                        .focus()

                    that.enforceFocus()

                    transition ?
                        that.$element.one($.support.transition.end, function () {
                            that.$element.trigger('shown')
                        }) :
                        that.$element.trigger('shown')

                })
            }

            , hide: function (e) {
                e && e.preventDefault()

                var that = this

                e = $.Event('hide')

                this.$element.trigger(e)

                if (!this.isShown || e.isDefaultPrevented()) return

                this.isShown = false

                $('body').removeClass('modal-open')

                this.escape()

                $(document).off('focusin.modal')

                this.$element
                    .removeClass('in')
                    .attr('aria-hidden', true)

                $.support.transition && this.$element.hasClass('fade') ?
                    this.hideWithTransition() :
                    this.hideModal()
            }

            , enforceFocus: function () {
                var that = this
                $(document).on('focusin.modal', function (e) {
                    if (that.$element[0] !== e.target && !that.$element.has(e.target).length) {
                        that.$element.focus()
                    }
                })
            }

            , escape: function () {
                var that = this
                if (this.isShown && this.options.keyboard) {
                    this.$element.on('keyup.dismiss.modal', function (e) {
                        e.which == 27 && that.hide()
                    })
                } else if (!this.isShown) {
                    this.$element.off('keyup.dismiss.modal')
                }
            }

            , hideWithTransition: function () {
                var that = this
                    , timeout = setTimeout(function () {
                    that.$element.off($.support.transition.end)
                    that.hideModal()
                }, 500)

                this.$element.one($.support.transition.end, function () {
                    clearTimeout(timeout)
                    that.hideModal()
                })
            }

            , hideModal: function (that) {
                this.$element
                    .hide()
                    .trigger('hidden')

                this.backdrop()
            }

            , removeBackdrop: function () {
                this.$backdrop.remove()
                this.$backdrop = null
            }

            , backdrop: function (callback) {
                var that = this
                    , animate = this.$element.hasClass('fade') ? 'fade' : ''

                if (this.isShown && this.options.backdrop) {
                    var doAnimate = $.support.transition && animate

                    this.$backdrop = $('<div class="modal-backdrop ' + animate + '" />')
                        .appendTo(document.body)

                    if (this.options.backdrop != 'static') {
                        this.$backdrop.click($.proxy(this.hide, this))
                    }

                    if (doAnimate) this.$backdrop[0].offsetWidth // force reflow

                    this.$backdrop.addClass('in')

                    doAnimate ?
                        this.$backdrop.one($.support.transition.end, callback) :
                        callback()

                } else if (!this.isShown && this.$backdrop) {
                    this.$backdrop.removeClass('in')

                    $.support.transition && this.$element.hasClass('fade') ?
                        this.$backdrop.one($.support.transition.end, $.proxy(this.removeBackdrop, this)) :
                        this.removeBackdrop()

                } else if (callback) {
                    callback()
                }
            }
        }


        /* MODAL PLUGIN DEFINITION
         * ======================= */

        $.fn.modal = function (option) {
            return this.each(function () {
                var $this = $(this)
                    , data = $this.data('modal')
                    , options = $.extend({}, $.fn.modal.defaults, $this.data(), typeof option == 'object' && option)
                if (!data) $this.data('modal', (data = new Modal(this, options)))
                if (typeof option == 'string') data[option]()
                else if (options.show) data.show()
            })
        }

        $.fn.modal.defaults = {
            backdrop: true
            , keyboard: true
            , show: true
        }

        $.fn.modal.Constructor = Modal


        /* MODAL DATA-API
         * ============== */

        $(function () {
            $('body').on('click.modal.data-api', '[data-toggle="modal"]', function (e) {
                var $this = $(this)
                    , href = $this.attr('href')
                    , $target = $($this.attr('data-target') || (href && href.replace(/.*(?=#[^\s]+$)/, ''))) //strip for ie7
                    , option = $target.data('modal') ? 'toggle' : $.extend({remote: !/#/.test(href) && href}, $target.data(), $this.data())

                e.preventDefault()

                $target
                    .modal(option)
                    .one('hide', function () {
                        $this.focus()
                    })
            })
        })

    }(window.jQuery);

    return xy_listener;

})
