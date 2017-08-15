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
    "validator",
    "util",
    "alertdialog"
],function(xy_validator, msg,jquerycookie,xy_beanUtil,xy_validator,xy_util){
    var xy_register = {

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
                $('#register_account').val(userBean[0]);
                $('#register_password').val(userBean[1]);
            }

        },

        /**
         * 初始化DOM元素监听
         */
        initDomListener: function()
        {
            var me = this;

            $('#J_register').on('click',function(){
                me.registerFun();

            });

            $(document).on('keydown', function(e){
                if(e.keyCode == 13){
                    $("#register_account").blur();
                    //基础验证
                    var validResult = xy_validator.autoValidator($("#login_form"));
                    if( !validResult ){
                        return false;
                    }
                    $('#J_register').click();
                }
            });
            $("#register_account").on('blur',function(){
                //基础验证
                var validResult = xy_validator.autoValidator($("#login_form"));
                console.log(validResult)
                if( !validResult ){
                    return false;
                }
            });
        },
        /**
         * 登录
         */
        registerFun : function(){
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


            $("#J_register").text("正在注册...");
            //通过验证,获取bean
            var bean=xy_beanUtil.creatBean("register_",[
                {name:"account"},
                {name:"password"},
                {name:"username"}
            ])

            bean.inviter = xy_util.getUrlParms()["id"];

            //发送数据到后台
            var url = "/user/regist";
            me.isSumbit = true;
            $.post(url,bean, function(jsonObj){
                me.isSumbit = false;
                $("#J_register").text("注册");
                //登录失败
                if(jsonObj.code!== 0){
                    $(".alert-login").text(jsonObj.msg).show();
                    return false;
                }

                //登录成功
                var passInfo = bean.account+"|"+bean.password;

                //清除上一次的用户信息
                $.cookie(location.host+"_userInfoResult","");
                jTip("注册成功,请登录！")
                setTimeout(function(){
                    window.location.href="/login";
                },2000)
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

    return xy_register;
})


