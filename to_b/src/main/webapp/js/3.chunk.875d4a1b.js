webpackJsonp([3,23],{

/***/ 28:
/***/ (function(module, exports) {

	module.exports = "<div class=\"homePage\" ms-important=\"homePage\">\r\n    <div class=\"welcome\">\r\n        <img class=\"user-pic\" src=\"../images/user.jpg\">\r\n        <p class=\"welcome-text\">\r\n            <span class=\"font-blod\">  {{userName}}，你好！</span><br/> 您可以点击上面分类查看数据分析。\r\n        </p>\r\n    </div>\r\n    <div class=\"dynamic\" ms-visible=\"dynamicList.length!==0\">\r\n        <p class=\"dynamic-title font-blod\">消息与更新</p>\r\n        <ul class=\"dynamic-list\">\r\n            <li ms-repeat=\"dynamicList\">\r\n                <img ms-if=\"el.mess_type==1\" src=\"../images/system.png\" />\r\n                <img ms-if=\"el.mess_type==2\" src=\"../images/tips.png\" />\r\n                <p class=\"dynamic-content\">\r\n                    <span>{{el.mess_title}}</span>\r\n                    <span class=\"small-font\">{{el.time}}</span><br/>\r\n                    <span>{{el.msg}}</span>\r\n                </p>\r\n            </li>\r\n        </ul>\r\n    </div>\r\n    <div aria-label=\"Page navigation\" ms-visible=\"dynamicList.length>10\">\r\n        <ul class=\"pagination\">\r\n            <li>\r\n                <a href=\"#\" aria-label=\"Previous\">\r\n                    <span aria-hidden=\"true\">&laquo;</span>\r\n                </a>\r\n            </li>\r\n            <li><a href=\"#\">1</a></li>\r\n            <li><a href=\"#\">2</a></li>\r\n            <li><a href=\"#\">3</a></li>\r\n            <li><a href=\"#\">4</a></li>\r\n            <li><a href=\"#\">5</a></li>\r\n            <li>\r\n                <a href=\"#\" aria-label=\"Next\">\r\n                    <span aria-hidden=\"true\">&raquo;</span>\r\n                </a>\r\n            </li>\r\n        </ul>\r\n    </div>\r\n</div>"

/***/ })

});
//# sourceMappingURL=3.chunk.875d4a1b.js.map