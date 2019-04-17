# wechatSubAuth

### 公众号扫码登陆
帮同学做的微信公众号扫码登录  
当用户在前台向服务端发出请求的时候附带一个“token”  
若token为空或验证不通过，返回一个临时二维码url和token
若有时间会继续完善，修改controller层让其可以作为一个独立的服务

前端调用方式示例  
首先从cookie中取出token  
之后向后端发出请求  
```
layui.use(['jquery','layer'],function () {
        var $ = layui.jquery;

        $('#download').click(function () {
            downloadRequest();
        })

        function downloadRequest(){
            var token = getCookie('token');
            if(token == null){
                token = '';
            }
            var data = {
                resourceUrl:$('#resourceUrl').val(),
                token:token
            }

            $.ajax({
                url:"/wechat/download/wenku",
                type:"post",
                dataType:"json",
                contentType: "application/json",
                data:JSON.stringify(data),
                success:function (respone) {
                    if(respone.status == "100001"){
                        console.log(respone.data.token)
                        setTokenToCookie(respone.data.token)
                        $('#qrcode').attr('src','https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket='+respone.data.ticket);
                        $('#respone').val(data);
                    }else if(respone.status == "100002"){
                        layer.msg(respone.message);
                        console.log(respone.data.url)
                        window.open(respone.data.url)
                        $('#click').text("若未开始下载，请点击");
                        $('#click').attr('href',respone.data.url);
                    }else{
                        layer.msg("未知错误");
                    }
                }
            })}

    })
 function setTokenToCookie(value){
        console.log(value+'SETCOOKIE'+document.cookie)
        document.cookie = 'token='+value;
        console.log('SETCOOKIEafter'+document.cookie)
    }

    function getCookie(objName){//获取指定名称的cookie的值
        console.log('getCOOKIE'+document.cookie)
        var arrStr = document.cookie.split("; ");
        for (var i = 0; i < arrStr.length; i++) {
            var temp = arrStr[i].split("=");
            if (temp[0] == objName){
                return temp[1];
            }
        }
    }
```
