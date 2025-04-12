<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<jsp:include page="layout/base.jsp">
    <jsp:param name="title" value="login.required.title" />
    <jsp:param name="content" value="loginRequiredContent.jsp" />
</jsp:include> 