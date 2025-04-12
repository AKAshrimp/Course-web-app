<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="../layout/header.jsp">
    <jsp:param name="title" value="添加用户" />
</jsp:include>

<jsp:include page="addUserContent.jsp" />

<jsp:include page="../layout/footer.jsp" /> 