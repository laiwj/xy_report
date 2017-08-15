/**
 * String.replaceAll
 * 
 * @param {String} s1
 * @param {String} s2
 * @returns {String}
 */
String.prototype.replaceAll = function(s1, s2) {
	try
	{
		return this.replace(new RegExp(s1, "gm"), s2);
	}
	catch(e)
	{
		return this;
	}
};

/**
 * String.toSqlString
 * 
 * @param {String} s1
 * @param {String} s2
 * @returns {String}
 */
String.prototype.toSqlString = function() {
	try
	{
		return this.replaceAll("/", "//");
	}
	catch(e)
	{
		return this;
	}
};

/**
 * String.startWidth
 * 
 * @param {String} str
 * @returns {Boolean}
 */
String.prototype.startWith = function(str){
	var reg = new RegExp("^"+str);
	return reg.test(this);
};

/**
 * Date.format
 * 
 * @param {String} format    format(default:"yyyy-MM-dd")
 * @returns {String}
 */
Date.prototype.format = function(format)
{
	var o = {
		"M+" : this.getMonth() + 1,
		"d+" : this.getDate(),
		"H+" : this.getHours(),
		"m+" : this.getMinutes(),
		"s+" : this.getSeconds(),
		"q+" : Math.floor((this.getMonth() + 3)/3),
		"S+" : this.getMilliseconds()
	};
	
	if ( !format )
	{
		format = "yyyy-MM-dd";
	}
	
	if(/(y+)/.test(format))
	{
		format = format.replace(RegExp.$1,(this.getFullYear()+"").substr(4-RegExp.$1.length));
	}
	
	if(/(S+)/.test(format))
	{
		format = format.replace(RegExp.$1,(this.getMilliseconds()+"").substr(3-RegExp.$1.length));
	}
	
	for(var k in o)
	{
		if(new RegExp("(" +k + ")").test(format))
		{
			format = format.replace(RegExp.$1,RegExp.$1.length == 1?o[k]:("00"+o[k]).substr((""+o[k]).length));
		}
	}
	return format;
};

/**
 * Date.getCNDay    获取日期是生期几,返回中文字符
 * 
 * @returns {String}    星期几
 */
Date.prototype.getCNDay = function()
{
	var cnDay = {
		"0": "星期日",
		"1": "星期一",
		"2": "星期二",
		"3": "星期三",
		"4": "星期四",
		"5": "星期五",
		"6": "星期六"
	};
	
	return cnDay[this.getDay()];
};

/**
 * Date.setCNDate    设置date对象指定的日期
 * 
 * @param {String} str    指定的日期(格式: yyyy-MM-dd HH:mm:ss:SSS,支持不完全设置)
 */
Date.prototype.setCNDate = function(str)
{
	if ( !str )
	{
		return;
	}
	var strs = $.trim(str).split(" ");

	var dates = strs[0].split("-");
	
	//年
	var year = "19" + dates[0];
	year = parseInt(year.substring(year.length - 4, year.length), 10);
	if ( isNaN(year) )
	{
		year = this.getFullYear();
	}
	
	//月
	var month = this.getMonth();
	if ( dates.length > 1 )
	{
		var _month = parseInt(dates[1], 10);
		if ( !isNaN(_month) )
		{
			month = _month - 1;
		}
	}
	
	//日
	var date = this.getDate();
	if ( dates.length > 2 )
	{
		var _date = parseInt(dates[2], 10);
		if ( !isNaN(_date) )
		{
			date = _date;
		}
	}
	
	//设置年月日
	this.setFullYear(year, month, date);
	
	if ( strs.length < 2 )
	{
		return;
	}
	
	//设置时间
	var times = strs[1].split(":");
	
	//时
	var hour = this.getHours();
	var _hour = parseInt(times[0], 10);
	if ( !isNaN(_hour) )
	{
		hour = _hour;
	}
	
	//分
	var min = this.getMinutes();
	if ( times.length > 1 )
	{
		var _min = parseInt(times[1], 10);
		if ( !isNaN(_min) )
		{
			min = _min;
		}
	}
	
	//秒
	var sec = this.getSeconds();
	if ( times.length > 2 )
	{
		var _sec = parseInt(times[2], 10);
		if ( !isNaN(_sec) )
		{
			sec = _sec;
		}
	}
	
	//毫秒
	var ms = this.getMilliseconds();
	if ( times.length > 3 )
	{
		var _ms = parseInt(times[3], 10);
		if ( !isNaN(_ms) )
		{
			ms = _ms;
		}
	}
	
	this.setHours(hour, min, sec, ms);
	
};

/**
 * Array.joinAll
 * 
 * @param {String} seperator
 * @returns {String}
 */
Array.prototype.joinAll = function(seperator)
{
	for ( var i = 0, len = this.length; i < len; i++ )
	{
		if ( this[i] instanceof Array )
		{
			this[i] = this[i].joinAll(seperator);
		}
	}
	return this.join(seperator);
};

/**
 * Array.last
 * 
 * @returns {Object}
 */
Array.prototype.last = function()
{
	return this[(this.length > 0 ? this.length - 1 : 0)];
};

