webpackJsonp([8,13],{38:function(t,e,n){var r,a;(function(i){r=[n(39)],a=function(t){var e={getPathParam:function(t){var e=new RegExp("(^|&)"+t+"=([^&]*)(&|$)","i"),n=window.location.search.substring(1).match(e);return null!=n?decodeURIComponent(n[2]):""},getUrlParms:function(){for(var t=new Object,e=location.search.substring(1),n=e.split("&"),r=0;r<n.length;r++){var a=n[r].indexOf("=");if(a!=-1){var i=n[r].substring(0,a),o=n[r].substring(a+1);t[i]=unescape(o)}}return t},createHeaderJSElement:function(t){var e=document.createElement("script");e.setAttribute("type","text/javascript"),e.setAttribute("src",t),document.getElementsByTagName("head")[0].appendChild(e)},createHeaderCSSElement:function(t){var e=document.createElement("link");e.setAttribute("type","text/css"),e.setAttribute("rel","stylesheet"),e.setAttribute("href",t),document.getElementsByTagName("head")[0].appendChild(e)},htmlEncode:function(t){var e=t+"";return""==e?e:e=e.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(new RegExp('"',"g"),"&quot;").replace(new RegExp("'","g"),"&#39;").replace(new RegExp("  ","g")," &nbsp;").replace(new RegExp("\r|\n","g"),"<br/>")},htmlTitleEncode:function(e){t.nullOrEmpty(e)&&(e="");var n=e+"";return""==n?n:n=n.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(new RegExp('"',"g"),"&quot;").replace(new RegExp("'","g"),"&#39;").replace(new RegExp("\r|\n","g"),"&#13")},htmlDecode:function(t){var e=t+"";return""==e?e:e=e.replace(/&lt;/g,"<").replace(/&gt;/g,">").replace(/&quot;/g,'"').replace(/&#39;/g,"'").replace(/ &nbsp;/g,"  ").replace(/&amp;/g,"&")},tipsDIV:function(t,n,r,a,r){jTip(e.htmlEncode(t),n,a,r)},alertDIV:function(t,n,r){jAlert(e.htmlEncode(t),n,r)},confirmDIV:function(t,e,n,r){jConfirm(t,e,n,r)},getMsg:function(t){var e=xy_msg[t];return null!=e&&""!=e||(e=t),e},getMessage:function(t){if(0==arguments.length)return"";t+="";for(var e=1;e<arguments.length;e++){var n="\\{"+e+"\\}",r=arguments[e]+"";t=t.replaceAll(n,r)}return t},restCallback:function(t,n,r){try{if(t.code==-10){var a=t.msg;return void jAlert(a,function(){location.href="/user/logout"})}if(0!=t.code){var a=t.msg;return r?(e.hideLock(),void r(t.code,t,a)):(e.alertDIV(a,function(){}),void e.hideLock())}switch(e.hideLoadingCard(),e.hideLock(),typeof n){case"string":alertDIV(n);break;case"function":n(t.data)}}catch(i){e.hideLock()}},showLoadingCard:function(t){var e=i("#loading_small");0!=e.length?e.show():(e='<div id="loading_small"><img src="../images/ajax_loader.gif" alt="" /></div>',i("#navigation").append(e))},hideLoadingCard:function(){i("#loading_small").hide()},callbackRb:function(t){i.browser.msie?(t.appendTo(i("#rubbish")),i("#rubbish").html("")):t.remove()},stopPop:function(t){try{t.stopPropagation()}catch(t){t.cancelBubble}},isIe:function(){return!!i.browser.msie},onloadImg:function(t,e,n){var r=new Image;if(r.onload=function(){e&&"function"==typeof e&&e(t)},!r.complete)var a=setInterval(function(){if(r.complete)return e&&"function"==typeof e&&e(t),clearInterval(a),!1},500);r.onerror=function(){n&&"function"==typeof n&&n()},r.src=t},hiddenOnMouseDown:function(t){document.onmousedown=function(){var e=i("#"+t);1==e.is(":visible")&&e.hide()},i("#"+t).mousedown(function(t){t.stopPropagation()})},getUrlAddress:function(){var t=window.location.href,e=t.split("/");return e[0]+"//"+e[1]+e[2]+"/"+e[3]},lockScreen:function(){var t=document.documentElement.clientWidth,e=i(document).height();document.documentElement.style.overflow="hidden";var n=i("#loading_layer");0!=n.length?(i(".loading_layer_body").empty(),i(".loading_layer_body").html("<img src='../images/ajax_loader.gif' alt='' />"),n.css({width:t+"px",height:e+"px"}),n.show()):n='<div id="loading_layer" style="width:'+t+"px;height:"+e+'px;"><div class="loading_layer_body"><img src="../images/ajax_loader.gif" alt="" /></div></div>',i("body").append(i(n))},hideLock:function(){i("#loading_layer").hide(),document.documentElement.style.overflow=""},showAjaxOverlay:function(t,e,n){var r=t.outerWidth(!0),a=t.outerHeight(!0),o=i(window).scrollTop(),s=i("#ajax_overlay");0!=s.length?(s.css({width:r+"px",height:a+"px"}),s.find(".ajax_overlay_img").css("margin-top",(a+o-50)/2+"px"),s.show()):(t.addClass("pr"),s='<div style="width:'+r+"px;height:"+a+'px" id="ajax_overlay"><div class="ajax_overlay_img" style="text-align:center;margin-top:'+(a+o-50)/2+'px"><img src="/images/loading.gif" alt="" /><p class="loadingText">'+(void 0==e?"":e)+"</p></div></div>"),t.append(i(s))},hideAjaxOverlay:function(){i("#ajax_overlay").hide()},initImgUpload:function(n,r){var a="/.tmp/",o=5,s=i("#"+n.fileBtnId),l=".jpg,.bmp,.png";n.accessoryId&&s.on("mousedown",function(){if(i("#"+n.accessoryId).children(".img-list:visible").length>=o)return alertDIV(STATICMSG[1018]),!1}),i("#"+n.formId).attr({enctype:"multipart/form-data",action:"/upload/photo/",method:"post"}),i("#"+n.formId).submit(function(){return i(this).ajaxSubmit({error:function(t){status("Error: "+t.status)},success:function(t){t=JSON.parse(t),i("#"+n.photoId).attr("src",a+t.filename),i("#"+n.dataId).val(t.filename),e.hideLock()}}),!1}),s.change(function(){var t=[],r=s.val();t=r.split("\\"),r=t[t.length-1];var a=r.substring(r.lastIndexOf(".")+1).toLowerCase();return l.indexOf(a)==-1?(alertDIV(e.getMessage(STATICMSG[1019],l)),!1):(e.lockScreen(),void i("#"+n.formId).trigger("submit"))}),i("#"+n.delBtnId).on("click",function(){i(this).hide(),i("#"+n.dataId).val(""),t.nullOrEmpty(n.photoId)||(i("#"+n.photoId).parent().hide(),i("#"+n.photoId).attr("src",""))}),i("#"+n.photoId).on("click",function(){window.open(i(this).attr("src"))})},getAll:function(){var t,e={};t=window.XMLHttpRequest?new XMLHttpRequest:new ActiveXObject("Microsoft.XMLHTTP"),t.open("GET","/static/script/positionTmp.txt",!1),t.send();var n={firstLevel:[],secondLevel:{}},e=n.secondLevel;return t.responseText.replace(/.+/g,function(t){var r=t.toString().split("\t");i.inArray(r[0],n.firstLevel)==-1&&n.firstLevel.push(r[0]),e[r[0]]||(e[r[0]]={}),r[2]?(e[r[0]][r[1]]||(e[r[0]][r[1]]=[]),e[r[0]][r[1]].push(r[2])):(e[r[0]][r[0]]||(e[r[0]][r[0]]=[]),e[r[0]][r[0]].push(r[1]))}),n},createMultilevelSelectHTML:function(t){var e=[],n=t.data||this.getAll();e.push('<div class="options-picker" style="display: none;">'),e.push('<div><div class="first-level-content">'),e.push('<div  _value="" class="pick-all-first"><input type="checkbox" class="check-all-first"/><span class="li-text ml5"><b>全部</b></span></div>');var r=n.firstLevel,a=0;return i.each(r,function(t){a++,e.push('<div  _value="'+r[t]+'" class="picker-first-level"><input id="checkbox_'+a+'" type="checkbox" class="checkbox-first-item" value="'+r[t]+'"/><span class="li-text ml5">'+r[t]+"</span></div>")}),e.push("</div>"),e.push('<div classs="next-level-content">'),a=0,i.each(r,function(t){a++,e.push('<div class="picker-next-level" style="display: none;" _title="'+r[t]+'">');var o=n.secondLevel[r[t]];if(!o)return e.push("</div>"),void e.push("</div>");i.isEmptyObject(o)||e.push('<div  class="pick-all-second ver-middle" ><input type="checkbox" class="check-all-second"/><span class="picker-second-title ml5">全部</span></div>');var s=0;i.each(o,function(n,o){return e.push('<div  class="mt10 mb10 ver-middle pick-second-item" style="text-align: left;">'),r[t]!=n&&(s++,e.push('<input id="checkbox_'+a+"_"+s+'" type="checkbox"  class="checkbox-second-item" value="'+n+'"/><span class="picker-second-title ml5">'+n+"</span>")),e.push('<div class="picker-three-level clearfix " style="display: block;">'),o&&0!=o.length?(i.each(o,function(i){o[i+1]?e.push('<div  class="mt10 ver-middle" f-data="'+r[t]+'" s-data="'+n+'"><input id="checkbox_'+a+"_"+s+"_"+(i+1)+'" type="checkbox"  class="checkbox-third-item" value="'+o[i]+'"/><span class="li-text ml5"  >'+o[i]+"</span>&nbsp;&nbsp;|&nbsp;&nbsp;</div>"):e.push('<div  class="mt10 ver-middle" f-data="'+r[t]+'" s-data="'+n+'"><input id="checkbox_'+a+"_"+s+"_"+(i+1)+'" type="checkbox"  class="checkbox-third-item" value="'+o[i]+'"/><span class="li-text ml5" >'+o[i]+"</span></div>")}),e.push("</div>"),void e.push("</div>")):(e.push("</div>"),void e.push("</div>"))}),e.push("</div>")}),e.push("</div>"),e.join("")},resResult:function(t,n,r){try{var a=i(document).height();if(i("#loading_layer").css("height",a+"px"),i("#loading_layer").show(),t.code==-10||t.code==-11){i(".loading_layer_body").empty();var n=t.msg;return i(".loading_layer_body").text(n),void(window.location.href="")}if(0!=t.code){i(".loading_layer_body").empty();var n=t.msg;return i(".loading_layer_body").text(n),void setTimeout(function(){e.hideLock(),r()},2e3)}n?(i(".loading_layer_body").empty(),i(".loading_layer_body").text(n),setTimeout(function(){e.hideLock(),r&&r()},2e3)):e.hideLock()}catch(o){e.hideLock()}},tips:function(t){try{var n=i(document).height();i("#loading_layer").css("height",n+"px"),i("#loading_layer").show(),t?(i(".loading_layer_body").empty(),i(".loading_layer_body").text(t),setTimeout(function(){e.hideLock()},2e3)):e.hideLock()}catch(r){e.hideLock()}}};return e}.apply(e,r),!(void 0!==a&&(t.exports=a))}).call(e,n(33))},39:function(t,e,n){var r,a;(function(i){r=[n(40),n(41)],a=function(t,e){var n={initAutoValidator:function(){var t=this;i(document).on("blur.validator","[validator]",function(e){t.validator(e.target,!0)})},getValidatorRules:function(t){var e=[];return null!=i(t).attr("validator")&&(e=i.trim(i(t).attr("validator")).split(" ")),i(t).closest("[removeDefaultRules]").length||(e.reverse(),e.push("char"),e.reverse()),e},getValidatorValue:function(t){var e="",n=t.tagName;switch(n.toUpperCase()){case"INPUT":case"TEXTAREA":e=i.trim(i(t).val());break;case"CITE":if("radio"==i(t).attr("type")){var r=i(t).attr("name"),a=i("cite[name='"+r+"'].checked");a.length>0&&this.notNullOrEmpty(a.eq(0).attr("value"))&&(e=a.eq(0).attr("value"))}else if("checkbox"==i(t).attr("type")){var r=i(t).attr("name"),o=[];i("cite[name='"+r+"'].checked").each(function(){o.push(i(this).attr("value"))}),e=o.join(",")}else e=i.trim(i(t).text());break;default:e=i.trim(i(t).text())}return e.toString()},getErrorTips:function(t,e){return i(t).attr("errorTipsId")&&i("#"+i(t).attr("errorTipsId")).length>0?i("#"+i(t).attr("errorTipsId")).removeClass("u-error").addClass("u-error"):i(t).siblings("p.errorTips").length>0?(i(t).siblings("p.errorTips").removeClass("u-error").addClass("u-error"),i(t).siblings("p.errorTips").eq(0)):(i(t).parent().append("<p class='u-error errorTips closeToHide'></p>"),i(t).siblings("p.errorTips").eq(0))},getErrorTipsKey:function(t){return i(t).attr("errorTipsKey")||""},validator:function(t,r){var a=this,o=t.nodeType;if(1!=o)return!0;var s=this.getValidatorRules(t),l=this.getValidatorValue(t),c=this.getErrorTips(t,r),u=this.getErrorTipsKey(t);if(i.inArray("same",s)>-1&&!this.sameValidator(t))return!1;if(""==l&&i.inArray("notNullOrEmpty",s)==-1&&i.inArray("notNullOrEmptySelect",s)==-1)return c.removeAttr("title"),c.hide(),!0;try{for(var d=0,p=s.length;d<p;d++)if("same"!=s[d]){var h=this[s[d]](l,t);if(!h){if("notNullOrEmpty"==s[d]&&1==r)return c.hide(),!1;"notNullOrEmptySelect"==s[d]&&i(t).parent(".select-warp").addClass("error-input"),i(t).parent().find("input").addClass("error-input");var g=n.getMessage(e["VALIDATOR_"+s[d].toUpperCase()],u);return c.text(a.htmlTitleEncode(g)),c.show(),!1}"notNullOrEmptySelect"==s[d]&&i(t).parent(".select-warp").removeClass("error-input")}for(var f=[["min","max"]],d=0,p=f.length;d<p;d++)if(!this.attrValidator(t,f[d]))return!1;return c.removeAttr("title"),c.hide(),c.parent().find("input").removeClass("error-input"),!0}catch(v){var g=n.getMessage(e.VALIDATOR_UNKNOWNERROR);return c.text(a.htmlTitleEncode(g)),c.show(),!1}},autoValidator:function(t){var e=!0,n=this;return t.find("[validator]").each(function(){var t=n.validator(this);t||(e=t)}),e},sameValidator:function(t){var r=this,a=this.getValidatorValue(t),o=this.getErrorTips(t),s=this.getErrorTipsKey(t);if(this.nullOrEmpty(i(t).attr("equalTo")))return!0;var l=i(i(t).attr("equalTo"))[0],c=this.getValidatorValue(l);if(c!=a){var u=n.getMessage(e.VALIDATOR_SAME,s);return o.text(r.htmlTitleEncode(u)),o.show(),!1}return!0},attrValidator:function(t,r){var a=this,o=this.getValidatorValue(t),s=this.getErrorTips(t),l=this.getErrorTipsKey(t);if(r instanceof Array){for(var c=0,u=r.length;c<u;c++)if(!this.attrValidator(t,r[c])){var d=this.getErrorMsg(t,r);return s.text(a.htmlTitleEncode(d)),s.show(),!1}return!0}if(r+="",this.nullOrEmpty(i(t).attr(r)))return!0;if(this[r](o,i(t).attr(r)))return!0;var p="VALIDATOR_"+r.toUpperCase(),d=n.getMessage(e[p],l);return s.text(a.htmlTitleEncode(d)),s.show(),!1},getMessage:function(t){if(0==arguments.length)return"";t+="";for(var e=1;e<arguments.length;e++){var n="\\{"+e+"\\}",r=arguments[e]+"";t=t.replaceAll(n,r)}return t},getErrorMsg:function(t,r){for(var a=this.getErrorTipsKey(t),o=[],s="",l="",c=0,u=r.length;c<u;c++)s="VALIDATOR_"+r[c].toUpperCase(),this.nullOrEmpty(i(t).attr(r[c]))||(l=o.length?e[s].replaceAll("\\{1\\}",""):e[s],o.push(n.getMessage(l,a,i(t).attr(r[c]))));return o.join(",")},htmlTitleEncode:function(t){this.nullOrEmpty(t)&&(t="");var e=t+"";return""==e?e:e=e.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(new RegExp('"',"g"),"&quot;").replace(new RegExp("'","g"),"&#39;").replace(new RegExp("\r|\n","g"),"&#13")},"char":function(t){return/^[\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;:#]*$|^([\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;:#])+([\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;:# ])+([\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;:#])+$/.test(t)},isNull:function(t){return null==t},isEmpty:function(t){return null!=t&&0==t.length},notNullOrEmpty:function(t){return!n.nullOrEmpty(t)},notNullOrEmptySelect:function(t){return!n.nullOrEmpty(t)},nullOrEmpty:function(t){return null==t||0==t.length},number:function(t){return t==parseInt(t,10)&&(t+"").indexOf("-")==-1&&(t+"").indexOf(".")==-1},numberPoint:function(t){return/^(?!(0[0-9]{0,}$))[0-9]{1,}[.]{0,}[0-9]{0,}$/.test(t)},email:function(t){return/^([a-z0-9A-Z]+[-|\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\.)+[a-zA-Z]{2,}$/.test(t)},moreEmail:function(t){return/^[a-zA-Z0-9]+([._\\-]*[a-zA-Z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.{1}){1,63}[a-z0-9]+$/.test(t)},phoneEmail:function(t){var e=/^((0?1\d{10})|((0(\d{2,3}))[\-]?\d{7,8}))$|^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})$/.test(t),n=/^([a-z0-9A-Z]+[-|\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\.)+[a-zA-Z]{2,}$/.test(t);if(e||n)return!0},companyEmail:function(t){return!/^$|^(([a-zA-Z0-9])+([a-zA-Z0-9-_.]{1,})@(qq|163|126|sina|gmail|outlook|yeah|aliyun|139|189).[a-zA-Z0-9\.\_]{2,})$/.test(t)},pwdlength:function(t){return/^$|^(([a-zA-Z0-9]){8,16})$/.test(t)},pwdFormat:function(t){return/^(?!\d+$)(?![A-Za-z]+$)[a-zA-Z0-9]{8,16}$/.test(t)},notes:function(t){return/^[\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;#@]*$|^([\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;#@])+([\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;#@ ])+([\u4E00-\u9FA5a-zA-Z0-9\/＋\[\]\w\-\(\).（）;#@])+$/.test(t)},phone:function(t){return/^([0-9\-]{5,})?$/.test(t)},telPhone:function(t){return/^((0?1\d{10})|((0(\d{2,3}))[\-]?\d{7,8}))$|^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})$/.test(t)},memo:function(t){return t.length>250},isDefined:function(t){return null!=t&&"undefined"!=typeof t},isURL:function(t){return/^((https?|ftp|news):\/\/)?([a-z]([a-z0-9\-]*[\.。])+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\/[a-z0-9_\-\.~]+)*(\/([a-z0-9_\-\.]*)(\?[a-z0-9+_\-\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$/.test(t)},identityCode:function(t){if(!/^([\d]{15}|[\d]{17}[Xx\d])$/.test(t))return!1;var e;e=15==t.length?"19"+t.substr(6,2)+"-"+t.substr(8,2)+"-"+t.substr(10,2):t.substr(6,4)+"-"+t.substr(10,2)+"-"+t.substr(12,2);var n=new Date;return n.setCNDate(e),e==n.format()},brithday:function(t){if(!/^([\d]{4}-[\d]{2}-[\d]{2})$/.test(t))return!1;var e=new Date;return e.setCNDate(t),t==e.format()},bandWidth:function(t){return/^\d+(.\d+)?$/.test(t)},min:function(t,e){var n=parseInt(t,10),e=parseInt(e,10);return!isNaN(n)&&!isNaN(e)&&e<=n},max:function(t,e){var n=parseInt(t,10),e=parseInt(e,10);return!isNaN(n)&&!isNaN(e)&&e>=n},decimalPoint:function(t){var e=/^[0-9]+([.]{1}[0-9]{1,2})?$/;return!!e.test(t)}};return n}.apply(e,r),!(void 0!==a&&(t.exports=a))}).call(e,n(33))},40:function(t,e,n){(function(t){String.prototype.replaceAll=function(t,e){try{return this.replace(new RegExp(t,"gm"),e)}catch(n){return this}},String.prototype.toSqlString=function(){try{return this.replaceAll("/","//")}catch(t){return this}},String.prototype.startWith=function(t){var e=new RegExp("^"+t);return e.test(this)},Date.prototype.format=function(t){var e={"M+":this.getMonth()+1,"d+":this.getDate(),"H+":this.getHours(),"m+":this.getMinutes(),"s+":this.getSeconds(),"q+":Math.floor((this.getMonth()+3)/3),"S+":this.getMilliseconds()};t||(t="yyyy-MM-dd"),/(y+)/.test(t)&&(t=t.replace(RegExp.$1,(this.getFullYear()+"").substr(4-RegExp.$1.length))),/(S+)/.test(t)&&(t=t.replace(RegExp.$1,(this.getMilliseconds()+"").substr(3-RegExp.$1.length)));for(var n in e)new RegExp("("+n+")").test(t)&&(t=t.replace(RegExp.$1,1==RegExp.$1.length?e[n]:("00"+e[n]).substr((""+e[n]).length)));return t},Date.prototype.getCNDay=function(){var t={0:"星期日",1:"星期一",2:"星期二",3:"星期三",4:"星期四",5:"星期五",6:"星期六"};return t[this.getDay()]},Date.prototype.setCNDate=function(e){if(e){var n=t.trim(e).split(" "),r=n[0].split("-"),a="19"+r[0];a=parseInt(a.substring(a.length-4,a.length),10),isNaN(a)&&(a=this.getFullYear());var i=this.getMonth();if(r.length>1){var o=parseInt(r[1],10);isNaN(o)||(i=o-1)}var s=this.getDate();if(r.length>2){var l=parseInt(r[2],10);isNaN(l)||(s=l)}if(this.setFullYear(a,i,s),!(n.length<2)){var c=n[1].split(":"),u=this.getHours(),d=parseInt(c[0],10);isNaN(d)||(u=d);var p=this.getMinutes();if(c.length>1){var h=parseInt(c[1],10);isNaN(h)||(p=h)}var g=this.getSeconds();if(c.length>2){var f=parseInt(c[2],10);isNaN(f)||(g=f)}var v=this.getMilliseconds();if(c.length>3){var m=parseInt(c[3],10);isNaN(m)||(v=m)}this.setHours(u,p,g,v)}}},Array.prototype.joinAll=function(t){for(var e=0,n=this.length;e<n;e++)this[e]instanceof Array&&(this[e]=this[e].joinAll(t));return this.join(t)},Array.prototype.last=function(){return this[this.length>0?this.length-1:0]}}).call(e,n(33))},41:function(t,e,n){var r;r=function(){return{VALIDATOR_UNKNOWNERROR:"未知的校验错误",VALIDATOR_CHAR:"不满足基础校验规则",VALIDATOR_NOTNULLOREMPTY:"请输入{1}",VALIDATOR_NOTNULLOREMPTYSELECT:"请选择{1}",VALIDATOR_PHONE:"{1}不符合规则",VALIDATOR_TELPHONE:"{1}不符合规则",VALIDATOR_LANDLINE:"请输入{1}",VALIDATOR_NUMBER:"{1}不是有效数值",VALIDATOR_SAME:"两次输入的{1}不一致",VALIDATOR_EMAIL:"请输入正确的邮箱地址，如example@jipin.com",VALIDATOR_MOREEMAIL:"{1}不符合规则",VALIDATOR_COMPANYEMAIL:"请输入以企业域名为后缀的邮箱地址",VALIDATOR_IDENTITYCODE:"{1}不符合身份证号码规则",VALIDATOR_DECIMALPOINT:"{1}必须为数字且最多只能保留两位小数",VALIDATOR_PWDLENGTH:"密码位数为8-16位",VALIDATOR_PWDFORMAT:"输入8-16位包含字母、数字的密码",VALIDATOR_ISURL:"不是正确的网址格式",VALIDATOR_NUMBERPOINT:"{1}不符合规则",VALIDATOR_PHONEEMAIL:"请输入正确手机号码或邮箱",VALIDATOR_MIN:"{1}必须大于或者等于{2}",VALIDATOR_MAX:"{1}必须小于或者等于{2}",BEYOND_MAX_LENGTH:"{1}过长。",BEYOND_MIN_LENGTH:"{1}过短。",COMMON_LOADING_TEXT:"loading..."}}.call(e,n,e,t),!(void 0!==r&&(t.exports=r))},47:function(t,e,n){var r,a;(function(i){r=[n(38)],a=function(t){function e(t){var e="";i.each(t,function(t,n){e+="<li onclick=\"Chk2('"+n+'\')"><label class="mr30"><input type="radio" id="checkbox_a3" class="chk_1" value="chk_1" name="chk_1" /> <label for="checkbox_a3"></label>'+n+"</label></li>"}),i("#drag").width("150px"),i("#FuntypeList").html("<ul>"+e+"</ul>"),i("#FuntypeAlpha li").hover(function(){i(this).addClass("over")},function(){i(this).removeClass("over")})}var n=avalon.define({$id:"configure",_id:"",type:"",_type:null,short_id:"",username:"",identity:"",toggle:!1,data:[],tag0s:[],tags1:[],tags2:[],tags3:[],configstype:"人才分布",report_type:201,ltnum:0,gtnum:200,condition_l:"=",condition_r:"p25",getPassFromCookie:function(){return window.$.cookie(location.host+"_userinfo")},clearPassToCookie:function(){window.$.cookie(location.host+"_userinfo","",{path:"/"})},toggle_hiddle:function(){n.toggle=!n.toggle},logout:function(){n.clearPassToCookie(),window.location.href=""},doClick:function(t){i("#sublist").empty(),i(".tags").attr("isMe","");var n=i(this)[0].parentNode.offsetTop,r=i(this)[0].parentNode.offsetWidth,a=i(this)[0].parentNode.nextElementSibling.lastElementChild;i(a).attr("isMe","yes");var o='<div id="FuntypeAlpha">';o+='<div id="FuntypeList"></div>',o+="</div>",i("#drag_con").html(o),e(t.$model.tags),i("#maskLayer").css({top:n+44,left:r+129}).show(),i("#sublist").css({top:n+44,left:r-92})},generate:function(){if(i("#msgTips").is(":hidden"))return!1;if(n.ltnum-0>n.gtnum-0)return i("#msgTips").hide(),i("#errorMsg").hide(),i("#errorMsg2").show(),!1;var t=n.ltnum+"-"+n.gtnum,e="";e='<div class="tag"><span>'+t+"</span><i> × </i></div>";var r=i(this)[0].parentNode.nextElementSibling.lastElementChild;i(r).find(".tag").remove(),i(r).append(e),n.domLisenter()},condition:function(){var t=n.condition_l+""+n.condition_r,e="";e='<div class="tag"><span>'+t+"</span><i> × </i></div>","<"==n.condition_l&&(e='<div class="tag"><span>&lt'+n.condition_r+"</span><i> × </i></div>");var r=i(this)[0].parentNode.nextElementSibling.lastElementChild,a=i(r).find(".tag span"),o=!0;i.each(a,function(e,n){t==i(n).text()&&(o=!1)}),o&&i(r).append(e),n.domLisenter()},go:function(){var e=n.getdatas();if("nodata"!=e){if(e.report_type=n.report_type,204==n.report_type)var r="/report/config/allmodify";else var r="/report/config/modify";i.post(r,e,function(e){t.resResult(e,"配置成功",function(){n.getconfig(n.configstype)})})}},clearAll:function(){i(".tags").find(".tag").remove()},getdatas:function(){var e={},r=["industry","demand","experience","supply","label","type_limit"];return i(".tags").each(function(a,o){var s=[],l=a;return 204!=n.report_type?(i(o).find("span").each(function(t,e){s.push(i(this).text())}),e.check=s.join(","),e.config_type="industry",!1):(i(o).find("span").each(function(t,e){s.push(i(this).text())}),4==l&&0==s.length?(t.tips("请配置岗位供需指数"),void(e="nodata")):void(e[r[a]]=s.join(",")))}),e},domLisenter:function(){i(".tag").find("i").on("click",function(){i(this).parent().remove()})},getJson:function(){num=204==n.report_type?201:202;var t="../data/config"+num+".json";i.get(t,function(t){n.data_redy[0].name=t.data[0].name,n.data_redy[0].tags=t.data[0].tags,201==num&&(n.data_redy[1].name=t.data[1].name,n.data_redy[1].tags=t.data[1].tags,n.data_redy[2].name=t.data[2].name,n.data_redy[2].tags=t.data[2].tags,n.data_redy[3].name=t.data[3].name,n.data_redy[3].tags=t.data[3].tags),n.data=[],n.data=n.data_redy,n.domLisenter()})},getconfig:function(e){var r=null;switch(e){case"人才分布":r=201;break;case"人才流动":r=202;break;case"人才供需":r=203;break;case"热门岗位人群的薪酬及特征画像":r=204}var a="/report/config/all",o={report_type:r};t.lockScreen(),i.post(a,o,function(e){t.hideLock(),t.resResult(e),n.data_redy=[],n.data_redy.push({hastag:e.data[0].checks}),204==o.report_type&&n.data_redy.push({hastag:e.data[1].checks},{hastag:e.data[2].checks},{hastag:e.data[3].checks}),n.getJson()})},validator:function(t,e){i("#errorMsg2").hide();var n=/\b[0-9]\d{0,1}\b|\b[1-1]\d\d\b|\b200\b/;!n.test(t)||t-0<0||t-0>200?(i("#msgTips").hide(),i("#errorMsg").show()):(i("#msgTips").show(),i("#errorMsg").hide())}});return n.$watch("configstype",function(){switch(n.configstype){case"人才分布":n.report_type=201;break;case"人才流动":n.report_type=202;break;case"人才供需":n.report_type=203;break;case"热门岗位人群的薪酬及特征画像":n.report_type=204}n.getconfig(n.configstype)}),n.$watch("ltnum",function(){n.validator(n.ltnum)}),n.$watch("gtnum",function(){n.validator(n.gtnum)}),avalon.scan(document.body),avalon.controller(function(t){t.$onRendered=function(){document.title="数联寻英",i("#side_accordion div").removeClass("md-accent-bg").eq(2).addClass("md-accent-bg")},t.$onEnter=function(t,e,r){n.getconfig(n.configstype);var a=n.getPassFromCookie(),i=a.split("|");n._id=i[0],n.type=i[1],n.identity="1"==n.type?"管理员":"2"==n.type?"公司":"业务员",n.short_id=i[2],n.username=i[3]},t.$onBeforeUnload=function(){i(".oni-dialog").empty()},t.$vmodels=[n]})}.apply(e,r),!(void 0!==a&&(t.exports=a))}).call(e,n(33))}});