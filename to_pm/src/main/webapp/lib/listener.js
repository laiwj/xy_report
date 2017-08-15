define([
    'jquery',
	'colorbox'
],function($){


var xy_listener =  {

		/**
		 * 默认全局监听
		 */
		initDefaultListener: function() {
			this.setMenuSelected();

			$('body').on('click.tab.data-api', '[data-toggle="tab"], [data-toggle="pill"]', function (e) {
		      e.preventDefault()
		      $(this).tab('show')
		    })

            //菜单监听
            $(document).on('click.[data-toggle="menu"]', 'div.accordion-heading' ,function (e) {

                $("#side_accordion .accordion-heading ").removeClass("md-accent-bg");
                $(e.target).parent('.accordion-heading').addClass("md-accent-bg");
                e.stopPropagation;
            })

			//弹框关闭
			$(document).on('click.J_cancel_dialog', '.J_cancel_dialog' ,function (e) {
				$.colorbox.close();
			})


			/*
			 * 文本框最大长度限制,监听键盘输入
			 */
		/*	$(document).on("keyup.maxlength mousemove.maxlength paste.maxlength", "textarea[maxlength]", function (event) {
				var maxlen = parseInt($(this).attr('maxlength'));
				var text = $(this).val();
				var chars = text.length;

				if (chars > maxlen) {
					var new_text = text.substr(0, maxlen);
					$(this).val(new_text);
				}
			});*/

			/*
			 * 文本框最大长度限制,监听粘贴
			 */
			$(document).on("paste.maxlength", "textarea[maxlength]", function (event) {
				var maxlen = parseInt($(this).attr('maxlength'));
				if(window.clipboardData && window.clipboardData.setData){
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
			$(document).on("click.checkbox", "cite[type='checkbox']", function(event){
				xy_util.stopPop(event);
				$(document).click();
				var me = $(event.target);
				if ( me.hasClass("disabled") )
				{
					return;
				}
				me.toggleClass("checked");
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
            $(document).on('click.showMenu','div.J_show_menu',function(e){
               var clientWidth= document.body.clientWidth;
                if(clientWidth<960){
                    $(".J_show_menu").toggleClass("sidebar_show");

                    if ($('#toolbar').hasClass('open')) {
                        $('#toolbar').removeClass('open');
                    }
                    else {
                        $('#toolbar').addClass('open');
                    }
                }
                e.stopPropagation;
            })
			//浏览器
			//$(window).bind('beforeunload',function(){$.cookie(location.host+"_userInfoResult","");});
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
		/**
		 * 窗口大小变更监听
		 */
		initResizeListener: function()
		{
			$(window).resize(function()
			{
				resize.window();
			});
			resize.window();
		},

		/**
		 * 自动较验监听
		 */
		initValidatorListener: function()
		{
			xy_validator.initAutoValidator();
		},


		/**
		 * 程序异常监听
		 * @ignore
		 */
		initErrorListerner: function()
		{

			window.onerror = function(msg, url, line){
				try
				{
					console.log(msg
					 + "\nurl:" + url
					 + "\nat line:" + line);
				}
				catch(e)
				{
					//
				}
				return true;
			};
		}
	};


    return xy_listener;

})
