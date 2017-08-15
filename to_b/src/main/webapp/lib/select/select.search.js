/**
* 下拉框带搜索控件
*  继承select.js
	var p = {
			domId:'J_UserList', //必填
			options: {1: "自有产权", 2: "租赁", 3: "混合产权"},//必填
			isCheckBox: true,
			defaultTitle: '--请选择产权--',
			defaultVal: '1',
			event: {
				onchange: function(obj, value, text){
					console.log(text+ '  ' + value);
				},
				onload: function(){
					//console.log('loaded..');
				}
			},
			search:{
				type: 'location'
			}
		}
		new SelectSearchList(p1);
*/

var SelectSearchList = function(config){
	this.params = config;
	this.init();
}
$.extend(SelectSearchList.prototype, SelectControl.prototype);
$.extend(SelectSearchList.prototype, {
	init : function(){
		this.container = $('#' + this.params.domId);
		this.search = this.params.search;

		this.selectInput = this.textNode = this.container.find('.select-text');
		
		this.valueNode =  this.container.find('.select-value');
		this.checkValue = [];
		this.allValue = [];
		this.minBox = 80;//最小宽度，带checkbox
		this.min = 50;//最小宽度
		this.maxHeight  = this.params.maxHeight ? this.params.maxHeight : 300;//最大高度
		
		this.isDefaultTitle = this.params.isDefaultTitle == false ? false :  true;
		this.defaultTitle = this.params.defaultTitle || '----请选择----';
		
		//是否需要设置默认值
		this.isDefaultVal = this.params.isDefaultVal == undefined ? true : false;
		
		this.event = this.params.event || [];
		this.search.type = this.search.type  || 'location';
		
		this.allOptoins = this.params.options;
		var data = this.allOptoins.length > 0 ? this.allOptoins[0] : [];
		this._countMapLength();
		this.allLength = this._getListLength();
		
		if(this.search.type == 'location'){
			this.drawing();
			////选中的值
			this.selectedList = this.valueNode.val();
			this.setSelectList();
			//this.addBodyEvent();
		}
		this.inputEvent();
	},

	inputEvent: function(){
		var _self = this;
		this.printValue = '';
		this.selectInput.on('keyup', function(){
			_self.printValue = $(this).val();
			_self.valueNode.val(_self.printValue);
			if(_self.printValue == ''){
				_self.isAddTitle = false;
				_self.params.defaultVal = '';
				_self.isDefaultVal = false ;
				_self.setListData(_self.allOptoins);


				//_self.setValue(_self.params.defaultVal, '');
				//如果有下拉框，应该将下拉框数据选中
			}else{
				if(_self.search.type == 'location'){
					_self.searchLocationData();
				}else{
					_self.aync(_self.printValue);
				}

			}
		});
		this.selectInput.on('blur', function(){
			//判断是否有相匹配的内容
			if($(this).val()!='' && _self.valueNode.val() == ''){//说明搜索之后没有去点击内容
				//先判断输入的值有没有全匹配
				var result = _self.pipeiData($(this).val());
				
				if(result){
					_self.setValue(result[1], result[0]);
				}else{
					if( _self.allOptionsLen > 0){
						var obj = _self.params.options;//默认为第一个数据
						var i=0;
						for(key in obj){
							if( obj[key] && typeof obj[key] == "function"){
								continue;
							}
							if(i==0){
								_self.setValue(obj[key], key);
							}
							i++;
						}
					}else{
						_self.setValue('', '');
					}
				}
			}
		});
		this.container.on('click', function(){
			if(_self.allLength != _self._getListLenByObj(_self.params.options)){
				_self.setListData(_self.allOptoins);
			}
		});
	},
	/**
	 * 搜索本地数据
	 * @return
	 */
	searchLocationData: function(){
		var result = {};
		var options = this.allOptoins;
		var searchkey = this.selectInput.val();
		for(var key in options){
			if( options[key] && typeof options[key] == "function"){
				continue;
			}
			var text = options[key];
			if(text.indexOf(searchkey) > -1){
				result[key] = text;
			}
		}
		this.setListData(result);
		//还原输入的值，清空value
		this.setValue(this.printValue, '');
		delete result;
		delete options;
	},
	/**
	 * 搜索本地数据
	 * @return
	 */
	pipeiData: function(_text){
		_text = _text || '';
		var result = null;
		var options = this.allOptoins;
		for(key in options){
			if( options[key] && typeof options[key] == "function"){
				continue;
			}
			var text = options[key];
			if(text == _text){
				result = [key,text];
				break;
			}
		}
		delete options;
		return result;
	},
	_countMapLength: function(){
		this.dataMaps = [];
		this.dataMaps = this.options || this.params.options;
		this.allOptionsLen = 0;
		for(var key in this.dataMaps){
			if( this.dataMaps[key] && typeof this.dataMaps[key] == "function"){
				continue;
			}
			this.allOptionsLen++;
		}
		delete options;
	},
	/**
	 * 异步请求
	 * @return
	 */
	aync: function(keyword){
		var _self = this;
		$.ajax({
			url: this.search.url,
			data:{keyword:keyword},
			success:function(result){
				_self.ajaxSuccess(result);
			}
		});
	},
	
	ajaxSuccess: function(result){
		var _self = this;
		var data = {};
		xy_util.restCallback(result, function(dataArr){
			$.each(dataArr, function(i){
				data[dataArr[i].enterprise_id] = dataArr[i].full_name;
			})
			data = typeof data == 'string' ? JSON.parse(data) : data;
		})
		this.options = data;
		this._countMapLength();
		this.setListData(data);

	},
	/**
      * 重置搜索总对象数据 add by zhangss 2014-10-13
      */
	setSearchAllData: function(data){
		this.allOptoins = data || [];
		this.setValue();
		this.setListData(data);
	}

});
