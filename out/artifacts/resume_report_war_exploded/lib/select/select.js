/**
* 下拉框控件
* <div class="select-warp" id="J_UserList">
	<label class="select-text" ></label>
	<input type="hidden" name="bean.staffJsonStr" m:id="staffJsonStr" id="staffJsonStr" />
    <input type="hidden" name="bean.staffIds" class="select-value"/>
</div>
									
var p = {
	domId:'#J_UserList', //必填
	options: [{text:'',value:22,tel:123456},{text:'bb',value:33,tel:456789}],//必填
	width: 200,//下拉宽度
	event: {
		onchange: function(obj, value, text){
			var tel = obj.tel || '';
			$('#assistant_deviceMaintainerph').val(tel);
		},
		onload: function(){
		
		}
	}
}
var selectControl = new SelectControl(p);
*/
/**
 * params.state
 * 
 */
define(["validator"], function( xy_validator ){
	var SelectControl = function(config){
		this.params = config;
		this.init();
	}
	/**
	 *
	 * @param {Object}  params      获取参数
	 * @param {String}  params.domId 主键名称或者是一个jquery的对象
	 * @param {Boolean} params.isDefaultTitle 是否添加默认项
	 * @param {String}  params.defaultTitle 默认提示 exp:'----请选择----'
	 * @param {Boolean} params.isDefaultVal 是否需要组件设置默认值
	 * @param {Boolean} params.isCheckBox 是否支持多选
	 * @param {Int}     params.maxHeight 最大高度
	 * @param {Int}     params.width 显示的宽度
	 * @param {Object}  event
	 * @param {Object}  event.onload 加载完成之后的回调
	 * @param {Object}  event.onchange 切换下拉框数据的改变方法回调 回传参数为<@param 数据对象,@param value,@param key,_that
	 *
	 */
	SelectControl.prototype = {
		init : function(){

			this.container = typeof(this.params.domId) == 'object' ? $(this.params.domId) : $('#' + this.params.domId);
			this.textNode = this.container.find('.select-text');

			this.valueNode =  this.container.find('.select-value');
			this.checkValue = [];
			this.allValue = [];
			this.minBox = 80;//最小宽度，带checkbox
			this.min = 50;//最小宽度
			this.maxHeight  = this.params.maxHeight ? this.params.maxHeight : 300;//最大高度

			this.isDefaultTitle = this.params.isDefaultTitle == false ? false :  true;

			this.defaultTitle = this.params.defaultTitle == undefined ?   '请选择' : this.params.defaultTitle;

			//是否需要设置默认值
			this.isDefaultVal = this.params.isDefaultVal == undefined ? true :  false;

			this.dataMaps = this.htmlDecodeValue(this.params.options);

			this.event = this.params.event || {};

			this.drawing();

			var state = this.params.state == undefined ? true : this.params.state;
			if(!state){
				this.setDisabled();
			}
			//初始化事件
			if(this.event.onload){
				this.event.onload();
			}
		},
		drawing : function(){
			var arr = [];
			var width = this.params.width || this.container.width();
			this.max = 500;//最大宽度

			if(this.params.isCheckBox){
				width = width < this.minBox ? this.minBox : width;
			}else{
				width = width < this.min ? this.min : width;
			}
			width = width > this.max ? this.max : width;

			var isOptionsWrap = false;
			var optionsWrap = this.container.find('.J_OptionsWarp');
			if(optionsWrap.hasClass('options-warp')){
				optionsWrap.html('');
				isOptionsWrap = true;
			}else{
				width = width.toString();
				if(width.indexOf("%") == -1){
					width = parseInt(width)+"px";
				}
				arr.push('<div class="options-warp J_OptionsWarp" style="width:'+ width +';">');
			}

			arr.push('<ul>');
			var isCheckBox = this.params.isCheckBox;
			if(isCheckBox){
				arr.push('<li><input type="checkbox" class="J_AllCheckBox check-box gridCheckBox"/>全选</li>');
			}else if(this.isDefaultTitle){
				arr.push('<li _value=""><span class="li-text">'+this.defaultTitle+'</span></li>');
			}
			var liTextWidth = width - (isCheckBox ? 25 : 6 );
			var itemLength = this._getListLength();
			if(itemLength == 0 && !isCheckBox){
				//arr.push('<li>无数据</li>');
			}else{
				if(itemLength * 20 > this.maxHeight){
					liTextWidth = liTextWidth - 19;
				}
				for(var key in this.dataMaps){
					var text = this.dataMaps[key];
					//prototype.js中对array进行了扩展，避免循环出array中的方法
					if( (text && typeof text == "function") || (typeof text == "string" && text.indexOf("function") > -1)){
						continue ;
					}
					arr.push('<li title="' + text + '" _value = "'+key+'">');
					if(isCheckBox){
						arr.push('<input type="checkbox" class="check-box gridCheckBox"/>');
					}
					arr.push('<span class="li-text" style="width:' + parseInt(liTextWidth - 10) + 'px;">' + text + '</span>');
					arr.push('</li>');
				}
			}
			arr.push('</ul>');
			if(!isOptionsWrap){
				arr.push('</div>');
				this.container.append(arr.join(''));
			}else{
				optionsWrap.append(arr.join(''));
			}
			var optionsWarp = this.container.find('.options-warp');
			if(optionsWarp.height() > this.maxHeight){
				optionsWarp.height(this.maxHeight);
			}

			this.addDefaultRules();

			//默认选中的值
			if(this.params.defaultVal || this.params.defaultVal == 0){
				this.setSelectList(this.params.defaultVal);
			}else {
				this.setDefaultValue();
			}


			optionsWarp = optionsWrap = null;
			delete arr;
			this.bindEvent();
		},
		/**
		 * 特殊数据做转义
		 */
		htmlDecodeValue: function( options ){
			var result = {};
			for(var key in options){
				var k = this.htmlDecode( key );
				var v = this.htmlDecode( options[key] );
				result[k] = v;
			}
			return result;
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
		/**
		 * 默认值设置
		 * @return
		 */
		setDefaultValue: function(){
			//是否需要设置默认值
			if(this.isDefaultVal){
				if(this.isDefaultTitle){//默认显示请选择
					this.setValue(this.defaultTitle, '');
				}else{//默认选择第一个
					this.setItemIndexSel(0);
				}
			}
		},
		setItemIndexSel: function(index){
			index = index || 0;
			var i=0;
			this.setValue('', '');
			if(this.dataMaps){
				for(var key in this.dataMaps){
					if( i == index ){
						var text = this.dataMaps[key];
						this.setValue(text, key);
					}
					i++;
				}
			}
		},
		bindEvent : function(){
			var _self = this;
			var liNode = this.container.find('li');
			var $_selfoptions = this.container.find(".options-warp");

			$(document).on("mousedown",function(){
				$(".options-warp").hide();
			});

			$_selfoptions.on("mousedown",function(){
				if(!$_selfoptions.is(":visible")){
					$_selfoptions.show();
				}
				return false;
			})


			this.container.on("click",function(){
				if($(this).hasClass("disabled")){
					return false;
				}
				_self.show();
			})

			liNode.on('mouseover',function(e){
				$(this).addClass('focus');
			});

			liNode.on('mouseout', function(){
				$(this).removeClass('focus');
			});

			liNode.on('click',function(e){
				e.stopPropagation();
				var _that = this;
				var text = $(this).html().replace(/<[^>]*>/g,'');
				var value = $(this).attr('_value');

				if(value && _self.params.isCheckBox){//多选框
					var node = $(this).find('.check-box');
					if(!node.hasClass('checked')){//选中
						_self.checkValue['t-' + text] = value;
						node.addClass('checked').attr("checked","checked");
					}else{
						_self.checkValue['t-' + text] = null;
						node.removeClass('checked').removeAttr("checked");
					}
					_self.updateInputValue();

					var obj = _self.dataMaps[value] || [];
					if(_self.event.onchange){
						//将obj修改为当前节点
						//_self.event.onchange(obj, value, text, _that);
						_self.event.onchange(_that, value, text, _that);
					}

				}else if(!_self.params.isCheckBox){//单选下拉
					if(value=='') text = _self.defaultTitle;//点击的请选择，则清空框
					var oldValue = _self.valueNode.val();
					_self.setValue(text,value);
					$('.J_OptionsWarp').hide();
					if("notNullOrEmptySelect".indexOf(_self.valueNode.attr("validator")) > -1){
						xy_validator.validator(_self.valueNode[0]);
					}
					if(_self.event.onchange && oldValue != value){
						var obj = _self.dataMaps[value] || [];
						//将obj修改为当前节点
						//_self.event.onchange(obj, value, text, _that);
						_self.event.onchange(_that, value, text, _that);
					}
					return false;
				}else{
					var checked = $(this).find('.J_AllCheckBox').hasClass('checked');
					if(checked){
						$(this).find('.J_AllCheckBox').removeClass('checked').removeAttr("checked");
					}else{
						$(this).find('.J_AllCheckBox').addClass('checked').attr("checked","checked");
					}
					_self.clickSelectAllCheckBox();
					if(_self.event.onchange){
						_self.event.onchange(_self._getTextVal(),_self.getValue());
					}
				}
				if(!_self.params.isCheckBox){
					_self.container.find('li').removeClass('opt-focus');
					$(this).addClass('opt-focus');
				}
				_self.valueNode.blur();

			});
			liNode = null;

			return false;
		},
		/**
		 * 设置禁用
		 * @return
		 */
		setDisabled: function(){
			this.container.addClass('disabled');
		},
		/**
		 * 设置启用
		 * @return
		 */
		setEnabled: function(){
			this.container.removeClass('disabled');
		},

		/**
		 * 隐藏下拉框
		 * @return
		 */
		closeSelectWrap: function(){
			$('.select-warp').css('z-index',1000);
			$('.J_OptionsWarp').hide();
		},
		/**
		 * 点击全选操作
		 * @return
		 */
		clickSelectAllCheckBox: function(){
			var checkAllNode = this.container.find('.J_AllCheckBox');
			var liNode = this.container.find('li');
			if(checkAllNode.hasClass('checked')){//多选的全部
				liNode.find('.check-box').addClass('checked').attr("checked","checked");
				this.checkValue = [];
				var _self = this;
				liNode.each(function(e){
					var value = $(this).attr('_value');
					if(value != undefined && value != ''){
						var text = $(this).html().replace(/<[^>]*>/g,'');
						_self.checkValue['t-' + text] = value;
					}
				});
				_self = null;
			}else{
				liNode.find('.check-box').removeClass('checked').removeAttr("checked");
				this.checkValue = [];
			}
			checkAllNode = liNode = null;
			this.updateInputValue();
		},
		//更新隐藏框的值
		updateInputValue : function(){
			var text = [];
			var value = [];
			for(var item in this.checkValue){
				if(item.indexOf('t-') > -1 && this.checkValue[item]){
					text.push(item.substring(2,item.length));
					value.push(this.checkValue[item]);
				}
			}
			this.setValue(text.join(','),value.join(','));
			delete text;
			delete value;
		},
		/**
		 * 清空设置的值
		 * @return
		 */
		clearSelectList: function(){
			this.checkValue = [];
			this.valueNode.val('');
			this.setSelectList();
		},
		/**
		 * 设置选中的值
		 * @param value
		 * @return
		 */
		setSelectList: function(_value, isCallChangeEvent){
			_value = (_value || this.valueNode.val() || this.params.defaultVal || '') + '';

			var text = [];
			var values = _value.split(',');

			for(var i=0;i<values.length;i++){
				if(values[i] != ''){
					for(var key in this.dataMaps){
						var txt = this.dataMaps[key];
                        if(this.isDataArray()){
                            if(txt == values[i]){
                                text.push(txt);
                                this.checkValue['t-' + txt] = txt;
                            }
                        }else{
                            if(key == values[i]){
                                text.push(txt);
                                this.checkValue['t-' + txt] = key;
                            }
                        }

					}
				}
			}
			//下拉框带checkbox选中操作
			if(this.params.isCheckBox){
				var checkboxs = this.container.find('.check-box');
				checkboxs.removeClass('checked').removeAttr("checked");

				var count = 0,_values = ','+_value+',';
				checkboxs.each(function(){
					var attrValue = $(this).parent().attr('_value');
					if(attrValue && _values.indexOf(','+attrValue+',') > -1){
						$(this).addClass('checked').attr("checked","checked");
					}
				})
			}

			if(text.join(',') == '' && _value == ''){
				this.setDefaultValue();
			}else{
				this.setValue(text.join(','), _value);
			}

			if(isCallChangeEvent && this.event.onchange){
				this.event.onchange(text, _value);
			}

			delete text;
		},
		/**
		 * 检查是否选中全选checkbox
		 * @return
		 */
		checkSelectedAll: function(){
			var count = 0;
			var checkAllNode = this.container.find('.J_AllCheckBox');
			if(this.params.isCheckBox){
				this.container.find('.check-box').each(function(k,i){
					if(!$(this).hasClass('J_AllCheckBox') && $(this).hasClass('checked')){
						count++;
					}
				});
				if(count && count == this.container.find('li').size() - 1){
					checkAllNode.addClass('checked').attr("checked","checked");
				}else{
					checkAllNode.removeClass('checked').removeAttr("checked");
				}
			}
			checkAllNode = null;
		},
		/**
		 * 设置方本值和隐藏值
		 * @param text
		 * @param value
		 * @return
		 */
		setValue : function(text,value){
			this.checkSelectedAll();
			text = text || '';
			value = value || '';

			this.textNode.attr('title', text);
			this._setTextVal(text);
            if(this.isDataArray()){
                this.valueNode.val(text);
            }else{
               this.valueNode.val(value);
            }
		},
        isDataArray: function(){
            return $.isArray(this.params.options)
        },
		_setTextVal: function(value){
			if(this.textNode[0].tagName == 'INPUT'){
				this.textNode.val(value);
			}else{
				this.textNode.html(value);
			}
		},
		_getTextVal: function(){
			if(this.textNode[0].tagName == 'INPUT'){
				return this.textNode.val();
			}else{
				return this.textNode.html();
			}
		},
		getValue : function(){
			return this.valueNode.val();
		},
		/**
		 * 显示下拉框
		 */
		show: function(){
			$('.J_OptionsWarp').hide();
			this.container.find('.J_OptionsWarp').show();
		},
		/**
		 * 重新设置下拉框数据
		 * @return
		 */
		setListData: function(data , isCallChangeEvent){
			isCallChangeEvent = isCallChangeEvent ? true: false;
			if(typeof data == 'object'){
				this.dataMaps = this.htmlDecodeValue(data);
				this.drawing();
			}else{
				alert('设置了无效的数据格式');
			}
			this.clearSelectList();
			if(isCallChangeEvent && this.event.onchange){
				this.event.onchange(null, this.getValue(), this._getTextVal());
			}
		},
		/**
		 * 设置下拉框下拉事件等是否可用
		 * @param flag
		 * @return
		 */
		setIsDisabled: function( flag ){
			this.isDisabled = !flag === false ;
		},
		/**
		 * [{'key1':'123'},{'key2': '456'}]
		 * @return
		 */
		_convertTopMap: function(){
			this.dataMaps = [];
			var options = this.params.options;
			this.allOptionsLen = 0;
			for(var i=0; i<options.length; i++){
				var obj = options[i];
				for(var key in obj){
					this.dataMaps[key] = obj[key];
					this.allOptionsLen++;
				}
			}
			delete options;
		},
		_getListLength: function(){
			return this.allOptionsLen;
		},

		_getListLenByObj: function(data){
			var len = 0;
			for(var i=0; i<data.length; i++){
				var obj = data[i];
				for(var key in obj){
					len++;
				}
			}
			data = null;
			return len;
		},
		getDataMap: function(){
			return this.dataMaps;
		},

		/**
		 * 获取下拉框数据
		 * @return
		 */
		getData: function(){
			return this.params.options;
		},
		/**
		 * 添加不走默认值的验证
		 * @return
		 */
		addDefaultRules: function(){
			this.valueNode.attr('removeDefaultRules', 'removeDefaultRules');
		},

		/**
		 * 通过显示值获取value
		 */
		getValueByText: function(text){
			for ( var x in this.dataMaps ){
				if ( this.dataMaps[x] == text ){
					return x;
				}
			}
			return "";
		}
	};

	return SelectControl;
})


