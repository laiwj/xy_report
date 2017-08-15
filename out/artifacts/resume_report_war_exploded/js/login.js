/**
 * @fileOverview 登陆及注册功能
 *
 * @author chen_qianqian(72)
 * @version 1.0.0
 */

/**
 * 登陆
 */
	define([
		"validator",
		"msg",
		"jquery-cookie",
		"beanUtil",
		"validator"
	],function(xy_validator, msg,jquerycookie,xy_beanUtil,xy_validator){
		var xy_login = {

			/**\
			 * document.ready
			 */
			ready: function()
			{
				//this.inputVal();
				this.initEditData();
				this.initDomListener();

				xy_validator.initAutoValidator()

			},

			initEditData: function(){

				var userStr = this.getPassFromCookie();
				if(userStr){
					var userBean = userStr.split('|');
					$('#login_account').val(userBean[0]);
					$('#login_password').val(userBean[1]);
					if(userBean[1]){
						$('.J_password_remember').attr("checked","checked");
					}
				}

			},

			/**
			 * 初始化DOM元素监听
			 */
			initDomListener: function()
			{
				var me = this;

				$('#J_login').on('click',function(){
					me.loginFun();

				});

				$(document).on('keydown', function(e){
					if(e.keyCode == 13){
						$("#login_login_email").blur();
						//基础验证
						var validResult = xy_validator.autoValidator($("#v_login_emai"));
						if( !validResult ){
							return false;
						}
						$('#J_login').click();
					}
				});
				$("#login_login_email").on('blur',function(){
					//基础验证
					var validResult = xy_validator.autoValidator($("#v_login_emai"));
					if( !validResult ){
						return false;
					}
				});
			},
			/**
			 * 登录
			 */
			loginFun : function(){
				var me = this;
				if(me.isSumbit){
					return ;
				}
				//基础验证
				var validResult = xy_validator.autoValidator($("#login_form"));

				var passwordnot=$("#login_password").val();

				if( !validResult ){
					return false;
				}


				$("#J_login").text("正在登录...");
				//通过验证,获取bean
				var bean=xy_beanUtil.creatBean("login_",[
					{name:"account"},
					{name:"password"}
				])

				//发送数据到后台
				var url = "/user/login";
				me.isSumbit = true;
				$.post(url,bean, function(jsonObj){
					me.isSumbit = false;
					$("#J_login").text("登录");
					//登录失败
					if(jsonObj.code!== 0){
						$(".alert-login").text(jsonObj.msg).show();
						return false;
					}

					//登录成功
					var passInfo = bean.account+"|"+bean.password;
					if($(".J_password_remember").attr("checked")){
						me.savePassToCookie(passInfo);
					}else{
						//清除cookie内明文密码
						me.clearPassToCookie();
					}

					//清除上一次的用户信息
					$.cookie(location.host+"_userInfoResult","");

					window.location.href="/dashbord";
				});
			},
			/**
			 * 明文密码保存到cookie内
			 * @param passInfo
			 * @return
			 */
			savePassToCookie: function(passInfo) {
				$.cookie(location.host + "_password", passInfo, { expires: 365 });
			},

			/**
			 * 获取明文密码
			 * @return
			 */
			getPassFromCookie: function() {
				return $.cookie(location.host + "_password");
			},

			/**
			 * 清除cookie内明文密码
			 * @param passInfo
			 * @return
			 */
			clearPassToCookie: function() {
				$.cookie(location.host + "_password", "", {path: "/"});
			},

			inputVal: function(){
				$('.login-c input').each(function(){
					if($(this).val() !== ""){
						$(this).parent().find('span').hide();
					}else{
						$(this).blur();
						$(this).parent().find('span').show();
					}
				});
			}	,
			pwdFormatlg: function(str)
			{
				return (/^(?!\d+$)(?![A-Za-z]+$)[a-zA-Z0-9]{8,16}$/.test(str));
			}

		};

		return xy_login;

	})



