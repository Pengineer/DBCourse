<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
   <head>
  	<base href="<%=basePath %>">
  	<script type="text/javascript">
  		var basePath = "<%=basePath%>";
  	</script>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>数据库操作</title>

    <!-- Bootstrap -->
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
   
  </head>
  
  <body>
   	<div class="col-sm-6 col-sm-offset-3">
  	  <div class="row" style="background:#0077ff;">
	  	<h2 class="col-sm-8 col-sm-offset-2 text-center" style="color:#fff;font-weight:bold;">数据库安全理论与技术</h2>
	  </div>
	  
	  <input id="errorFlag" type="hidden" name="login_error" value="${flag}" />
	  <div class="row" style="margin:20px 10px 20px;">
	  	         用户等级：${sessionScope.securityLevel }<br>
	  	<div class="col-sm-10 col-sm-offset-1" style="background:#ccc;padding:30px;border-radius:10px;">
		  	
		  	<table>
		 		<form>
			 		<table class="table">
			 			<thead>
			 			<tr>
			 				<th>学生id</th>
			 				<th>姓名</th>
			 				<th>班级</th>
			 			</tr>
			 			</thead>
			 			<tbody>
			 				<c:forEach var="stu" items="${students }">
			 				<tr>
			 					<td>${stu.studentid }</td>
			 					<td>${stu.name }</td>
			 					<td>${stu.className }</td>
			 				</tr>
			 				</c:forEach> 
			 			</tbody>
			 		</table>
			 		<div class="form-horizontal">
				 		<div class="form-group">
						    <label class="col-sm-1 control-label">SQL</label>
						    <div class="col-sm-11">
						     	<input id="operate" class="form-control" type="text" placeholder="请输入sql语句" value="select * from t_student"/>
						    </div>
						</div>
					</div>
			 		<div class="text-center" style="margin-top:20px;">
				 		<input id="select" class="btn btn-primary" type="button" value="查询">
				 		<input id="add" class="btn btn-primary" type="button" value="添加">
				 		<input id="modify" class="btn btn-primary" type="button" value="修改">
				 		<input id="delete" class="btn btn-primary" type="button" value="删除">
			 		</div>
		 		</form>
  			</table>
  		</div>
  	</div>
  	</div>
  </body>
  <script src="js/jquery.js"></script>
  <script src="js/operate.js"></script>
</html>
