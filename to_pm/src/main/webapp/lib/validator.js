/**
 * @fileOverview
 * @version 1.0
 */
define(["prototype", "msg"],function(prototype, xy_msg){
	var xy_validator =  {

		/**
		 * 初始化较验
		 *
		 * @private
		 */
		initAutoValidator: function()
		{
			var me = this;
			//TODO:
			//keyup.validator
			$(document).on("blur.validator", "[validator]",function(event){
				me.validator(event.target,true);
			});

			/*$(document).on("blur.validator", , function(event){
			 me.validator(event.target);
			 });*/


			/*$(document).on("keyup.validator blur.validator",
			 "[validator], input[type='text'], input[type='password']",
			 function(event){
			 me.validator(event.target);
			 }
			 );*/
		},

		/**
		 * 获取较验规则
		 *
		 * @param {Object} obj    JS DOM节点
		 * @returns {String}
		 */
		getValidatorRules: function(obj)
		{
			var rules = [];
			if ( $(obj).attr("validator") != null )
			{
				rules = $.trim($(obj).attr("validator")).split(" ");
			}

			if ( !$(obj).closest("[removeDefaultRules]").length )
			{
				rules.reverse();
				rules.push("char");
				rules.reverse();
			}

			return rules;
		},

		/**
		 * 获取需要较验的值
		 *
		 * @param {Object} obj    JS DOM节点
		 * @returns {String}
		 */
		getValidatorValue: function(obj)
		{
			//初始化返回值
			var value = "";

			//检查节点标签
			var tagName = obj.tagName;
			switch (tagName.toUpperCase())
			{
				case "INPUT":
				case "TEXTAREA":
					value = $.trim($(obj).val());
					break;

				case "CITE":
					//radio 插件
					if ( $(obj).attr("type") == "radio" )
					{
						var appName = $(obj).attr("name");

						//真正取值的对象
						var valueObj = $("cite[name='" + appName + "'].checked");

						//有选中节点 且 选中节点有值
						if ( valueObj.length > 0 && this.notNullOrEmpty(valueObj.eq(0).attr("value")))
						{
							value = valueObj.eq(0).attr("value");
						}
					}
					//checkbox插件
					else if ( $(obj).attr("type") == "checkbox" )
					{
						var appName = $(obj).attr("name");
						var valueBuffer = [];
						$("cite[name='" + appName + "'].checked").each(function(){
							valueBuffer.push($(this).attr("value"));
						});
						value = valueBuffer.join(",");
					}
					//标准元素
					else
					{
						value = $.trim($(obj).text());
					}
					break;

				default:
					value = $.trim($(obj).text());
					break;
			}

			return value.toString();
		},

		/**
		 * 获取错误提示对象
		 *
		 * @param {Object} obj    JS DOM节点
		 * @returns {jQuery}      jQuery对象
		 */
		getErrorTips: function(obj,flag)
		{

			//已指定错误提示载体,用指定的载体替换
			if ( $(obj).attr("errorTipsId") && $("#" + $(obj).attr("errorTipsId")).length > 0 )
			{

				return $("#" + $(obj).attr("errorTipsId")).removeClass("u-error").addClass("u-error");
			}

			//默认取同级的错误提示元素
			if ( $(obj).siblings("p.errorTips").length > 0 )
			{
				$(obj).siblings("p.errorTips").removeClass("u-error").addClass("u-error");
				return $(obj).siblings("p.errorTips").eq(0);
			}

			//没有同级的错误提示元素,在父节点的末位创建
			$(obj).parent().append("<p class='u-error errorTips closeToHide'></p>");
			return $(obj).siblings("p.errorTips").eq(0);
		},

		/**
		 * 获取错误提示关键字
		 *
		 * @param {Object} obj    JS DOM节点
		 * @returns {String}      关键字
		 */
		getErrorTipsKey: function(obj)
		{
			return $(obj).attr("errorTipsKey") || "";
		},

		/**
		 * 执行指定元素较验
		 *
		 * @param {Object} obj    指定较验的JS DOM节点
		 * @param {Boolean}flag   失焦时不做空验证
		 * @returns {Boolean}     较验结果
		 */
		validator: function(obj,flag)
		{
			var me = this;
			//检查节点类型
			var nodeType = obj.nodeType;
			if ( nodeType != 1 )
			{
				return true;
			}

			//检查较验规则
			var rules = this.getValidatorRules(obj);

			//较验值
			var value = this.getValidatorValue(obj);

			//错误提示元素
			var errorTips = this.getErrorTips(obj,flag);

			//错误提示关键字
			var errorTipsKey = this.getErrorTipsKey(obj);
			//same属性较验
			if ( $.inArray("same", rules) > -1 && !this.sameValidator(obj) )
			{
				return false;
			}

			//值为空,且rules里没有非空较验,返回成功
			if ( value == "" && $.inArray("notNullOrEmpty", rules) == -1 &&  $.inArray("notNullOrEmptySelect", rules)== -1 )
			{
				errorTips.removeAttr("title");
				errorTips.hide();
				return true;
			}
			//常规较验
			try
			{

				for ( var i = 0, len = rules.length; i < len; i++ )
				{
					if ( rules[i] == "same" )
					{
						continue;
					}

					//较验结果
					var ruleFlag = this[rules[i]](value, obj);
					//较验未通过
					if ( !ruleFlag )
					{
						//失焦时不做空验证
						if(rules[i] == "notNullOrEmpty" && flag == true){
							errorTips.hide();
							return false;
						}
						if(rules[i] == "notNullOrEmptySelect"){
							$(obj).parent(".select-warp").addClass("error-input");
						}
						$(obj).parent().find("input").addClass("error-input");
						var errorMsg = xy_validator.getMessage(xy_msg["VALIDATOR_" + rules[i].toUpperCase()], errorTipsKey);
						//errorTips.attr("title", htmlTitleEncode(errorMsg));
						errorTips.text(me.htmlTitleEncode(errorMsg));
						errorTips.show();
						return false;
					}else{
						if(rules[i] == "notNullOrEmptySelect"){
							$(obj).parent(".select-warp").removeClass("error-input");
						}
					}
				}



				//attr属性较验
				var validatorAttrList = [["min", "max"]];
				for ( var i = 0, len = validatorAttrList.length; i < len; i++ )
				{
					if ( !this.attrValidator(obj, validatorAttrList[i]) )
					{
						return false;
					}
				}

				errorTips.removeAttr("title");
				errorTips.hide();
				errorTips.parent().find("input").removeClass("error-input");
				return true;
			}
			catch(e)
			{
				var errorMsg = xy_validator.getMessage(xy_msg["VALIDATOR_UNKNOWNERROR"]);
				//errorTips.attr("title", xy_util.htmlTitleEncode(errorMsg));
				errorTips.text(me.htmlTitleEncode(errorMsg));
				errorTips.show();
				return false;
			}
		}, //-- validator END

		/**
		 * 指定范围较验
		 *
		 * @param {Object} obj    较验的jQuery DOM节点
		 * @returns {Boolean}     较验结果
		 */
		autoValidator: function(obj)
		{
			var result = true;

			var me = this;
			obj.find("[validator]").each(function(){
				var res = me.validator(this);
				if ( !res )
				{
					result = res;
				}
			});

			return result;
		},

		/**
		 * same属性较验
		 *
		 * @returns {Boolean}
		 *
		 * @private
		 */
		sameValidator: function(obj)
		{
			var me =  this;
			//较验值
			var value = this.getValidatorValue(obj);

			//错误提示元素
			var errorTips = this.getErrorTips(obj);

			//错误提示关键字
			var errorTipsKey = this.getErrorTipsKey(obj);


			//未指定较验对象
			if ( this.nullOrEmpty($(obj).attr("equalTo")) )
			{
				return true;
			}

			//获取较验对象的值
			var targetDom = $($(obj).attr("equalTo"))[0];
			var targetValue = this.getValidatorValue(targetDom);
			//较验相同值
			if ( targetValue != value )
			{
				var errorMsg = xy_validator.getMessage(xy_msg["VALIDATOR_SAME"], errorTipsKey);
				//errorTips.attr("title", xy_util.htmlTitleEncode(errorMsg));
				errorTips.text(me.htmlTitleEncode(errorMsg));
				errorTips.show();
				return false;
			}

			return true;
		},

		/**
		 * 扩展属性较验
		 *
		 * @returns {Boolean}
		 *
		 * @private
		 */
		attrValidator: function(obj, attr)
		{
			var me = this;
			//较验值
			var value = this.getValidatorValue(obj);

			//错误提示元素
			var errorTips = this.getErrorTips(obj);

			//错误提示关键字
			var errorTipsKey = this.getErrorTipsKey(obj);

			//数组类型较验
			if ( attr instanceof Array )
			{
				for ( var i = 0, len = attr.length; i < len; i++ )
				{
					//较验成功
					if ( this.attrValidator(obj, attr[i]) )
					{
						continue;
					}

					//较验失败
					var errorMsg = this.getErrorMsg(obj, attr);
					//errorTips.attr("title", xy_util.htmlTitleEncode(errorMsg));
					errorTips.text(me.htmlTitleEncode(errorMsg));
					errorTips.show();
					return false;
				}
				return true;
			}

			//标准较验
			attr += "";
			if ( this.nullOrEmpty($(obj).attr(attr)) )
			{
				return true;
			}

			if ( this[attr](value, $(obj).attr(attr)) )
			{
				return true;
			}

			var key = "VALIDATOR_" + attr.toUpperCase();
			var errorMsg = xy_validator.getMessage(xy_msg[key], errorTipsKey);
			//errorTips.attr("title", xy_util.htmlTitleEncode(errorMsg));
			errorTips.text(me.htmlTitleEncode(errorMsg));
			errorTips.show();
			return false;
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
		},/*
		/**
		 * 获取错误提示语
		 *
		 * @private
		 */
		getErrorMsg: function(obj, list)
		{
			var me = this;
			//错误提示关键字
			var errorTipsKey = this.getErrorTipsKey(obj);

			var errorMsgBuffer = [];
			var key = "";
			var msg = "";
			for ( var i = 0, len = list.length; i < len; i++ )
			{
				key = "VALIDATOR_" + list[i].toUpperCase();

				if ( this.nullOrEmpty($(obj).attr(list[i])) )
				{
					continue;
				}

				msg = errorMsgBuffer.length ? xy_msg[key].replaceAll("\\{1\\}", "") : xy_msg[key];
				errorMsgBuffer.push(xy_validator.getMessage(msg, errorTipsKey, $(obj).attr(list[i])));
			}

			return errorMsgBuffer.join(",");
		},
		/**
		 * HTML代码中的title属性编码
		 *
		 * @param   {String} objVal    需要编码的字符串
		 * @returns {String}           编码后的字符串
		 */
		htmlTitleEncode: function(objVal)
		{
			if ( this.nullOrEmpty(objVal) )
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
		 * 基础较验
		 *
		 * @param {Object} str
		 * @returns {Boolean}
		 *
		 * @from Admin
		 */
		char: function(str)
		{
			//允许字符串中间有空格，不允许头或尾有空格，不允许半角加号，因为级联时会丢失
			return (/^[\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;:#]*$|^([\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;:#])+([\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;:# ])+([\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;:#])+$/.test(str));
		},

		/**
		 * null较验
		 *
		 * @param {Object} str
		 * @returns {Boolean}
		 */
		isNull: function(str)
		{
			return null == str;
		},

		/**
		 * 空值较验
		 *
		 * @param {Object} str
		 * @returns {Boolean}
		 */
		isEmpty: function(str)
		{
			return null != str && str.length == 0;
		},

		/**
		 * 不为空较验
		 *
		 * @param {Object} str
		 * @returns {Boolean}
		 */
		notNullOrEmpty: function(str)
		{
			return !xy_validator.nullOrEmpty(str);
		},
		/**
		 *选择框不为空校验
		 */
		notNullOrEmptySelect: function(str)
		{
			return !xy_validator.nullOrEmpty(str);
		},

		/**
		 * 为空较验
		 *
		 * @param {Object} str
		 * @returns {Boolean}
		 */
		nullOrEmpty: function(str)
		{
			return null == str || str.length == 0;
		},

		/**
		 * 数字较验
		 *
		 * @param {Object} str
		 * @returns {Boolean}
		 */
		number: function(str)
		{
			return str == parseInt(str, 10)
				&& (str + "").indexOf("-") == -1
				&& (str + "").indexOf(".") == -1;
				//&& (str == 0 || (str + "").indexOf("0") != 0);
		},
		/**
		 * 不能为负数,0
		 *
		 * @param {Object} str
		 * @returns {Boolean}
		 */
		numberPoint : function(str){
			return (/^(?!(0[0-9]{0,}$))[0-9]{1,}[.]{0,}[0-9]{0,}$/.test(str));
		},
		/**
		 * 邮件地址较验
		 *
		 * @param {String} str
		 * @returns {Boolean}
		 */
		email: function(str)
		{
			//return (/^$|^(([a-zA-Z0-9])+([a-zA-Z0-9-_.]{1,})@([a-zA-Z0-9])+.[a-zA-Z0-9\.\_]{2,})$/.test(str));
            //modify 邮箱基本格式验证 by zhanghaibing on 2016/1/23

			/*return (/^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+[.]{1}){1,63}[a-z0-9]+$/.test(str));*/
			return (/^([a-z0-9A-Z]+[-|\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\.)+[a-zA-Z]{2,}$/.test(str));
		},
		/**
		 * 多邮箱校验
		 *
		 * @param {String} str
		 * @returns {Boolean}
		 */
		moreEmail : function(str){
			//return (/^$|^(([a-zA-Z0-9])+([a-zA-Z0-9-_.]{1,})@([a-zA-Z0-9])+.[a-zA-Z0-9\.\_]{2,};?)+$/.test(str));
			return (/^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.{1}){1,63}[a-z0-9]+$/.test(str));
		},
		/*
		* 邮箱 手机
		* */
		phoneEmail:function(str){

			var phone = /^((0?1\d{10})|((0(\d{2,3}))[\-]?\d{7,8}))$|^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})$/.test(str);
			var email = /^([a-z0-9A-Z]+[-|\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\.)+[a-zA-Z]{2,}$/.test(str);
			if(phone || email){
				return true;
			}


		},
    		/**
		 * 非主流邮件地址较验
		 *
		 * @param {String} str
		 * @returns {Boolean}
		 */
		companyEmail: function(str)
		{
			return !(/^$|^(([a-zA-Z0-9])+([a-zA-Z0-9-_.]{1,})@(qq|163|126|sina|gmail|outlook|yeah|aliyun|139|189).[a-zA-Z0-9\.\_]{2,})$/.test(str));
		},
		/**
		 * 密码位数校验
		 *
		 * @param {String} str
		 * @returns {Boolean}
		 */
		pwdlength: function(str)
		{
			return (/^$|^(([a-zA-Z0-9]){8,16})$/.test(str));
		},
		/**
		 * 密码格式为字母与数字组合且为8-16位数
		 *
		 * @param {String} str
		 * @returns {Boolean}
		 */
		pwdFormat: function(str)
		{
			return (/^(?!\d+$)(?![A-Za-z]+$)[a-zA-Z0-9]{8,16}$/.test(str));
		},
		/**
		 * NOTES地址较验
		 *
		 * @param {Object} str
		 * @returns {Boolean}
		 *
		 * @from Admin
		 */
		notes: function(str)
		{
			return (/^[\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;#@]*$|^([\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;#@])+([\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;#@ ])+([\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;#@])+$/.test(str));
		},

		/**
		 * 电话号码较验
		 *
		 * @param {String} str
		 * @returns {Boolean}
		 */
		phone: function(str)
		{
			return (/^([0-9\-]{5,})?$/.test(str));
		},
		telPhone: function(str){
			return (/^((0?1\d{10})|((0(\d{2,3}))[\-]?\d{7,8}))$|^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})$/.test(str));
		},
		/**
		 * 备注长度较验
		 *
		 * @param {String} str
		 * @returns {Boolean}
		 */
		memo: function(str)
		{
			return str.length > 250;
		},

		/**
		 * defined类型较验
		 *
		 * @param {Object} str
		 * @returns {Boolean}
		 */
		isDefined: function(ele)
		{
			return ele != null && typeof ele != "undefined";
		},
		isURL: function(str){
			return ((/^((https?|ftp|news):\/\/)?([a-z]([a-z0-9\-]*[\.。])+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\/[a-z0-9_\-\.~]+)*(\/([a-z0-9_\-\.]*)(\?[a-z0-9+_\-\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$/).test(str))
		},
		/**
		 * 身份证号码较验
		 *
		 * @param {String} str
		 * @returns {Boolean}
		 */
		identityCode: function(str)
		{
			//长度有效性较验
			if ( !(/^([\d]{15}|[\d]{17}[Xx\d])$/.test(str)) )
			{
				return false;
			}

			//出生年月日格式转换
			var dayStr;
			if ( str.length == 15 )
			{
				dayStr = "19" + str.substr(6,2)
				+ "-" + str.substr(8,2)
				+ "-" + str.substr(10,2);
			}
			else
			{
				dayStr = str.substr(6,4)
				+ "-" + str.substr(10,2)
				+ "-" + str.substr(12,2);
			}

			var brithday = new Date();
			brithday.setCNDate(dayStr);

			//较验出生日期有效性
			return dayStr == brithday.format();
		},

		/**
		 * 生日
		 * @param {String} str yyyy-MM-dd
		 *
		 */
		brithday: function(str){

			//长度有效性较验
			if ( !(/^([\d]{4}-[\d]{2}-[\d]{2})$/.test(str)) )
			{
				return false;
			}

			var brithday = new Date();
			brithday.setCNDate(str);

			//较验出生日期有效性
			return str == brithday.format();
		},

		/**
		 * 带宽
		 *
		 * @param {String} str
		 * @returns {Boolean}
		 */
		bandWidth: function(str)
		{
			return (/^\d+(.\d+)?$/.test(str));
		},

		/**
		 * 最小值验证
		 *
		 * @param {String} str    目标值
		 * @param {String} min    最小值
		 * @returns {Boolean}
		 */
		min: function(str, min)
		{
			var num = parseInt(str, 10);
			var min = parseInt(min, 10);
			if ( isNaN(num) || isNaN(min) )
			{
				return false;
			}
			return min <= num;
		},

		/**
		 * 最大值验证
		 *
		 * @param {String} str    目标值
		 * @param {String} max    最大值
		 * @returns {Boolean}
		 */
		max: function(str, max)
		{
			var num = parseInt(str, 10);
			var max = parseInt(max, 10);
			if ( isNaN(num) || isNaN(max) )
			{
				return false;
			}
			return max >= num;
		},

		/**
		 * 两位小数点
		 *
		 * @param {Object} str
		 * @returns {Boolean}
		 */
		decimalPoint : function(str){
			var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;
			//格式较验
			if ( !(reg.test(str)) )
			{
				return false;
			}
			return true;
		}

	};

	return xy_validator;
})
