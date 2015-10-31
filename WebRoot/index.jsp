
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html lang="zh-CN">
  <head>
  	<base href="<%=basePath %>">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>数据库登录</title>

    <!-- Bootstrap -->
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
   
  </head>
  <body>
  <div class="col-sm-6 col-sm-offset-3">
  	  <div class="row" style="background:#0077ff;">
	  	<h2 class="col-sm-4 col-sm-offset-4 text-center" style="color:#fff;font-weight:bold;">数据库安全课程</h2>
	  </div>
	  
	  <input id="errorFlag" type="hidden" name="login_error" value="${flag}" />
	  <div class="row" style="margin:20px 10px 20px;">
	  	<div class="col-sm-8 col-sm-offset-2">
	  		<form action="login.action"  style="background:#ccc;padding:30px;border-radius:10px;">
			  <div class="form-group">
			    <label for="exampleInputEmail1">用户名</label>
			    <input type="text" class="form-control" name="username" placeholder="请输入">
			  </div>
			  <div class="form-group">
			    <label for="exampleInputPassword1">密码</label>
			    <input type="password" class="form-control" name="password" placeholder="请输入">
			  </div>
			  <div id="errorInfo" style="color:red;padding-left:5px"></div>
			  <div class="text-center">
			    <button type="submit" class="btn btn-default">登录</button>
			  </div>
			  
			</form>
	  	</div>
	  </div>
	  <%--<div class="row" style="margin-top:200px;">
	  	<h4 class="text-center col-sm-12 " style="font-weight:bold;margin:0 auto;">
	  		学院：电信学院&nbsp;&nbsp;&nbsp;&nbsp;
	  		姓名：彭亮&nbsp;&nbsp;&nbsp;&nbsp;
	  		学号：M201471812	
	  	</h4>
	  </div>
  --%></div>
  
  
   
    <script src="js/jquery.js"></script>
    <script src="js/main.js"></script>
  </body>
</html>