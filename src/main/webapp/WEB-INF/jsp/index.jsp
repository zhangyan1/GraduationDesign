<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isELIgnored="false"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<title>首页</title>
<link type="text/css" rel="stylesheet" href="style/reset.css">
<link type="text/css" rel="stylesheet" href="style/main.css">
<script type="text/javascript" src="js/jquery.min.js"></script>
<!-- <script type="text/javascript" src="js/myfocus-2.0.1.min.js"></script>  -->
<script type="text/javascript" src="js/mydate.js"></script>
<!-- <script type="text/javascript">
myFocus.set({
    id:'bannerBox',//焦点图盒子ID
    pattern:'mF_liuzg',//风格应用的名称
});
</script> -->
<script type="text/javascript">
	function queryList(pageNo){
		alert(pageNo);
	 	window.location.href="find?pageNo="+pageNo 
	}
	function querySList(){
		var pageNo = $("#sPageNo").val();
		alert(pageNo);
	 	window.location.href="find?pageNo="+pageNo 
	}
</script>
</head>
<body>
<div class="box">
	<div class="top">
		<div class = "topbar">
			<div class = "comWidth">
				<div class = "topleft">
				友情提醒：提高警惕，识破虚假招聘、避免上当受骗！
			</div>
			</div>
		</div>
		<div id="bannerBox"><!--焦点图盒子-->
  			<div class="pic"><!--内容列表(li数目可随意增减)-->
  				<ul>
        			<li><a href="#"><img src="images/6.jpg" thumb="" alt="标题1" text="详细描述1" /></a></li>
  				</ul>
  			</div>
		</div>
	</div>
	<div class = "mainBox comWidth">
		<div class = "serch ">
			<form class="search-form" action="find" >
				<div class = "serch-left">
				<input type="hidden" name=pageNo value="1"/>
					&nbsp;&nbsp;&nbsp;&nbsp;<b>日期</b><input type="text" class = "input1" name="date" onfocus="MyCalendar.SetDate(this)" value="${pageObject.params.date}"/>
					<select name="school" id="keyid" ">
						<option value=" ">--请选择--</option>
						<c:forEach var="Num" items="${pageObject.schoolList}">
							<option value ="${Num}">${Num}</option>
  						</c:forEach>
					</select>
				</div>
				<div class = "serch-right">
					<input type="text" name = "name"  class = "input3" placeholder="输入公司名称" value="${pageObject.params.name}"/>
					<input type ="submit" class="search-button" value=""/>
				</div>
			</form>
		</div>
		<div class = "content ">
			<ul class="activity1">
				<c:forEach var="workInfo" items="${pageObject.rslist}">
				<li><a href="${workInfo.url}">
				<c:choose>     
					    	<c:when test="${fn:length(workInfo.name)>25}">         
					    	${fn:substring(workInfo.name,0,25)}...    
					    	</c:when>
					    	<c:otherwise>       
					    	${workInfo.name}   
					    	</c:otherwise> 
				 </c:choose>
				 </a><span>${workInfo.realDate }</span><b>${workInfo.school }</b></li>
				</c:forEach>
			</ul>
			<div class="list-page">
				<ul><li><p>共有${pageObject.totalPages}页,这是第${pageObject.pageNo}页</p></li>
					<li><a href="javascript:queryList('${pageObject.topPageNo}')">首页</a></li>
					<c:if test="${pageObject.pageNo!=1}">
					<li><a href="javascript:queryList('${pageObject.pageNo-1}')">上一页</a></li>
					</c:if>
					<c:if test="${pageObject.pageNo<pageObject.totalPages}">
					<li><a href="javascript:queryList('${pageObject.pageNo+1}')">下一页</a></li>
					</c:if>
					<li><a href="javascript:queryList('${pageObject.totalPages}')">最后一页</a></li>
				<li><p>跳转到:
				<select name="pageNo" id="sPageNo" >
					<c:forEach var="Num" items="${pageObject.pageList}">
						<option value ="${Num}">${Num}</option>
  					</c:forEach>
  				</select></p></li>
				<span><li><a href="javascript:querySList()">Go</a></span>
				</ul>
			</div>
		</div>
	</div>
	<div class = "footer">
		<p>
		${url}|  版权隐私  |  联系站长  |  免责声明  |  cocss.com  |  ued资源分享  |  站点统计  |  加入收藏  | <a href="#">网站后台</a><br>
		Copyright@2015-2018 Key Laboratory of Ecological Remediation and Safe Utilization of Heavy Metal-Polluted Soils,College of Hunan Province<br />
		版权所有：湖南科技大学&middot;计算机科学与工程学院<br />
	</p>
	</div>
</div>
</body>
</html>