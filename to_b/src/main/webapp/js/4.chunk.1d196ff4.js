webpackJsonp([4,23],{

/***/ 29:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function($) {__webpack_require__(30);
	var host = __webpack_require__(4).requestHost();
	var homePageVm = avalon.define({
	    $id: 'homePage',
	    userName: '',
	    dynamicList: [],
	    getNoticeList: function(page) {
	        $.post(host + '/notice/list', {
	            pageSize: 10,
	            page: page || 1
	        }, function(data) {
	            if (data.code === -10 || data.code === -11) {
	                avalon.vmodels.root.showLoading = false;
	                avalon.router.go('login');
	            }
	            if (data.code === 0) {
	                var list = data.data.data;
	                for (var i = 0; i < list.length; i++) {
	                    if (list[i].mess_type === 1) {
	                        list[i].mess_title = "系统消息";
	                    } else {
	                        list[i].mess_title = "账号提醒";
	                    }
	                }
	                homePageVm.dynamicList = list;
	            } else {
	                avalon.vmodels.root.showMessage(data.msg);
	            }
	        })
	    }
	});
	module.exports = avalon.controller(function($ctrl) {
	    // 视图渲染后，意思是avalon.scan完成
	    $ctrl.$onRendered = function() {}
	        // 进入视图
	    $ctrl.$onEnter = function() {
	            homePageVm.userName = avalon.vmodels.root.userName;
	            homePageVm.getNoticeList();
	        }
	        // 对应的视图销毁前
	    $ctrl.$onBeforeUnload = function() {
	
	        }
	        // 指定一个avalon.scan视图的vmodels，vmodels = $ctrl.$vmodels.concact(DOM树上下文vmodels)
	    $ctrl.$vmodels = []
	})
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(2)))

/***/ }),

/***/ 30:
/***/ (function(module, exports) {

	// removed by extract-text-webpack-plugin

/***/ })

});
//# sourceMappingURL=4.chunk.1d196ff4.js.map