define(["validator"],function( xy_validator){
var xy_util =  {
		
		/**
		 * 获取地址栏参数
		 * 
		 * @param   {String} name    参数名
		 * @returns {String}         参数值
		 */
		getPathParam: function(name)
		{
			var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
			var r = window.location.search.substring(1).match(reg);
			if ( r != null )
			{
				return decodeURIComponent(r[2]);
			}
			return "";
		},
		
		/**
		 * 获取地址栏参数
		 * @returns {Object}
		 */
		getUrlParms: function()    
		{
			 var args=new Object();   
			 var query=location.search.substring(1);//获取查询串   
			 var pairs=query.split("&");//在逗号处断开   
			 for(var i=0;i<pairs.length;i++)   
			 {   
			     var pos=pairs[i].indexOf('=');//查找name=value   
			        if(pos==-1)   continue;//如果没有找到就跳过   
			         var argname=pairs[i].substring(0,pos);//提取name   
			        var value=pairs[i].substring(pos+1);//提取value   
			        args[argname]=unescape(value);//存为属性   
			}
			 return args;
		},
		
		/**
		 * head部分增加JS文件引用
		 * 
		 * @param {String} path    JS文件引用地址
		 */	
		createHeaderJSElement: function(path)
		{
			var element = document.createElement("script");
			element.setAttribute("type", "text/javascript");
			element.setAttribute("src", path);
			document.getElementsByTagName("head")[0].appendChild(element);
		},
		
		/**
		 * head部分增加CSS文件引用
		 * 
		 * @param {String} path    CSS文件引用地址
		 */	
		createHeaderCSSElement: function(path)
		{
			var element = document.createElement("link");
			element.setAttribute("type", "text/css");
			element.setAttribute("rel", "stylesheet");
			element.setAttribute("href", path);
			document.getElementsByTagName("head")[0].appendChild(element);
		},
		
		/**
		 * HTML编码
		 * 
		 * @param   {String} objVal    需要编码的字符串
		 * @returns {String}           编码后的字符串
		 */
		htmlEncode: function(objVal)
		{
			var str = objVal + "";
			if(str == '')
			{
				return str;
			}
			str = str.replace(/&/g, "&amp;")
				.replace(/</g, "&lt;")
				.replace(/>/g, "&gt;")
				.replace(new RegExp("\"","g"), "&quot;")
				.replace(new RegExp("\'","g"), "&#39;")
				.replace(new RegExp("  ","g"), " &nbsp;")
				.replace(new RegExp("\r|\n","g"),"<br/>");
			return str;
		},
		
		/**
		 * HTML代码中的title属性编码
		 * 
		 * @param   {String} objVal    需要编码的字符串
		 * @returns {String}           编码后的字符串
		 */
		htmlTitleEncode: function(objVal)
		{
			if ( xy_validator.nullOrEmpty(objVal) )
			{
				objVal = "";
			}
			
			var str = objVal + "";
			if(str == '')
			{
				return str;
			}
			str = str.replace(/&/g, "&amp;")
				.replace(/</g, "&lt;")
				.replace(/>/g, "&gt;")
				.replace(new RegExp("\"","g"), "&quot;")
				.replace(new RegExp("\'","g"), "&#39;")
				.replace(new RegExp("\r|\n","g"), "&#13");
			return str;
		},
		
		/**
		 * HTML解码
		 * 
		 * @param   {String} objVal    需要解码的字符串
		 * @returns {String}           解码后的字符串
		 */
		htmlDecode: function(objVal)
		{
			var str = objVal + "";
			if(str == '')
			{
				return str;
			}
			str = str.replace(/&lt;/g, "<")
				.replace(/&gt;/g, ">")
				.replace(/&quot;/g, "\"")
				.replace(/&#39;/g, "\'")
				.replace(/ &nbsp;/g, "  ")
				.replace(/&amp;/g, "&");
			return str;
		},
		
		tipsDIV: function(message, time, icon, callback,icon){
			jTip(xy_util.htmlEncode(message), time, callback,icon );
		},
		/**
		 * 初始化包装的alert弹窗
		 *
		 * @param {String}   message       提示语
		 * @param {function} callback      弹窗关闭的回调事件
		 */
		alertDIV: function(message,callback, icon)
		{
			jAlert(xy_util.htmlEncode(message),callback, icon);
		},

		/**
		 * 初始化包装的confirm弹窗
		 *
		 * @param {String}   message       提示语
		 * @param {function} callback      回调事件
		 *
		 * @example
		 * confirmDIV("message", function(res){
		 *     if (res)
		 *     {
		 *         return "点击的是'确定'按钮";
		 *     }
		 *     else
		 *     {
		 *         return "点击的是'取消'按钮或'关闭'按钮";
		 *     }
		 * });
		 */
		confirmDIV: function(message, callback,icon, showIframe)
		{
			jConfirm (message, callback, icon,showIframe);
		},
		
		/**
		 * 获取标准信息
		 * 
		 * @param   {String} key    key
		 * @returns {String}        xy_msg对象对应的value值,如不存在指定的key则返回key值
		 */
		getMsg: function(key)
		{
			var msg = xy_msg[key];
			if(null == msg || "" == msg)
			{
				msg = key;
			}
			return msg;
		},
		
		/**
		 * 替换动态消息
		 * @param   {String} msg          替换前的字符串
		 * @param   {String} arguments    替换的字符串(不定数量)
		 * @returns {String} msg          替换后的字符串
		 * 
		 * @example
		 * getMessage();                                     //return "";
		 * getMessage("message");                            //return "message";
		 * getMessage("message", "str");                     //return "message";
		 * getMessage("message {1}", "str");                 //return "message str";
		 * getMessage("message {2}", "str");                 //return "message {2}";
		 * getMessage("message {1}", "str", "hello");        //return "message str";
		 * getMessage("message {1} {2}", "str", "hello");    //return "message str hello";
		 * getMessage("message {2}", "str", "hello");        //return "message hello";
		 */
		getMessage: function(msg)
		{
			if(arguments.length == 0)
			{
				return "";
			}
			
			msg += "";
			for(var i=1; i<arguments.length; i++)
			{
				var key = "\\{" + i + "\\}";
				var value = arguments[i] + "";
				msg = msg.replaceAll(key, value);
			}
			return msg;
		},
		
		/**
		 * rest接口callback统一处理方法
		 * 
		 * @param {Object}   result        rest接口返回值
		 * @param {Object}   callback      Function类型:  处理完成回调方法,含回调参数result.value, 
		 *                                 String类型:    成功操作提示语
		 * @param {Function} onError       错误回调函数
		 */
		restCallback: function(result, callback, onError)
		{
			try
			{
				//登录失效
				if ( result.code == -10 ){
					//var msg = this.getMessage(xy_resultCode[result.retcode]);
					var msg = result.msg;
					jAlert(msg, function(){
							location.href = '/user/logout';
					});
					return;
				}
				
				if ( result.code != 0 )
				{
					//var msg = this.getMessage(xy_resultCode[result.retcode], result.text);
					var msg = result.msg;
					if ( onError )
					{
						xy_util.hideLock();
						onError(result.code, result, msg);
						return;
					}
					
					xy_util.alertDIV(msg, function(){
						
					});
					xy_util.hideLock();
					return;
				}
				xy_util.hideLoadingCard();
				xy_util.hideLock();
				switch( typeof(callback) )
				{
					//提示语
					case "string":
						alertDIV(callback);
						break;
						
					//回调函数
					case "function":
						callback(result.data);
						break;
	
					default:
						break;
				}
			}
			catch(e)
			{
				console.log(e);
				xy_util.hideLock();
				//error
			}
		},
		
		
		
		/**
		 * 显示loading
		 * 
		 * @param {String}   text          提示语(默认'loading...')
		 * @param {Function} callback      显示完成回调事件
		 */
		showLoadingCard: function(obj)
		{
			//var width = obj.width();
			//var height = obj.height();

			var lock_overlay = $("#loading_small");
			if(lock_overlay.length !=0 ){
				//lock_overlay.css({width:width+'px',height:height+'px'});
				lock_overlay.show();
			}else{
				lock_overlay = '<div id="loading_small"><img src="/static/img/ajax_loader.gif" alt="" /></div>';
				$("#navigation").append(lock_overlay);
			}
			//obj.addClass("position-re");

		},

		/**
		 * 隐藏loading
		 */
		hideLoadingCard: function()
		{   
			$("#loading_small").hide();
		},
		
		
		
		
		/**
		 * 垃圾回收方法(for IE)
		 * 
		 * @param {jQuery} obj
		 */
		callbackRb : function(obj)
		{
			if ($.browser.msie)
			{
				obj.appendTo($("#rubbish"));
				$("#rubbish").html("");
			}
			else
			{
				obj.remove();
			}
		},
		
		/**
		 * 禁用事件冒泡
		 * 
		 * @param {Event} e         event
		 */
		stopPop: function(e)
		{
			try
			{
				e.stopPropagation();
			}
			catch(e)
			{
				e.cancelBubble;
			}
		},
		isIe: function(){
			if( $.browser.msie){
				return true;
			}
			return false;
		},
		onloadImg: function(path,callback, onError){
			var img = new Image();

			img.onload = function(){
				if(callback && typeof callback == "function"){
					callback(path);
				}
			}
			if(!img.complete){
				var t = setInterval(function(){
					if(img.complete){
						if(callback && typeof callback == "function"){
							callback(path);
						}
						clearInterval(t);
						return false;
					}
				},500)
			}
			img.onerror = function(){
				if(onError && typeof onError == "function"){
					onError();
				}
			}
			img.src = path;
		},
		
		/**
		 * 在文档其他地方点击时隐藏下拉元素
		 */
		hiddenOnMouseDown: function(strId){
			document.onmousedown = function(){
				var obj=$("#"+strId);
				if(obj.is(":visible") == true) {
					obj.hide();
				}
			};
			 $("#"+strId).mousedown(function(event){
				 event.stopPropagation();
			 });
		},
		
		/**
		 * 
		 * 获取当前访问的地址栏路径
		 */
		getUrlAddress: function(){
			 var href = window.location.href;
			 var address = href.split('/');
			 return address[0] + '//' + address[1] + address[2] + '/' + address[3];
		},
		lockScreen: function(){
			var width = document.documentElement.clientWidth;
			var height = document.documentElement.clientHeight+document.documentElement.scrollHeight;
			//var scrlloTop = $(window).clientHeight();

			//$("body").addClass("overflow-hidden");
			var lock_overlay = $("#loading_layer");
			if(lock_overlay.length !=0 ){
				lock_overlay.css({width:width+'px',height:height+'px'});
				lock_overlay.show();
			}else{
				lock_overlay = '<div id="loading_layer" style="width:'+width+'px;height:'+height+'px;"><img src="/static/img/ajax_loader.gif" alt="" /></div>';
			}
			$("body").append($(lock_overlay));
		},
		hideLock: function(){
			$("#loading_layer").hide();
		},
		/**
		 *
		 * @param param
		 * 	fileBtnId
		 * 	accessoryId
		 * 	dataId
		 * 	photoId
		 *
		 * @param callback
		 */
		initImgUpload: function(param,callback){
			var uploadUrl = "/.tmp/"
			var me = this;
			var max = 5;
			var button = $("#" + param.fileBtnId);
			var allowExtention = ".jpg,.bmp,.png";
			if(param.accessoryId){
				button.on("mousedown", function(){
					if ( $("#" + param.accessoryId).children(".img-list:visible").length >= max )
					{
						alertDIV(STATICMSG["1018"]);
						return false;
					}
				})
			}

 			$('#'+param.formId).attr({enctype:"multipart/form-data", action:"/upload/photo/" ,method:"post"});

			$('#'+param.formId).submit(function() {
			        $(this).ajaxSubmit({
			            error: function(xhr) {
					            status('Error: ' + xhr.status);
			            },
			            success: function(result) {
			            	result = JSON.parse(result);
			            	$("#"+param.photoId).attr("src",uploadUrl+result.filename );
			            	$("#"+param.dataId).val(result.filename);

			            	xy_util.hideLock();

			            }
					});
				return false;
			  }); 

			button.change(function(){
				var arr = [];
				var fileName = button.val();
				arr = fileName.split("\\");
				fileName = arr[arr.length-1];
				var extention = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
				if (allowExtention.indexOf(extention) == -1){allowExtention
					alertDIV(xy_util.getMessage(STATICMSG["1019"],allowExtention));
					return false;
				}
				xy_util.lockScreen();

				$('#'+param.formId).trigger("submit");
				
			})

			//绑定附件删除功能
			$("#" + param.delBtnId).on("click", function(){
				$(this).hide();
				$("#" + param.dataId).val("");

				if ( xy_validator.nullOrEmpty(param.photoId) )
				{
					return;
				}

				//隐藏图片
				$("#" + param.photoId).parent().hide();
				//$("#" + param.photoId).hide();
				$("#" + param.photoId).attr("src", "");
			});

			$("#" + param.photoId).on("click", function(){
				window.open($(this).attr("src"));
			});
		},
		getAll:function(){
				var obj = {};
				var xmlhttp;
				if (window.XMLHttpRequest)
				{// code for IE7+, Firefox, Chrome, Opera, Safari
					xmlhttp=new XMLHttpRequest();
				}
				else
				{// code for IE6, IE5
					xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
				}
				xmlhttp.open("GET","/static/script/positionTmp.txt",false);
				xmlhttp.send();
				var a = {firstLevel:[],secondLevel:{}}, obj = a.secondLevel;
				xmlhttp.responseText.replace(/.+/g,function(url){
					var str = url.toString().split("\t");
					if($.inArray(str[0],a.firstLevel) == -1){
						a.firstLevel.push(str[0]);
					}

					if(!obj[str[0]]){
						obj[str[0]] = {};
					}

					//txt空格隔开以后的数组只有两级
					if(!str[2]){
						if(!obj[str[0]][str[0]]){
							obj[str[0]][str[0]] = [];
						}
						obj[str[0]][str[0]].push(str[1]);
					}else{
						if(!obj[str[0]][str[1]]){
							obj[str[0]][str[1]] = [];
						}
						obj[str[0]][str[1]].push(str[2]);
					}
				})
				return a;
			},
		/***生成多级选择的静态html代码
		 * option:{firstLevel:[a,b,c],secondLevel:{a:{a_child:[]},b:{b_child:[]}}}
		 *
		 * */
		 createMultilevelSelectHTML: function(option){
			 var me = this;
			 var arr = [];
			 var data = option.data || this.getAll();
			 arr.push('<div class="options-picker" style="display: none;">');
			 arr.push('<div><div class="first-level-content">');
			 arr.push('<div  _value="" class="pick-all-first"><input type="checkbox" class="check-all-first"/><span class="li-text ml5"><b>全部</b></span></div>');
			 var _data = data.firstLevel;
			var f_index = 0;
			 $.each(_data, function(i){
				 f_index++;
				 arr.push('<div  _value="'+_data[i]+'" class="picker-first-level"><input id="checkbox_'+(f_index)+'" type="checkbox" class="checkbox-first-item" value="'+_data[i]+'"/><span class="li-text ml5">'+_data[i]+'</span></div>');
			 })
			 arr.push('</div>')

			 arr.push('<div classs="next-level-content">');

			f_index = 0;
			 $.each(_data, function(i){
				  f_index++;
				 arr.push('<div class="picker-next-level" style="display: none;" _title="'+_data[i]+'">');

				 var _second = data.secondLevel[_data[i]];
				 if(!_second){
                     arr.push('</div>');
					 arr.push('</div>');
                     return;
                 }

				 if(!$.isEmptyObject(_second)){
                      arr.push('<div  class="pick-all-second ver-middle" ><input type="checkbox" class="check-all-second"/><span class="picker-second-title ml5">全部</span></div>');
                 }
                 var s_index = 0;
				 $.each(_second, function(k ,v){
					 arr.push('<div  class="mt10 mb10 ver-middle pick-second-item" style="text-align: left;">');
					 if(_data[i] != k){
						 s_index++;
						 arr.push('<input id="checkbox_'+f_index+'_'+(s_index)+'" type="checkbox"  class="checkbox-second-item" value="'+k+'"/><span class="picker-second-title ml5">'+k+'</span>');
					 }
					 arr.push('<div class="picker-three-level clearfix " style="display: block;">');
					 if(!v || v.length == 0){
						 arr.push('</div>');
						 arr.push('</div>');
						 return;
					 }
					 $.each(v, function(j){
						 if(!v[j+1]){
							 arr.push('<div  class="mt10 ver-middle" f-data="'+_data[i]+'" s-data="'+k+'"><input id="checkbox_'+f_index+'_'+s_index+'_'+(j+1)+'" type="checkbox"  class="checkbox-third-item" value="'+v[j]+'"/><span class="li-text ml5" >'+v[j]+'</span></div>');
						 }else{
							 arr.push('<div  class="mt10 ver-middle" f-data="'+_data[i]+'" s-data="'+k+'"><input id="checkbox_'+f_index+'_'+s_index+'_'+(j+1)+'" type="checkbox"  class="checkbox-third-item" value="'+v[j]+'"/><span class="li-text ml5"  >'+v[j]+'</span>&nbsp;&nbsp;|&nbsp;&nbsp;</div>');
						 }

					 })
					 arr.push('</div>');
					 arr.push('</div>');
				 })
				 arr.push('</div>');

			 })
			 arr.push('</div>')
			return arr.join("");
		 }

}
	return xy_util;
})